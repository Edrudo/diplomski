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

import org.junit.Test;
import gov.nasa.jpf.util.test.TestJPF;

import gov.nasa.jpf.annotation.*;

// for our model space predicate
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import gov.nasa.jpf.aprop.Predicate;
import java.util.Random;

/**
 * regression test for programming-by-contract annotations
 */
public class ContractTest extends TestJPF {

  static final String[] JPF_ARGS = { "+listener=.aprop.listener.ContractVerifier", 
                                     "+log.info=gov.nasa.jpf.aprop",
                                     "+cg.enumerate_random" };

  //--- driver to execute single test methods
  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  //--- test methods

  static class TestContractsBase {

    @Requires("a > 0")
    int foo(int a) {
      return a;
    }

    @Ensures("Result < 0")
    int baz(int a) {
      return a;
    }
  }

  @Invariant({"d within 40 +- 5",  "a > 0"})
  static class TestContracts extends TestContractsBase {

    static String w = "WHAT?";
    double d = 42.1;
    int a = 42;

    @Requires("a > 10 && a < 50")
    int foo(int a) {
      return 42 / a;
    }

    @Requires("a within -10,10")
    @Ensures("old(d) >= d")
    int baz(int a) {
      d += a;
      return a + 1;
    }

    @Requires("d <= e")
    static void bar(double d, String x, double e) {
      // nothing
    }

    // only here for the invariant
    void faz() {
      d += 100;
    }

    @Ensures("Result matches 'X.*X'")
    String encode(String s){
      return "X" + s;
    }

    // <2do> we need something like peer_packages

    // example of model predicate
    @Ensures("Result satisfies ContractTest$IsValidDate('01/01/1999', '12/31/2010')")
    @SuppressWarnings("deprecation")
    Date computeDate(int year, int month, int day) {
      Date d = new Date(year, month, day);
      System.out.println("result date is: " + d);
      return d;
    }

    // example of native predicate
    @Ensures("Result satisfies gov.nasa.jpf.aprop.predicate.IsMonotonicDecreasing")
    int computeSomething(int i, int j) {
      if (i * (5 - j) > 10) {
        return 10;
      } else {
        return j;
      }
    }

  }

  //example of model-space predicate
  @SandBox // means it's not allowed to change anything but its own fields
  static class IsValidDate implements Predicate {
    // should be side effect free
    public String evaluate (Object testObj, Object[] args) {
      Date test=null, begin=null, end=null;
      boolean result = false;

      if (testObj != null) {
        test = (Date)testObj;

        if (args.length == 2 && args[0] != null && args[1] != null) {
          try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            begin = df.parse(args[0].toString());
            end = df.parse(args[1].toString());

            if ((test.compareTo(begin) < 0) || (test.compareTo(end) > 0)){
              return "IsValidDate " + test + " outside " + begin + " and " + end;
            }

          } catch (ParseException px) {
            return "IsValidDate argument did not parse: " + px.getMessage();
          }
        }
      }
      return null; // null return means everything Ok
    }
  }

  @Test //----------------------------------------------------------------------
  public void testPrecondOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.foo(20);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testBasePrecondOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.foo(1); // should be still Ok since Base precond is "> 0"
    }
  }

  @Test //----------------------------------------------------------------------
  public void testPrecondFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.foo(0);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testPostcondOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContractsBase o = new TestContractsBase();
      int r = o.baz(-1);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testPostcondFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContractsBase o = new TestContractsBase();
      int r = o.baz(1);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testPrePostOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();
      int r = o.baz(-2);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testBasePostFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();
      int r = o.baz(-1); // postconds are strengthening
    }
  }

  @Test //----------------------------------------------------------------------
  public void testModelPredicateOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.computeDate(2009-1900, 9, 2);
    }
  }

  @Test //----------------------------------------------------------------------
  public void testModelPredicateFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.computeDate(2020-1900, 9, 2);
    }
  }


  @Test //----------------------------------------------------------------------
  public void testNativePredicateFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();

      Random random = new Random(42);
      // note we have to run this with cg.enumerate_random=true to catch the error
      int n = random.nextInt(4); // this is a choice point, i.e. we backtrack

      System.out.println("--- n= " + n);
      for (int i = 5; i >= 0; i--) {
        int m = o.computeSomething(n, i);
        System.out.println("  " + m);
      }
    }
  }

  @Test
  public void testNativePredicateOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();

      Random random = new Random(42);
      // note we have to run this with cg.enumerate_random=true to catch the error
      int n = random.nextInt(2); // this is a choice point, i.e. we backtrack

      System.out.println("--- n= " + n);
      for (int i = 5; i >= 0; i--) {
        int m = o.computeSomething(0, i);
        System.out.println("  " + m);
      }
    }
  }

  @Test //----------------------------------------------------------------------
  public void testInvariantFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.faz();
    }
  }


  @Test //----------------------------------------------------------------------
  public void testParamPropertyFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts.bar(2.0, "whatever", 1.0);
    }
  }

  @Test
  public void testMatchOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.encode("abcX");
    }
  }

  @Test
  public void testMatchFail () {
    if (verifyAssertionError(JPF_ARGS)){
      TestContracts o = new TestContracts();
      o.encode("abc");
    }
  }

  //--- test field lookup
  class Outer {
    int a;

    class Inner {

      @Ensures("c > 0")
      void bar () {
        // nothing really
      }
    }

    void foo () {
      Inner inner = new Inner();
      inner.bar();
    }
  }

  //@Test // not yet supported
  public void testInnerLookup() {
    if (verifyAssertionError(JPF_ARGS)){
      Outer outer = new Outer();
      outer.foo();
    }
  }
}
