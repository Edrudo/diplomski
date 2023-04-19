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

package gov.nasa.jpf.test.aprop;

import gov.nasa.jpf.annotation.NonShared;

import org.junit.Test;
import gov.nasa.jpf.util.test.TestJPF;

/**
 *
 */
public class NonSharedTest extends TestJPF {

  static final String[] JPF_ARGS = { "+listener=.aprop.listener.NonSharedChecker",
                                     "+nonshared.throw_on_cycle", "+cg.threads.break_start" };

  //--- driver to execute single test methods
  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  //--- test methods

  @NonShared
  static class SomeObject implements Runnable {
    int d;
    public void run() {
      d = 42;
    }
    public void doNothing() {}
  }

  @Test //----------------------------------------------------------------------
  public void testSingleOk(){
    if (verifyNoPropertyViolation(JPF_ARGS)){
      SomeObject o = new SomeObject();
      o.run();
    }
  }

  @Test //----------------------------------------------------------------------
  public void testMultipleOk(){
    if (verifyNoPropertyViolation(JPF_ARGS)){
      SomeObject o = new SomeObject();
      Thread t = new Thread(o);
      t.start();
    }
  }

  @Test //----------------------------------------------------------------------
  public void testMultipleFail(){
    if (verifyAssertionError(JPF_ARGS)){
      SomeObject o = new SomeObject();
      Thread t = new Thread(o);
      t.start();
      o.doNothing();
    }
  }

  @Test //----------------------------------------------------------------------
  public void testHandoverOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      SomeObject o = new SomeObject();
      Thread t = new Thread(o);
      t.start();
      try {
        t.join();
        o.doNothing();
      } catch (InterruptedException ix){
        throw new RuntimeException("unexpected interrupt");
      }
    }
  }
}
