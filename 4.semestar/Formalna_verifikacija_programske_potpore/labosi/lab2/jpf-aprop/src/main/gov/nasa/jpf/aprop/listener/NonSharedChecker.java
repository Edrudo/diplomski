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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.ALOAD;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.ASTORE;
import gov.nasa.jpf.jvm.bytecode.IFNONNULL;
import gov.nasa.jpf.jvm.bytecode.IFNULL;
import gov.nasa.jpf.jvm.bytecode.JVMInstanceFieldInstruction;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.MONITORENTER;
import gov.nasa.jpf.jvm.bytecode.VirtualInvocation;
import gov.nasa.jpf.vm.StackFrame;

/**
 *
 */
public class NonSharedChecker extends ListenerAdapter {

  boolean throwOnCycle = false;

  static class Access {
    ThreadInfo ti;
    Access prev;

    Access(ThreadInfo ti, Access prev){
      this.ti = ti;
      this.prev = prev;
    }

    // <2do> get a better hashCode for state hashing
    public int hashCode() {
      int h = ti.getId();
      for (Access p = prev; p!= null; p = p.prev){
        h = 31*h + p.ti.getId();
      }
      return h;
    }
    // but we don't care for equals()
  }

  public NonSharedChecker (Config conf){
    throwOnCycle = conf.getBoolean("nonshared.throw_on_cycle");
  }

  boolean isNonShared(ElementInfo ei){
    ClassInfo ci = ei.getClassInfo();
    return (ci.getAnnotation("gov.nasa.jpf.annotation.NonShared") != null);
  }

  boolean checkLiveCycles (ElementInfo ei, ThreadInfo ti, Access ac){
    if (ti == ac.ti){
      return true; // Ok, fine - no need to record

    } else {
      boolean foundLiveOne = false;
      for (Access a = ac; a != null; a = a.prev){
        ThreadInfo t = a.ti;
        if (t == ti){ // cycle detected
          return !foundLiveOne;
        }
        foundLiveOne = (foundLiveOne || t.isAlive()); // <2do> maybe we should check for non-blocked threads
      }

      // new one, record it in the access history of the object
      ac = new Access(ti, ac);
      ei = ei.getModifiableInstance();
      ei.setObjectAttr(ac);
    }

    return true;
  }


  @Override
  public void executeInstruction (VM vm, ThreadInfo ti, Instruction insn){

    ElementInfo ei = null;

    if (ti.isFirstStepInsn()) {
      return;
    }

    if (insn instanceof JVMInstanceFieldInstruction){
      ei = ((JVMInstanceFieldInstruction)insn).peekElementInfo(ti);
    } else if (insn instanceof VirtualInvocation){
      ei = ((VirtualInvocation)insn).getThisElementInfo(ti);  // Outch - that's expensive
    } else if (insn instanceof MONITORENTER ||
               insn instanceof ASTORE ||
               insn instanceof ARETURN ||
               insn instanceof IFNONNULL ||
               insn instanceof IFNULL) {
      StackFrame frame = ti.getTopFrame();
      int ref = frame.peek();
      if (ref != -1){
        ei = ti.getElementInfo(ref);
      }
    } else if (insn instanceof ALOAD){
      StackFrame frame = ti.getTopFrame();
      int ref = frame.getLocalVariable(((ALOAD)insn).getLocalVariableIndex());
      if (ref != -1){
        ei = ti.getElementInfo(ref);
      }
    }

    if (ei != null){
      Access ac = ei.getObjectAttr(Access.class);
      if (ac != null){
        if (!checkLiveCycles(ei,ti,ac)){
          StringBuilder sb = new StringBuilder("NonShared object: ");
          sb.append( ei);
          sb.append(" accessed in live thread cycle: ");
          sb.append( ti.getName());
          for (Access a = ac; a != null; a = a.prev ){
            sb.append(',');
            sb.append(a.ti.getName());
          }
          String msg = sb.toString();

          if (throwOnCycle){
            ti.setNextPC( ti.createAndThrowException("java.lang.AssertionError", msg));
          } else {
            System.err.println("WARNING: " + msg);
            System.err.println("\tat " + insn.getSourceLocation());
          }
          return;
        }
      }
    }
  }

  @Override
  public void objectCreated (VM vm, ThreadInfo ti, ElementInfo ei){
    if (isNonShared(ei)){
      ei.setObjectAttrNoClone(new Access(ti, null));
    }
  }
}
