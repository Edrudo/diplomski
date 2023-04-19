//
// Copyright (C) 2009 United States Government as represented by the
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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.NEW;
import gov.nasa.jpf.util.MethodSpec;
import gov.nasa.jpf.vm.StackFrame;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * a listener that checks for calls with reference arguments that are not
 * allowed to be passed into specified methods
 *
 * this is mostly an example for a class of scope based, instance specific
 * properties
 */
public class ArgChecker extends ListenerAdapter {

  protected static Logger log = JPF.getLogger("gov.nasa.jpf.aprop");

  static final String NO_ARG = "gov.nasa.jpf.annotation.NoArg";


  /**
   * which methods are not allowed to have reference arguments of the annotated type
   */
  static class ForbiddenMethods {
    LinkedList<MethodSpec> methodSpecs = new LinkedList<MethodSpec>();

    public void add (MethodSpec ms){
      methodSpecs.add(ms);
    }
    public void addAll (List<MethodSpec> l){
      methodSpecs.addAll(l);
    }
    public List<MethodSpec> getMethodSpecs(){
      return methodSpecs;
    }
  }
  
  ForbiddenMethods getForbiddenMethods (ClassInfo ci){
    return ci.getAttr(ForbiddenMethods.class);
  }
  void setMethodSpecs (ClassInfo ci, ForbiddenMethods methodSpecs){
    ci.addAttr( methodSpecs);
  }

  public void instructionExecuted (VM vm, ThreadInfo ti, Instruction nextInstruction, Instruction insn){
    // attach attributes to instances of annotated types
    if (insn instanceof NEW && insn.isCompleted(ti)){
      StackFrame frame = ti.getTopFrame();
      int objRef = frame.peek();
      ElementInfo ei = ti.getElementInfo(objRef);
      ForbiddenMethods a = getForbiddenMethods(ei.getClassInfo());
      if (a != null){
        ei = ei.getModifiableInstance();
        // in apps with little data flow, it would be more efficient to set this
        // as an operand attribute, but we want to demonstrate how type based
        // instance attributes can be handled
        ei.setObjectAttr(a);
      }
    }
  }


  public void executeInstruction (VM vm, ThreadInfo ti, Instruction insn){

    // check if this is a forbidden call
    if (insn instanceof JVMInvokeInstruction){
      JVMInvokeInstruction call = (JVMInvokeInstruction)insn;

      /*if (call.hasAttrRefArgument(ti, ForbiddenMethods.class)){ // check first if its worth the hassle
        MethodInfo mi = call.getInvokedMethod();
        Object[] args = call.getArgumentValues(ti);

        for (int i=0; i<args.length; i++){
          Object arg = args[i];
          if (arg != null && arg instanceof ElementInfo){
            ForbiddenMethods attr = ((ElementInfo)arg).getObjectAttr(ForbiddenMethods.class);
            if (attr != null){
              for (MethodSpec mspec : attr.getMethodSpecs()) {
                if (mspec.matches(mi)) {
                  if (mspec.isMarkedArg(i)) {
                    Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                            "call of " + mi.getFullName() + " with argument: " + arg + 
                            " violates method spec \"" + mspec.getSource()+'"');
                    ti.setNextPC(nextPc);
                    return;
                  }
                }
              }
            }
          }
        }
      }*/
    }
  }

  // factored out so that we can easily introduce specialized versions
  protected ForbiddenMethods getAttr (ClassInfo ci){
    AnnotationInfo ai = ci.getAnnotation(NO_ARG);
    if ( ai != null){
      String[] specs = ai.getValueAsStringArray();
      ForbiddenMethods a = new ForbiddenMethods();
      for (String s : specs){
        MethodSpec ms = MethodSpec.createMethodSpec(s);
        if (ms != null){
          a.add(ms);
          return a;

        } else {
          log.warning("illegal @NoArg method spec ignored: " + s);
        }
      }
    }

    return null;
  }

  public void classLoaded (VM vm, ClassInfo ci){

    // check if we have an annotation ourself
    ForbiddenMethods a = getAttr(ci);
    if (a != null){
      setMethodSpecs( ci, a);
    }

    // check if our superclass has one
    ClassInfo sci = ci.getSuperClass();
    if (sci != null){
      ForbiddenMethods sa = getForbiddenMethods(sci);
      if (sa != null) {
        if (a == null) {
          setMethodSpecs(ci, sa);
        } else {
          a.addAll(sa.getMethodSpecs()); // it's already added
        }
      }
    }

    // same for our interfaces
    for (ClassInfo ici : ci.getInterfaceClassInfos()){
      ForbiddenMethods ia = getForbiddenMethods(ici);
      if (ia != null){
        if (a == null) {
          setMethodSpecs(ci, ia);
        } else {
          a.addAll(ia.getMethodSpecs()); // it's already added
        }
      }
    }
  }

  // <2do> would have to deal with OverrideNoArg here

}
