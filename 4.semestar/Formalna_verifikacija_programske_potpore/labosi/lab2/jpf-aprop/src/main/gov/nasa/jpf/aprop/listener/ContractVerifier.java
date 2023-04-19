//
// Copyright (C) 2007 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.aprop.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.InfoObject;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;
import gov.nasa.jpf.aprop.Contract;
import gov.nasa.jpf.aprop.ContractAnd;
import gov.nasa.jpf.aprop.ContractContext;
import gov.nasa.jpf.aprop.ContractSpecLexer;
import gov.nasa.jpf.aprop.ContractSpecParser;
import gov.nasa.jpf.aprop.EmptyContract;
import gov.nasa.jpf.aprop.Satisfies;
import gov.nasa.jpf.aprop.VarLookup;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * listener tool to evaluate pre-, post-conditions and invariants given as
 * Java annotations
 */
public class ContractVerifier extends ListenerAdapter {

  protected static Logger log = JPF.getLogger("gov.nasa.jpf.aprop");

  static final String PRECOND_ANNOTATION = "gov.nasa.jpf.annotation.Requires";
  static final String POSTCOND_ANNOTATION = "gov.nasa.jpf.annotation.Ensures";
  static final String INVARIANT_ANNOTATION = "gov.nasa.jpf.annotation.Invariant";
  
  // we need separate types for contracts associated with methods since we store them as attrs
  static abstract class Cond {
    Contract contract;
    Cond(Contract contract){
      this.contract = contract;
    }
    Contract getContract() { return contract; }
  }
  static class PreCond extends Cond {
    PreCond (Contract contract){
      super(contract);
    }
  }
  static class PostCond extends Cond {
    PostCond (Contract contract){
      super(contract);
    }
  }
  
  static class PostCondContext {
    VarLookup lookupPolicy;
    Contract contract;
    StackFrame frame;

    PostCondContext (StackFrame f, Contract c, VarLookup p){
      frame = f;
      contract = c;
      lookupPolicy = p;
    }
  }

  ContractContext ctx = new ContractContext(log);


  public ContractVerifier (Config conf){
    Satisfies.init(conf);
  }

  public void classLoaded(VM vm, ClassInfo ci) {
    getInvariantContract(ci);
  }

  ClassInfo getClassInfo (VM vm, StackFrame frame) {
    MethodInfo mi = frame.getMethodInfo();

    if (mi.isStatic()) {
      return mi.getClassInfo();
    } else {
      int thisRef = frame.getThis();
      ElementInfo ei = vm.getElementInfo(thisRef);
      return ei.getClassInfo();
    }
  }

  public void executeInstruction (VM vm, ThreadInfo ti, Instruction insn){

    // we do eval preConds post-exec, so that we catch
    // threading related violations (field values might change
    // between pre- and post-exec of the invoke, due to synchronization)

    if (insn instanceof JVMReturnInstruction) { // eval pending postconditions
      // we need to do this pre-exec so that we still get the right stack snapshot
      StackFrame frame = ti.getTopFrame();
      MethodInfo mi = frame.getMethodInfo();

      ctx.setContextInfo(mi, ti);

      if (!frame.isDirectCallFrame()) { // no contracts on synthetic methods

        //--- check postconditions
        PostCondContext pc = frame.getFrameAttr(PostCondContext.class);
        if (pc != null) {
          Contract postCond = pc.contract;
          VarLookup lookupPolicy = new VarLookup.PostCond(ti, (JVMReturnInstruction)insn, pc.lookupPolicy);

          if (!postCond.holdsAll(lookupPolicy)) {
            Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                                                              getErrorMessage(postCond, lookupPolicy, "postcondition", "AND"));
            ti.setNextPC(nextPc);
            return;
          }
        }

        //--- check class invariants
        // <2do> - this does not yet handle value lookup and reporting correctly
        ClassInfo ci = getClassInfo(vm, frame);
        if (ci != null) {

          if (mi.isStatic()) {
            // <2do> add static class invariants

          } else if (!mi.isCtor()){ // Hmm, do we really want to skip ctors?
            Contract inv = ci.getAttr(Contract.class);
            if (inv != null && inv.hasNonEmptyContracts() ) {
              VarLookup lookupPolicy = new VarLookup.Invariant(ti);

              if (!inv.holdsAll(lookupPolicy)) {
                Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                                                                  getErrorMessage(inv,lookupPolicy,"invariant", "AND"));
                ti.setNextPC(nextPc);
                return;
              }
            }
          }
        }
      }
    }
  }

  public void instructionExecuted (VM vm, ThreadInfo ti, Instruction nextInsn, Instruction insn) {

    // check for pending post conditions - we need to do this *after* execution
    // because we need the new StackFrame
    if (insn instanceof JVMInvokeInstruction){
      JVMInvokeInstruction call = (JVMInvokeInstruction)insn;
      MethodInfo mi = call.getInvokedMethod();

      if (mi == null){
        return; // will cause exception anyways
      }
      
      ctx.setContextInfo(mi, ti);

      // check preconditions, but not before we actually enter the method
      // (which might be synchronized, and the preconds might be violated
      // at the time of execution, or when we first see the call)
      Contract preCond = getPreCondContract(mi);
      if (!preCond.isEmpty()){ // we can evaluate right away
        //printPreCondition(preCond);
        VarLookup lookup = new VarLookup.PreCond(ti, call);

        if (!preCond.holdsAny(lookup)){
          Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                                                          getErrorMessage(preCond,lookup,"precondition","OR"));
          ti.setNextPC(nextPc);
        }
      }

      // remember postcond 'old' - values
      Contract postCond = getPostCondContract(mi);
      if (postCond.hasNonEmptyContracts()) { // store the postcond, together with 'old' values
        // <2do> native methods ??

        VarLookup lookup = new VarLookup.PostCondPreExec(ti);
        postCond.saveOldValues(lookup);
        lookup.purgeVars(); // so that we don't use cached values for fields and locals

        StackFrame frame = ti.getTopFrame();
        PostCondContext pc = new PostCondContext(frame,postCond,lookup);
        
        // <2do> that's not going to work since the frame identify can change if there is a CG in between
        // (when the Ti memento is stored it resets the frame change flags
        frame.addFrameAttr(pc);
      }
    }
  }

  String getErrorMessage (Contract c, VarLookup policy, String type, String combinator) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(type);

    sb.append(" violated: ");
    sb.append(c.getErrorMessage(policy,combinator));

    HashMap<Object,Object> values = policy.getCache();
    if (!values.isEmpty()) {
      sb.append(", values={");

      int i=0;
      for (Map.Entry<Object,Object> e : values.entrySet()){
        if (i++ > 0){
          sb.append(',');
        }
        sb.append(e.getKey());
        sb.append('=');

        Object v = e.getValue();
        if (v instanceof ElementInfo && ((ElementInfo)v).isStringObject()){
           String s = ((ElementInfo)v).asString();
           if (s != null){
             sb.append('"');
             sb.append(s);
             sb.append('"');
           } else {
             sb.append(v);
           }
        } else {
          sb.append(v);
        }
      }
      sb.append('}');
    }

    return sb.toString();
  }

  Contract loadContract (String annotation, InfoObject iobj) {
    Contract contract = null;

    AnnotationInfo ai = iobj.getAnnotation(annotation);
    if (ai != null){  // Ok, we have an unparsed contract spec
      Object v = ai.value();

      if (v instanceof String) {
        contract = parseContractSpec((String)v);

      } else if (v instanceof Object[]) {
        for (Object s : (Object[])v) {
          Contract c = parseContractSpec(s.toString());
          if (contract == null) {
            contract = c;
          } else {
            contract = new ContractAnd(contract, c);
          }
        }
      }

    } else {                // remember that we've checked this
      contract = new EmptyContract(); // can't be a shared object because of super class methods
    }

    return contract;
  }
  
  Contract getPreCondContract (MethodInfo mi){
    Cond cond = mi.getAttr( PreCond.class);
    Contract contract;

    if (cond == null){
      contract = loadContract(PRECOND_ANNOTATION, mi);
      cond = new PreCond(contract);
      mi.addAttr(cond);
    } else {
      contract = cond.getContract();
    }

    // now check for super contracts
    MethodInfo smi = mi.getOverriddenMethodInfo();
    if (smi != null){
      contract.setSuperContract( getPreCondContract(smi));
    }

    return contract;
  }
  
  Contract getPostCondContract (MethodInfo mi) {
    Cond cond = mi.getAttr( PostCond.class);
    Contract contract;

    if (cond == null){
      contract = loadContract(POSTCOND_ANNOTATION, mi);
      cond = new PostCond(contract);
      mi.addAttr(cond);
    } else {
      contract = cond.getContract();
    }

    // now check for super contracts
    MethodInfo smi = mi.getOverriddenMethodInfo();
    if (smi != null){
      contract.setSuperContract( getPostCondContract(smi));
    }

    return contract;
  }
  
  
  Contract getInvariantContract (ClassInfo ci) {
    Contract contract = ci.getAttr(Contract.class);

    if (contract == null){
      contract = loadContract(INVARIANT_ANNOTATION, ci);
      ci.addAttr(contract);
    }

    ClassInfo sci = ci.getSuperClass();
    if (sci != null) {
      contract.setSuperContract( getInvariantContract(sci));
    }

    return contract;
  }

  Contract parseContractSpec (String spec){
    ANTLRStringStream input = new ANTLRStringStream(spec);
    ContractSpecLexer lexer = new ContractSpecLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    ContractSpecParser parser = new ContractSpecParser(tokens, ctx);

    try {
      Contract contract = parser.contract();
      return contract;

    } catch (RecognitionException rx){
      error("spec did not parse: " + rx);
      return null;
    }
  }

  void error (String msg){
    System.err.println(msg);
  }
}
