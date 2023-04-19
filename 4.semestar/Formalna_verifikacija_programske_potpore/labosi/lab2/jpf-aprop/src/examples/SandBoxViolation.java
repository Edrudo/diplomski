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

import gov.nasa.jpf.annotation.SandBox;

/**
 * example of @SandBox usage
 */
public class SandBoxViolation {

  static class X {
    int d;
    void doSomethingUnsuspicious () {
      d++; // nothing bad in and of itself
    }
  }

  @SandBox
  static class Y {
    X myOwnX = new X();

    void foo (X x){
      x.doSomethingUnsuspicious();
    }
  }

  public static void main (String[] args){
    X x = new X();
    Y y = new Y();

    x.doSomethingUnsuspicious(); // fine
    y.foo(y.myOwnX); // still fine
    y.foo(x); // not so fine
  }
}
