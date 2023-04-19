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

import gov.nasa.jpf.annotation.Const;
import gov.nasa.jpf.util.test.TestJPF;
import org.junit.Test;

public class ConstTest extends TestJPF {

  static final String[] JPF_ARGS = { "+listener=.aprop.listener.ConstChecker" };


  //--- the test methods

  static int s = 42000;
  int d;


  @Test //---------------------------------------------------------------------
  public void testStaticConstOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      ConstTest.doThis();
    }
  }

  @Const
  static int doThis() {
    return s;
  }


  @Test //---------------------------------------------------------------------
  public void testInstanceConstOk (){
    if (verifyNoPropertyViolation(JPF_ARGS)){
      doThat();
    }
  }

  @Const
  int doThat() {
    return d + 1;
  }


  @Test //---------------------------------------------------------------------
  public void testNonConstOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      doWhatever();
    }
  }

  void doWhatever() {
    d+= 10;
  }


  @Test //---------------------------------------------------------------------
  public void testDeepInstanceViolation () {
    if (verifyAssertionError(JPF_ARGS)){
      dontDoThis();
    }
  }
  
  @Const
  public void dontDoThis() {
    foo();
  }
  void foo () {
    d = 42;
  }


  @Test //---------------------------------------------------------------------
  public void testImmutableObjectOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      ConstObj o = new ConstObj();
      int d = o.whatIsIt();
    }
  }

  @Test
  public void testImmutableObjectViolation () {
    if (verifyAssertionError(JPF_ARGS)){
      ConstObj o = new ConstObj();
      o.initialize(); // should cause exception
    }
  }

  @Const static class ConstObj {
    int d;

    ConstObj (){
      initialize();
    }

    void initialize() { // Ok to call from within ctor, not outside
      d = 42;
    }

    int whatIsIt() { // always Ok
      return d;
    }
  }

  @Test //---------------------------------------------------------------------
  public void testConstInstanceFieldOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      ConstInstanceFields o = new ConstInstanceFields();
    }
  }

  @Test
  public void testConstInstanceFieldViolation () {
    if (verifyAssertionError(JPF_ARGS)){
      ConstInstanceFields o = new ConstInstanceFields();
      o.initialize();
    }
  }

  static class ConstInstanceFields {
    @Const int d;

    ConstInstanceFields() {
      initialize();
    }

    void initialize() {
      d = 42;
    }
  }

  @Test //---------------------------------------------------------------------
  public void testConstStaticFieldOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      int d = ConstStaticFields.d;
    }
  }

  @Test
  public void testConstStaticFieldViolation () {
    if (verifyAssertionError(JPF_ARGS)){
      ConstStaticFields.staticInit();
    }
  }


  static class ConstStaticFields {
    @Const static int d;

    static {
      staticInit();
    }
    static void staticInit(){
      d = 42;
    }
  }
}
