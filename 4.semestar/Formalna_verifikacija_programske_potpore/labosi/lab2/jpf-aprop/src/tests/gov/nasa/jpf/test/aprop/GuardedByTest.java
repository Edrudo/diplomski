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

import gov.nasa.jpf.util.test.TestJPF;
import org.junit.Test;

import javax.annotation.concurrent.GuardedBy;

/**
 * regression test for lock specification annotations
 */
public class GuardedByTest extends TestJPF {

  static final String[] JPF_ARGS = { "+listener=.aprop.listener.LockChecker" };

  //--- driver to execute single test methods
  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  //--- test methods

  @GuardedBy("this")
  int d;

  @GuardedBy("getLock()")
  int precious;

  Object lock = new Object();

  @GuardedBy("gov.nasa.jpf.test.aprop.GuardedByTest.class")
  int classLocked;


  void setD (){
    d = 42;
  }

  Object getLock() {
    return lock;
  }

  void setPrecious(Object lock) {
    synchronized(lock){
      precious = 42;
    }
  }

  @Test //---------------------------------------------------------------------
  public void testThisLockOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      synchronized (this){
        setD();
      }
    }
  }


  @Test //---------------------------------------------------------------------
  public void testThisLockFail () {
    if (verifyAssertionError(JPF_ARGS)){
      setD();
    }
  }


  @Test //---------------------------------------------------------------------
  public void testSimpleMethodLockOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      synchronized (getLock()){
        precious = 42;
      }
    }
  }

  @Test //---------------------------------------------------------------------
  public void testSimpleMethodLockFail () {
    if (verifyAssertionError(JPF_ARGS)){
      precious = 42;
    }
  }


  @Test //---------------------------------------------------------------------
  public void testWrongInstanceMethodLockFail () {
    if (verifyAssertionError(JPF_ARGS)){
      GuardedByTest other = new GuardedByTest();

      synchronized(other.getLock()){
        precious = 42; // fooled you
      }
    }
  }

  @Test //---------------------------------------------------------------------
  public void testMethodLockOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      Object o = getLock();
      setPrecious(o);
    }
  }

  @Test //---------------------------------------------------------------------
  public void testClassLockOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      synchronized(GuardedByTest.class){
        classLocked = 42;
      }
    }
  }

  @Test //---------------------------------------------------------------------
  public void testClassLockFail () {
    if (verifyAssertionError(JPF_ARGS)){
      synchronized(TestJPF.class){
        classLocked = 42;
      }
    }
  }

}
