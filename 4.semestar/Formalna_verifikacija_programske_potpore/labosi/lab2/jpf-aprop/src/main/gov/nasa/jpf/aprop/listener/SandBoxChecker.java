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

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.ANEWARRAY;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.ARRAYLENGTH;
import gov.nasa.jpf.jvm.bytecode.ArrayStoreInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.NEW;
import gov.nasa.jpf.jvm.bytecode.NEWARRAY;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;
import java.util.HashSet;

/**
 * check if @SandBox instances modify any external fields
 *
 * The basic check is on PUTFIELD,PUTSTATIC and ArrayStore operations that
 * are performed while there is at least one SandBox method on the stack. The
 * field owner has to be either this SandBox, or it had to be created by
 * a method of this SandBox, w/o having leaked out of a sandboxed context between
 * creation and the write operation.
 *
 * This is downward transitive, i.e. writes to objects directly or indirectly
 * created from the same sandbox context are allowed (e.g. to allow SandBox objects
 * to use containers)
 *
 * While SandBox objects can nest, this is not upwards transitive, i.e. we
 * just have to check the innermost sandbox context for violations.
 *
 * A static method of a SandBox type is only allowed to change static fields
 * of this class.
 *
 * An instance method of a SandBox type can change its own instance fields and
 * (optionally) static fields of its class.
 *
 * According to this scheme, there are three types of objects:
 *
 *  (a) sandbox objects (identified by their types/reference values)
 *  (b) objects created from within a sandbox (marked with attributes holding the
 *      ref value of the creating sandbox context)
 *  (c) all others
 *
 * If a marked (b) object is returned from a sandbox context (topmost sandbox
 * method on call stack), it is recursively unmarked, which can be an expensive
 * operation.
 *
 * SandBoxes are also downwards transitive with respect to types, i.e. all types
 * extending or implementing SandBox types are SandBoxes themselves.
 * The inverse is not true - super-types of SandBoxes are not SandBoxes. This
 * means we have to check for concrete object types of instance methods, not the
 * types thet implement the method
 */
public class SandBoxChecker extends ListenerAdapter {

  enum ClassAttr { SANDBOX };
  
  void makeSandBoxed (ClassInfo ci){
    ci.addAttr(ClassAttr.SANDBOX);
  }
  boolean isSandBoxed (ClassInfo ci){
    return ci.hasAttrValue(ClassAttr.SANDBOX);
  }
  
  static class SBContext {
    StackFrame frame;
    int sbid;      // instance or class sandbox id
    int sbidCls;   // class sandbox id (== sbid if this is a static context)
    SBContext prev;

    SBContext (StackFrame frame, int sbid, int sbidCls, SBContext prev){
      this.frame = frame;
      this.sbid = sbid;
      this.sbidCls = sbidCls;
      this.prev = prev;
    }

    // only used for error reporting, i.e. not performance critical
    String getSandbox (ThreadInfo ti){
      MethodInfo mi = frame.getMethodInfo();
      if (mi.isStatic()){
        return mi.getClassInfo().getName();
      } else {
        ElementInfo ei = ti.getElementInfo(frame.getThis());
        return ei.toString();
      }
    }
  }

  static class Attr {
    int owner;

    Attr(int owner){
      this.owner = owner;
    }

    @Override
    public int hashCode() {
      return owner;
    }
  }
  
  
  
  SBContext ctx;

  void updateContext (ThreadInfo ti, StackFrame frame){
    if (frame.isDirectCallFrame()){
      return;
    }

    MethodInfo mi = frame.getMethodInfo();

    if (mi.isStatic()) {
      int id = getSandBoxId(mi.getClassInfo());
      if (id != 0) {
        ctx = new SBContext(frame, id, id, ctx);
      }

    } else {
      ElementInfo ei = ti.getElementInfo(frame.getThis());
      int id = getSandBoxId(ei);
      if (id != 0) {
        int idCls = getSandBoxId(ei.getClassInfo());
        ctx = new SBContext(frame, id, idCls, ctx);
      }
    }
  }

  void setContext (ThreadInfo ti){
    ctx = null;

    // <2do> this is processed lifo - is this what we want? (was fifo)
    for (StackFrame frame : ti.invokedStackFrames()){
      updateContext(ti, frame);
    }
  }

  boolean isNewContext (int sbid, SBContext ctxPrev){
    for (; ctxPrev != null; ctxPrev = ctxPrev.prev){
      if (ctxPrev.sbid == sbid){
        return false;
      }
    }
    return true;
  }

  int getSandBoxId (ThreadInfo ti, StackFrame frame){
    if (!(frame instanceof DirectCallStackFrame)){
      MethodInfo mi = frame.getMethodInfo();
      if (mi.isStatic()) {
        // <2do> what to do with static methods?
      } else {
        ElementInfo ei = ti.getElementInfo(frame.getThis());
        int id = getSandBoxId(ei);
        if (id != 0) {
          return id;
        }
      }
    }

    return 0;
  }

  int getSandBoxId (ClassInfo ci){
    if (isSandBoxed(ci)){
      // java.lang.Class instances are never @SandBoxes, so no collision here
      return ci.getClassObjectRef();
    } else {
      return 0;
    }
  }

  int getSandBoxId (ElementInfo ei){
    if (isSandBoxed(ei.getClassInfo())){
      return ei.getObjectRef();
    } else {
      return 0;
    }
  }

  int getOwnerId (ElementInfo ei){
    Attr a = ei.getObjectAttr(Attr.class);
    if (a != null){
      return a.owner;
    } else {
      return 0;
    }
  }

  void markOwned (ElementInfo ei, int ownerId){
    ei.setObjectAttrNoClone(new Attr(ownerId));
  }


  // beware - this can be expensive
  // we only reset reference chains here
  // <2do> can there be holes in the reference chains?
  void resetOwner(ThreadInfo ti, ElementInfo ei, int sbid){
    Attr a = ei.getObjectAttr(Attr.class);
    if ((a != null) && (a.owner == sbid)){
      ei.setObjectAttr(null);
      resetOwnedReferences(ti, ei, sbid);
    }
  }

  void resetOwnedReferences (ThreadInfo ti, ElementInfo ei, int sbid){
    if (ei.isArray()){
      if (ei.isReferenceArray()){
        for (int i=0; i<ei.arrayLength(); i++){
          int objRef = ei.getReferenceElement(i);
          if (objRef != MJIEnv.NULL){
            ElementInfo fei = ti.getElementInfo(objRef);
            resetOwner(ti, fei, sbid);
          }
        }
      }

    } else {
      int nFields = ei.getNumberOfFields();
      for (int i = 0; i < nFields; i++) {
        FieldInfo fi = ei.getFieldInfo(i);
        if (fi.isReference()) {
          int objRef = ei.getReferenceField(fi);
          if (objRef != MJIEnv.NULL) {
            ElementInfo fei = ti.getElementInfo(objRef);
            resetOwner(ti, fei, sbid);
          }
        }
      }
    }
  }

  /**
   * this is the instance check, which is only performed if ctx != null
   * @param eiModified the object which got modified
   * @return true if no property violation, false if a SandBox was violated
   */
  boolean checkSandBoxViolation (ThreadInfo ti, ElementInfo eiModified, Instruction insn){

    if (getSandBoxId(eiModified) == ctx.sbid){
      // all writes to ourselves are Ok
      return true;
    }

    if (ctx.sbid == getOwnerId(eiModified)) {
      // Ok, object is owned (got created) by the active sandbox context
      return true;
    }

    // we got a property violation
    Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
            "write to non-owned object: " + eiModified + " from sandbox: " + ctx.getSandbox(ti));
    ti.setNextPC(nextPc); // this is post exec

    return false;
  }

  /**
   * static field check. Just called if ctx != null
   * 
   * @param ciModified the modified class
   * @return
   */
  boolean checkSandBoxViolation (ThreadInfo ti, ClassInfo ciModified, Instruction insn){
    int sbid = getSandBoxId(ciModified);

    if (sbid == ctx.sbidCls){
      return true;
    }

    // we got a property violation
    Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
            "write to static field of: " + ciModified.getName() + " from sandbox: " + ctx.getSandbox(ti));
    ti.setNextPC(nextPc); // this is post exec

    return false;
  }

  //--- our overridden listener notifications

  @Override
  public void instructionExecuted(VM vm, ThreadInfo ti, Instruction nextInsn, Instruction insn){

    if (insn == ti.getNextPC() || (ti.getTopFrame() instanceof DirectCallStackFrame)){
      // only handle insns that are done
      return;
    }

    if (ti.isFirstStepInsn()){ // we could also check for context switches
      setContext(ti);
    }

    if (insn instanceof PUTFIELD){
      if (ctx != null){
        PUTFIELD put = (PUTFIELD) insn;
        ElementInfo ei = put.getLastElementInfo(); // field container
        checkSandBoxViolation(ti, ei, insn);
      }

    } else if (insn instanceof ArrayStoreInstruction){
      if (ctx != null){
        int objRef = ((ARRAYLENGTH) insn).getArrayRef(ti);
        ElementInfo ei = ti.getElementInfo(objRef);
        checkSandBoxViolation(ti, ei, insn);
      }

    } else if (insn instanceof PUTSTATIC){
      if (ctx != null){
        ClassInfo ci = ((PUTSTATIC)insn).getClassInfo();
        checkSandBoxViolation(ti, ci, insn);
      }

    } else if (insn instanceof NEW || insn instanceof NEWARRAY || insn instanceof ANEWARRAY){
      if (ctx != null){
        StackFrame frame = ti.getTopFrame();
        int objRef = frame.peek();  // the new object reference
        ElementInfo ei = ti.getElementInfo(objRef);
        markOwned(ei, ctx.sbid);
      }

    } else if (insn instanceof JVMReturnInstruction){
      if (ctx != null){
        StackFrame retFrame = ((JVMReturnInstruction)insn).getReturnFrame();
        if (retFrame == ctx.frame){
          int sbid = ctx.sbid;
          ctx = ctx.prev;
          
          if (insn instanceof ARETURN){
            int objRef = ((ARETURN)insn).getReturnValue();
            ElementInfo ei = ti.getElementInfo(objRef);
            if (getOwnerId(ei) == sbid) {
              resetOwner(ti, ei, sbid);
            }
          }
        }
      }

    } else if (insn instanceof JVMInvokeInstruction){
      updateContext(ti, ti.getTopFrame());
    }
  }

  @Override
  public void exceptionHandled (VM vm, ThreadInfo ti){
    // we don't try to update, just recompute the sandbox context
    setContext(ti);
  }

  @Override
  public void classLoaded (VM vm, ClassInfo ci){
    if (ci.getAnnotation("gov.nasa.jpf.annotation.SandBox") != null){
      makeSandBoxed(ci);

    } else {
      // check if one of the superClasses is sandboxed, in which case this class is sandboxed too
      for (ClassInfo sci=ci.getSuperClass(); sci != null; sci = sci.getSuperClass()){
        if (isSandBoxed(sci)){
          makeSandBoxed(ci);
          return;
        }
      }
    }
  }

}
