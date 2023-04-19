//
// Copyright (C) 2008 United States Government as represented by the
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

import java.util.HashSet;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.PUTFIELD;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.jvm.bytecode.RETURN;
import gov.nasa.jpf.vm.StackFrame;

/**
 * listener that checks if @NonNull marked 
 * 
 *  - methods return null values
 *  - method paramters that are null
 *  - instance fields are or get nullified outside of ctors
 *  - static fields are or get nullified outside clinit
 * 
 * In case the property is violated, the listener throws an AssertionError
 */
public class NonnullChecker extends ListenerAdapter {

  static final String ANNOTATION_TYPE = "javax.annotation.Nonnull";

  
  enum FieldCandidate { INSTANCE, STATIC };
    
  void makeInstanceFieldCandidate (ClassInfo ci){
    ci.addAttr( FieldCandidate.INSTANCE);
  }
  boolean isInstanceFieldCandidate (ClassInfo ci){
    return ci.hasAttrValue( FieldCandidate.INSTANCE);
  }
  void makeStaticFieldCandidate (ClassInfo ci){
    ci.addAttr( FieldCandidate.STATIC);
  }
  boolean isStaticFieldCandidate (ClassInfo ci){
    return ci.hasAttrValue( FieldCandidate.STATIC);
  }
  
  
  
  
  @Override
  public void classLoaded (VM vm, ClassInfo ci) {
    
    // check for @Nonnull static fields w/o clinit
    for (FieldInfo fi : ci.getDeclaredStaticFields()) {
      if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
        if (ci.getClinit() == null) {
          throwAssertionError( vm.getCurrentThread(),
                               "@Nonnull static field without clinit: " + fi.getFullName());
          return;          
        }
        
        makeStaticFieldCandidate(ci);
        break;
      }
    }
    
    // check if there are any @NonNull instance fields
    for (FieldInfo fi : ci.getDeclaredInstanceFields()) {
      if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
        if (!ci.hasCtors()) {
          throwAssertionError( vm.getCurrentThread(),
                               "@Nonnull instance field without ctor: " + fi.getFullName());
          return;          
        }
        makeInstanceFieldCandidate(ci);
        break;
      }
    }
  }

  public void executeInstruction (VM vm, ThreadInfo ti, Instruction insn){

    if (insn instanceof JVMInvokeInstruction){
      JVMInvokeInstruction call = (JVMInvokeInstruction)insn;
      MethodInfo callee = call.getInvokedMethod(ti);

      if (callee.hasParameterAnnotations()){
        AnnotationInfo[][] paramAnnotations = callee.getParameterAnnotations();
        if (paramAnnotations != null){
          Object[] args = call.getArgumentValues(ti);
          for (int i = 0; i < args.length; i++) {
            if (paramAnnotations[i] != null) {
              for (int j = 0; j < paramAnnotations.length; j++) {
                if (ANNOTATION_TYPE.equals(paramAnnotations[i][j].getName())) {
                  if (args[i] == null) {
                    throwAssertionError(ti,
                            "null value of @Nonnull parameter: " + callee.getFullName() + ", parameter: " + i);
                    return;
                  }
                }
              }
            }
          }
        }
      }

    } else if (insn instanceof ARETURN) {  // check @NonNull method returns
      ARETURN areturn = (ARETURN)insn;
      MethodInfo mi = insn.getMethodInfo();
      if (areturn.getReturnValue(ti) == null) {
        // probably faster to directly check for annotation, and not bother with instanceCandidate lookup
        if (mi.getAnnotation(ANNOTATION_TYPE) != null) {
          throwAssertionError( ti, "null return from @Nonnull method: " + mi.getFullName());
          return;
        }
      }

    } else if (insn instanceof RETURN) {  // check @NonNull fields
      RETURN ret = (RETURN)insn;

      // rather than checking every GETFIELD, we only check at the end of
      // each ctor in the inheritance hierarchy if declared @NonNulls are initialized

      // NOTE - this is currently per-base, but we could defer the check until the
      // concrete type ctor returns

      MethodInfo mi = insn.getMethodInfo();

      if (mi.isCtor()) {   // instance field checks
        ClassInfo ci = mi.getClassInfo();
        if (isInstanceFieldCandidate(ci)){
          ElementInfo ei = ti.getElementInfo(ti.getThis());

          FieldInfo fi = checkNonNullInstanceField(ci, ei);
          if (fi != null) {
            throwAssertionError(ti, "uninitialized @Nonnull instance field: " + fi.getFullName());
            return;
          }
        }

      } else if (mi.isClinit()) {  // static field checks
        ClassInfo ci = mi.getClassInfo();
        if (isStaticFieldCandidate(ci)){
          FieldInfo fi = checkNonNullStaticField( ci);
          if (fi != null) {
            throwAssertionError( ti, "uninitialized @Nonnull static field: " + fi.getFullName());
            return;
          }
        }
      }

    } else if (insn instanceof PUTFIELD) {  // null instance field assignment
      MethodInfo mi = insn.getMethodInfo();
      if (!mi.isCtor()) {
        // probably faster to directly check for annotation, and not bother with instanceCandidate lookup
        PUTFIELD put = (PUTFIELD)insn;
        StackFrame frame = ti.getTopFrame();
        if (frame.peek() == MJIEnv.NULL) {
          FieldInfo fi = put.getFieldInfo();
          if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
            throwAssertionError( ti, "null assignment to @Nonnull instance field: " + fi.getFullName());
            return;
          }
        }
      }

    } else if (insn instanceof PUTSTATIC) {  // null static field assignment
      MethodInfo mi = insn.getMethodInfo();
      if (!mi.isClinit()) {
        // probably faster to directly check for annotation, and not bother with staticCandidate lookup
        PUTSTATIC put = (PUTSTATIC)insn;
        StackFrame frame = ti.getTopFrame();
        if (frame.peek() == MJIEnv.NULL) {
          FieldInfo fi = put.getFieldInfo();
          if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
            throwAssertionError( ti, "null assignment to @Nonnull static field: " + fi.getFullName());
            return;
          }
        }
      }
    }
  }  
  
  
  //--- internal helper methods
  
  void throwAssertionError (ThreadInfo ti, String msg) {
    // since we might throw from within and outside insn.execute(), and the thrown
    // exceptions are just AssertionErrors, the safer way is to let ThreadInfo do
    // the throwing from within executeInstruction
    ti.requestSUTException( "java.lang.AssertionError", msg);
  }
  
  FieldInfo checkNonNullInstanceField (ClassInfo ci, ElementInfo ei) {
    // note - we only check declared fields here, but call this for every
    // ctor in the chain
    for (FieldInfo fi : ci.getDeclaredInstanceFields()) {
      if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
        if (ei.getReferenceField(fi) == MJIEnv.NULL) {
          return fi;
        }
      }
    }
    
    return null;
  }
  
  FieldInfo checkNonNullStaticField (ClassInfo ci) {
    ElementInfo ei = ci.getStaticElementInfo();
    
    for (FieldInfo fi : ci.getDeclaredStaticFields()) {
      if (fi.getAnnotation(ANNOTATION_TYPE) != null) {
        if (ei.getReferenceField(fi) == MJIEnv.NULL) {
          return fi;
        }
      }
    }
    
    return null;
  }
  
}
