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

import javax.annotation.Nonnull;

import org.junit.Test;
import gov.nasa.jpf.util.test.TestJPF;

/**
 * regression test for @Nonnull and the NonnullChecker
 */
public class NonnullTest extends TestJPF {

  static final String[] JPF_ARGS = { "+listener=.aprop.listener.NonnullChecker" };


  @Test  //---------------------------------------------------------------------
  public void testReturnValue() {
    if (verifyAssertionError( JPF_ARGS)) {
      Object ret = returnSomething();
    }
  }

  @Nonnull Object returnSomething() {
    return doWhatever();
  }
  Object doWhatever () {
    return null;
  }


  @Test  //---------------------------------------------------------------------
  public void testInstanceField() {
    if (verifyAssertionError( JPF_ARGS)) {
      A a = new A();
      a.reset();
    }
  }

  static class A {
    @Nonnull String id;

    A () {
      id = "a";
    }
    void reset () {
      id = computeId();
    }
    String computeId() {
      return null;
    }
  }


  @Test  //---------------------------------------------------------------------
  public void testUninitializedInstanceField() {
    if (verifyAssertionError( JPF_ARGS)) {
      B b = new B();
    }
  }

  static class B {
    @Nonnull String x;
    B() {} // look, no x init
    B(String s) {
      x = null;
    }
  }


  @Test  //---------------------------------------------------------------------
  public void testNullInitializedInstanceField() {
    if (verifyAssertionError( JPF_ARGS)) {
      B b = new B("whatever");
    }
  }



  @Test  //---------------------------------------------------------------------
  public void testStaticField() {
    if (verifyAssertionError( JPF_ARGS)) {
      C.initialize();
    }
  }
  static Object computeSomething() {
    return null;
  }
  static class C {
    @Nonnull static Object var = "something";
    
    static void initialize(){
      var = computeSomething();
    }
  }


  @Test  //---------------------------------------------------------------------
  public void testUninitializedStaticField() {
    if (verifyAssertionError( JPF_ARGS)) {
      String x = D.x;
    }
  }

  static class D {
    @Nonnull static String x;
  }


  @Test  //---------------------------------------------------------------------
  public void testNullParameter() {
    if (verifyAssertionError( JPF_ARGS)) {
      foo(computeSomething());
    }
  }

  void foo (@Nonnull Object arg){
  }


  void doesFindBugsFindThat() {
    Object a = null;
    foo(a);
  }


  @Test  //---------------------------------------------------------------------
  public void testNullBaseField() {
    if (verifyAssertionError( JPF_ARGS)) {
      F f = new F();
    }
  }

  static class E {
    @Nonnull Object d;
  }
  static class F extends E {
  }
}
