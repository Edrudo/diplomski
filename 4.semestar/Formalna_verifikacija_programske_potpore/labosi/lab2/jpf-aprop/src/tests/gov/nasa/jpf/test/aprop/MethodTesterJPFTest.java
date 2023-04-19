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

import gov.nasa.jpf.annotation.Test;
import gov.nasa.jpf.aprop.tools.MethodTester;
import gov.nasa.jpf.util.test.TestJPF;



/**
 * regression test for the MethodTester tool execution under JPF
 */
public class MethodTesterJPFTest extends TestJPF {

  static String TEST_CLASS = "gov.nasa.jpf.test.aprop.MethodTesterJPFTest$TestClass";

  //--- driver to execute single test methods
  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  //----------------------------------------- the SUT

  static class TestClass {

    @Test("(41) == 42")
    int testOk (int a){
      return a+1;
    }

    @Test("(0.0) throws IllegalArumentException")
    void testFail (double d){
      // we don't throw, so the test fails
    }

    // doesn't work yet
    @Test("(Integer.MAX_VALUE) satisfies gov.nasa.jpf.aprop.goal.NoOverflow")
    void testOverflow(int a){
      int x = a + 1;
    }
  }

  //----------------------------------------- junit tests

  // this shows why it is very suboptimal to run the MethodTester under JPF
  static final String JPF_CP = "+classpath=${jpf-aprop.native_classpath};${jpf-core.native_classpath}";

  @org.junit.Test
  public void testOk() {
    if (verifyNoPropertyViolation(JPF_CP)){
      MethodTester.main(new String[] {"-x", TEST_CLASS, "testOk"});
    }
  }

  @org.junit.Test
  public void testFail() {
    if (verifyAssertionError(JPF_CP)){
      MethodTester.main(new String[] {"-x", TEST_CLASS, "testFail"});
    }
  }


}
