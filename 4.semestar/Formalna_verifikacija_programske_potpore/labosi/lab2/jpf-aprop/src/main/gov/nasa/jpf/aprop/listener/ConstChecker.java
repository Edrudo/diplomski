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
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.InfoObject;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.Trace;
import java.util.HashSet;

/**
 * listener to check for @Const annotations. A method that is marked @Const
 * is not allowed to modify any of it's class or instance fields.
 *
 * This goes beyond 'final' fields because it
 *  - can be applied to method scopes
 *  - applies to the whole object (all fields)
 *  - allows deep modification from ctors (i.e. calling methods from the
 *    ctor that modify fields)
 *  - can be set/unset dynamically
 * 
 * If the property is violated, this listener throws an AssertionError
 * 
 * <2do> so far, we only support 'deep' const, i.e. any PUTFIELD
 * between enter and exit from the @Const method is treated as a 
 * violation
 *
 * This implementation uses a state extension to store the 'const' execution
 * context, which constists of the current 'const' object/class references
 * together with their const count. This does not store the methods where
 * objects became subject to checking (like the SandBoxChecker does), and is
 * probably less efficient than to keep the context transient (recompute
 * at beginning of transition, and only update within this transition w/o
 * storing it afterwards), but it gives us more control over when an object
 * becomes const, which might not be a static condition (e.g. when "freezing"
 * an object after putting it into a HashMap)
 */
public class ConstChecker extends ListenerAdapter {
  
  // <2do> should really use pools for ConstObj, ConstContext 
  static class ConstObj {
    int ref;
    int count;
    
    ConstObj (int r, int c){
      ref = r;
      count = c; 
    }
    
    ConstObj inc() {
      return new ConstObj(ref, count+1);
    }
    
    ConstObj dec() {
      assert count > 0;
      return new ConstObj(ref, count-1);
    }
  }
  
  static class ConstContext {
    // probably not the best implementation - we assume for now that const
    // methods are rather small and short living.
    ConstObj[] consts;
    
    ConstContext() {
      consts = new ConstObj[0];
    }
    
    ConstContext (ConstObj[] c) {
      consts = c;
    }
    
    // handle statics via class object reference, i.e. two ConstObj entries per instance
    
    ConstContext incRef (int ref) {
      for (int i=0; i<consts.length; i++) {
        if (consts[i].ref == ref) { // replace ConstObj
          ConstObj[] newConsts = consts.clone();
          newConsts[i] = consts[i].inc();
          return new ConstContext(newConsts);
        }
      }
      
      // append a new ConstObj
      ConstObj[] newConsts = new ConstObj[consts.length+1];
      if (consts.length>0) {
        System.arraycopy(consts,0,newConsts,0,consts.length);
      }
      newConsts[consts.length] = new ConstObj(ref,1);
      return new ConstContext(newConsts);      
    }
    
    ConstContext decRef (int ref) {
      for (int i=0; i<consts.length; i++) {
        if (consts[i].ref == ref) {
          ConstObj[] newConsts;
          if (consts[i].count > 1) {
            newConsts = consts.clone();
            newConsts[i] = consts[i].dec();
          } else {
            int l = consts.length-1;
            if (l > 0) {
              newConsts = new ConstObj[l];
              if (i>0) {
                System.arraycopy(consts,0,newConsts,0,i);
              }
              if (i<l) {
                System.arraycopy(consts,i+1,newConsts,i,l-i);
              }
            } else {
              newConsts = new ConstObj[0];
            }
          }
          return new ConstContext(newConsts);
        }
      }
      
      return this;
    }

    ConstContext incRefs (int ref1, int ref2) {
      // <2do> fix this for efficiency
      ConstContext c = this;
      if (ref1 != -1) {
        c = c.incRef(ref1);
      }
      if (ref2 != -1) {
        c = c.incRef(ref2);
      }
      return c;
    }
    ConstContext decRefs (int ref1, int ref2) {
      // <2do> fix this for efficiency
      ConstContext c = this;
      if (ref1 != -1) {
        c = c.decRef(ref1);
      }
      if (ref2 != -1) {
        c = c.decRef(ref2);
      }
      return c;
    }
    
    
    boolean includes (int ref) {
      for (int i=consts.length-1; i>=0; i--) {
        if (consts[i].ref == ref) {
          return true;
        }
      }
      return false;
    }
  }
  
  // another peudo trace that really is just a backtrackable state extension
  Trace<ConstContext> constContext = new Trace<ConstContext>();
  
  // how we keep track of what const objects we have to check
  enum ConstInfo { CLASS, METHOD, FIELD };
  
  void makeConstClass (ClassInfo ci){
    ci.addAttr(ConstInfo.CLASS);
  }
  boolean isConstClass (ClassInfo ci){
    return ci.hasAttrValue(ConstInfo.CLASS);
  }
  void makeConstMethod (MethodInfo mi){
    mi.addAttr(ConstInfo.METHOD);
  }
  boolean isConstMethod (MethodInfo mi){
    return mi.hasAttrValue(ConstInfo.METHOD);
  }
  void makeConstField (FieldInfo fi){
    fi.addAttr(ConstInfo.FIELD);
  }
  boolean isConstField (FieldInfo fi){
    return fi.hasAttrValue(ConstInfo.FIELD);
  }


  public ConstChecker (Config conf) {
    // <2do> we should probably configure if const instance also includes const static
    constContext.addOp(new ConstContext());
  }

  boolean isImmutable (InfoObject o) {
    AnnotationInfo[] ai = o.getAnnotations();
    if (ai != null){
      for (int i=0; i<ai.length; i++){
        String name = ai[i].getName();
        if ("javax.annotation.concurrent.Immutable".equals(name) ||
                "gov.nasa.jpf.annotation.Const".equals(name)){
          return true;
        }
      }
    }

    return false;
  }

  boolean isConst (InfoObject o) {
    AnnotationInfo[] ai = o.getAnnotations();
    if (ai != null){
      for (int i=0; i<ai.length; i++){
        String name = ai[i].getName();
        if ("gov.nasa.jpf.annotation.Const".equals(name)){
          return true;
        }
      }
    }

    return false;
  }


  //--- update the constContext

  public void instructionExecuted (VM vm, ThreadInfo ti, Instruction nextInsn, Instruction insn) {
  
    if (!insn.isCompleted(ti)) {
      return;
    }

    // <2do> this doesn't handle exceptions yet

    if (insn instanceof JVMInvokeInstruction) { // check for new const context
      JVMInvokeInstruction call = (JVMInvokeInstruction)insn;
      MethodInfo mi = call.getInvokedMethod();
      if (!mi.isDirectCallStub()) {
        if (isConstMethod(mi)) {
          ConstContext cc = constContext.getLastOp();

          if (!mi.isStatic()) {
            int objRef = ti.getThis(); // we are already on the stack
            int clsRef = ti.getClassInfo(objRef).getClassObjectRef();

            // const instance methods are not allowed to mod either static or instance fields
            constContext.addOp(cc.incRefs(objRef, clsRef));
          } else {
            int clsRef = mi.getClassInfo().getClassObjectRef(); 
            constContext.addOp(cc.incRef(clsRef));
          }
        }
      }
      
    } else if (insn instanceof JVMReturnInstruction) { // check for const context update
      JVMReturnInstruction ret = (JVMReturnInstruction)insn;
      MethodInfo mi = insn.getMethodInfo();
      if (!mi.isDirectCallStub()) {
        if (isConstMethod(mi)) {
          ConstContext cc = constContext.getLastOp();

          if (cc != null) {
            if (!mi.isStatic()) {
              int objRef = ret.getReturnFrame().getThis(); // it's already off the stack
              int clsRef = ti.getClassInfo(objRef).getClassObjectRef();
              constContext.addOp(cc.decRefs(objRef, clsRef));
            } else {
              int clsRef = mi.getClassInfo().getClassObjectRef(); 
              constContext.addOp(cc.decRef(clsRef));          
            }
          }
        }
      }
      
    } else if (insn instanceof PUTFIELD) { // check for illegal instance field writes
      PUTFIELD put = (PUTFIELD)insn;
      int objRef = put.getLastThis();
      FieldInfo fi = put.getFieldInfo();
      ClassInfo ci = ti.getClassInfo(objRef);
      
      if (isConstClass(ci)){
        if (!ti.isCtorOnStack(objRef)) {
          Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                  "write of instance field outside constructor of immutable class: " + fi);
          ti.setNextPC(nextPc);
          return;
        }        
        
      } else if (isConstField(fi)) {
        if (!ti.isCtorOnStack(objRef)) {
          Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                  "write of const instance field outside constructor: " + fi);
          ti.setNextPC(nextPc);
          return;
        }

      } else { // check for a const method on the stack
        ConstContext cc = constContext.getLastOp();
        if (cc != null) {
          // check the instance
          if (cc.includes(objRef)) {
            if (!ti.isCtorOnStack(objRef)) {
              Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                      "instance field write within const context: " + fi);
              ti.setNextPC(nextPc);
              return;
            }
          }
        }
      }
      
    } else if (insn instanceof PUTSTATIC) { // check for illegal static field writes
      PUTSTATIC put = (PUTSTATIC)insn;
      FieldInfo fi = put.getFieldInfo();
      ClassInfo ci = fi.getClassInfo();

      if (isConstField(fi)){
        if (!ti.isClinitOnStack(ci)){
          Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                  "write of const static field outside class initialization: " + fi);
          ti.setNextPC(nextPc);
          return;
        }

      } else {  // check for const method on stack
        int clsRef = ci.getClassObjectRef();
        ConstContext cc = constContext.getLastOp();

        if (cc != null) {
          if (cc.includes(clsRef)) {
            Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                    "static field write within const context: " + fi);
            ti.setNextPC(nextPc);
            return;
          }
        }
      }
    }
  }
  
  public void stateAdvanced (Search search) {
    constContext.stateAdvanced(search);
  }
  
  public void stateBacktracked (Search search) {
    constContext.stateBacktracked(search);
  }

  public void stateStored (Search search) {
    constContext.stateStored(search);
  }

  public void stateRestored (Search search) {
    constContext.stateRestored(search);
  }

  public void classLoaded (VM vm, ClassInfo ci){

    if (isImmutable(ci)){
      makeConstClass(ci);
      // we could just bail here
    }

    for (MethodInfo mi : ci){
      if (isConst(mi)){
        makeConstMethod(mi);
      }
    }

    for (FieldInfo fi : ci.getDeclaredStaticFields()){
      if (isConst(fi)){
        makeConstField(fi);
      }
    }

    for (FieldInfo fi : ci.getDeclaredInstanceFields()) {
      if (isConst(fi)) {
        makeConstField(fi);
      }
    }
  }


}
