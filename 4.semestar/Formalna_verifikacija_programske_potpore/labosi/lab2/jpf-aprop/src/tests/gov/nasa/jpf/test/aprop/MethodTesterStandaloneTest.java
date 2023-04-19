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

import gov.nasa.jpf.aprop.tools.MethodTester;
import gov.nasa.jpf.annotation.Test;


/**
 * direct test of MethodTester tool
 */
public class MethodTesterStandaloneTest {

  static final String TEST_CLASS = TestClass.class.getName();

  static class TestClass {

    double d;

    TestClass () {}
    
    TestClass (double d){
      this.d = d;
    }

    @Test( "(41) == 42" )
    int basicTestOk(int a) {
      return a+1;
    }

    @Test( "this(42.0).(1.0) within 40 +- 1")
    double withinFailure (double r){
      return d+r;
    }

    @Test( "([12],'a'|'b') noThrows")
    void argCombination (int a, String s){
    }
  }

  //--- test methods

  @org.junit.Test
  public void basicTestOk () {
    String[] args = { TEST_CLASS, "basicTestOk" };
    MethodTester t = new MethodTester(args);

    t.run();
    assert t.getNumberOfTests() == 1 && t.getNumberOfFailures() == 0;
  }

  @org.junit.Test
  public void withinFailure () {
    String[] args = { TEST_CLASS, "withinFailure" };
    MethodTester t = new MethodTester(args);

    t.run();
    assert( t.getNumberOfTests() == 1 && t.getNumberOfFailures() == 1);
  }

  @org.junit.Test
  public void argCombinationOk () {
    String[] args = { TEST_CLASS, "argCombination" };
    MethodTester t = new MethodTester(args);

    t.run();
    assert( t.getNumberOfTests() == 4 && t.getNumberOfFailures() == 0);
  }

}

