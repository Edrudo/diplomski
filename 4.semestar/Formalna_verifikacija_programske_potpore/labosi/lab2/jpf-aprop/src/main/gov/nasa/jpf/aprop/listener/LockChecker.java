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
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.JVMInstanceFieldInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.util.Trace;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;


/**
 * listener to check lock specifications (@GuardedBy)
 */
public class LockChecker extends ListenerAdapter {

static Logger logger = JPF.getLogger("gov.nasa.jpf.aprop");

  abstract static class LockPolicy {
    abstract boolean check (ThreadInfo ti, int objRef);
  }

  static class ThisLocked extends LockPolicy {
    boolean check (ThreadInfo ti, int objRef){
      for (ElementInfo ei : ti.getLockedObjects()){
        if (ei.getObjectRef() == objRef){
          return true;
        }
      }

      return false;
    }

    public String toString() {
      return "@GuardedBy(\"this\")";
    }
  }

  abstract static class FieldLocked extends LockPolicy {
    FieldInfo lockField;

    FieldLocked(FieldInfo fi){
      lockField = fi;
    }

    abstract int getLockRef(ThreadInfo ti, int objRef);

    boolean check (ThreadInfo ti, int objRef){
      int lockRef = getLockRef(ti, objRef);

      for (ElementInfo ei : ti.getLockedObjects()){
        if (ei.getObjectRef() == lockRef){
          return true;
        }
      }

      return false;
    }
  }

  static class InstanceFieldLocked extends FieldLocked {

    InstanceFieldLocked (FieldInfo fi){
      super(fi);
    }

    int getLockRef(ThreadInfo ti, int objRef) {
      ElementInfo eiThis = ti.getElementInfo(objRef);
      int lockRef = eiThis.getReferenceField(lockField);
      return lockRef;
    }

    public String toString() {
      return "@GuardedBy(\"" + lockField.getName() + "\")";
    }
  }

  static class StaticFieldLocked extends FieldLocked {

    StaticFieldLocked (FieldInfo fi){
      super(fi);
    }

    int getLockRef (ThreadInfo ti, int objRef){
      ElementInfo eiLock = lockField.getClassInfo().getStaticElementInfo();
      int lockRef = eiLock.getReferenceField(lockField);
      return lockRef;
    }

    public String toString() {
      return "@GuardedBy(\"" + lockField.getFullName() + "\")";
    }
  }

  static class ClassLocked extends LockPolicy {
    ClassInfo lockClass;

    ClassLocked (ClassInfo ci){
      lockClass = ci;
    }

    boolean check (ThreadInfo ti, int objRef){
      int lockRef = lockClass.getClassObjectRef();

      for (ElementInfo ei : ti.getLockedObjects()){
        if (ei.getObjectRef() == lockRef){
          return true;
        }
      }

      return false;
    }

    public String toString() {
      return "@GuardedBy(\"" + lockClass.getName() + ".class\")";
    }
  }

  static class MethodLocked extends LockPolicy {
    MethodInfo miLock;

    MethodLocked (MethodInfo mi){
      miLock = mi;
    }

    boolean check (ThreadInfo ti, int objRef){

      for (ElementInfo ei : ti.getLockedObjects()){
        Object attr = ei.getObjectAttr();
        if (attr != null && attr instanceof LockAttr){
          LockAttr lattr = (LockAttr)attr;
          if (miLock == lattr.miLock){
            if (miLock.isStatic()){  // no need to check instances
              return true;
            } else {
              if (objRef == lattr.objRef){
                return true;
              }
            }
          }
        }
      }

      return false;
    }

    public String toString() {
      return "@GuardedBy(\"" + miLock.getFullName() + "\")";
    }
  }

  static class LockAttr {
    int objRef; // the instance of non-static log getters
    MethodInfo miLock; // NOTE - this is the MethodLocked registered getter
                       // can be different than the actual getter method
    LockAttr (int objRef, MethodInfo miLock){
      this.objRef = objRef;
      this.miLock = miLock;
    }
  }

  //----------------------------------------------------------------------------

  HashMap<FieldInfo,LockPolicy> guardedFields = new HashMap<FieldInfo,LockPolicy>();

  // this is where we record potential lock creator methods
  HashMap<String,MethodInfo> lockGetters = new HashMap<String,MethodInfo>();


  public LockChecker (Config conf){
    // no options yet
  }


  // factory method for lock policies
  LockPolicy getLockPolicy (ClassInfo ci, String spec){
    if (spec == null || spec.isEmpty()){
      return null;
    }

    if ("this".equals(spec)){
      return new ThisLocked();
    }

    if (!spec.endsWith("()")){  // field or class
      FieldInfo fi = ci.getInstanceField(spec);
      if (fi != null) {
        return new InstanceFieldLocked(fi);
      }
      fi = ci.getDeclaredStaticField(spec);
      if (fi != null) {
        return new StaticFieldLocked(fi);
      }

      int idx = spec.lastIndexOf('.');
      if (idx > 0) {
        String name = spec.substring(idx + 1);
        String clsName = spec.substring(0, idx);

        if ("class".equals(name)) { // class object locked
          ClassInfo lci = ClassLoaderInfo.getCurrentResolvedClassInfo(clsName);
          return (lci != null) ? new ClassLocked(lci) : null;
        }

        ClassInfo fci = ClassLoaderInfo.getCurrentResolvedClassInfo(clsName);
        if (fci != null) {
          fi = fci.getDeclaredStaticField(name);
          return (fi != null) ? new StaticFieldLocked(fi) : null;
        }
      }
    
    } else { // method return value
      ClassInfo mci = null;
      String mname;

      int idx = spec.lastIndexOf('.');
      if (idx > 0){ // we have a class part, so this is a static method
        mci = ClassLoaderInfo.getCurrentResolvedClassInfo(spec.substring(0, idx));
        mname = spec.substring(idx+1, spec.length()-2);
      } else {
        mci = ci;
        mname = spec.substring(0, spec.length()-2);
      }

      if (mci != null){
        MethodInfo mi = mci.getMethod(mname, "()", true);
        if (mi != null) {
          lockGetters.put(mname, mi);
          return new MethodLocked(mi);
        }
      } else {
        return null;
      }
    }

    return null;
  }

  //--- overridden interface methods

  @Override
  public void instructionExecuted (VM vm, ThreadInfo ti, Instruction nextInsn, Instruction insn) {

    // make sure this is not just the top half of a CG insn
    if (!insn.isCompleted(ti)) {
      return;
    }

    if (insn instanceof JVMInstanceFieldInstruction){
      JVMInstanceFieldInstruction finsn = (JVMInstanceFieldInstruction)insn;
      int objRef = finsn.getLastThis();
      FieldInfo fi = finsn.getFieldInfo();

      LockPolicy lockPolicy = guardedFields.get(fi);
      if (lockPolicy != null){
        if (!lockPolicy.check(ti,objRef)){
          Instruction nextPc = ti.createAndThrowException("java.lang.AssertionError",
                  "failed lock policy " + lockPolicy + " of field: " + fi);
          ti.setNextPC(nextPc);
          return;
        }
      }

    } else if (insn instanceof ARETURN){ // check if this was a lock getter
      ARETURN aret = (ARETURN)insn;
      MethodInfo miRet = insn.getMethodInfo();

      MethodInfo miLock = lockGetters.get(miRet.getName());
      if (miLock != null){
        boolean isCandidate = false;

        if (miLock == miRet) { // that takes care of static or non-overridden methods
          isCandidate = true;
        } else if (miRet.getSignature().equals(miLock.getSignature())) {
          if (!miLock.isStatic()) {
            // the actual lock creator can be in a subclass of the
            // registered creator
            ClassInfo ciRet = miRet.getClassInfo();
            ClassInfo ciLock = miLock.getClassInfo();
            if (ciRet.isInstanceOf(ciLock)) {
              isCandidate = true;
            }
          }
        }

        if (isCandidate){
          int objRef = aret.getReturnFrame().getThis();
          int lockRef = aret.getReturnValue();
          if (lockRef != -1){
            ElementInfo ei = ti.getElementInfo(lockRef);
            ei.setObjectAttr(new LockAttr(objRef,miLock));
          }
        }
      }
    }
  }


  @Override
  public void classLoaded (VM vm, ClassInfo ci){
    for (FieldInfo fi : ci.getDeclaredInstanceFields()){
      AnnotationInfo ai = fi.getAnnotation("javax.annotation.concurrent.GuardedBy");
      if (ai != null){
        String value = ai.valueAsString();
        LockPolicy lockPolicy = getLockPolicy(ci,value);
        if (lockPolicy != null){
          guardedFields.put(fi, lockPolicy);
        } else {
          logger.warning("field " + fi + " has unknown lock specification: " + value);
        }
      }
    }
  }
}
