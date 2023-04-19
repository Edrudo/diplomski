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

import gov.nasa.jpf.annotation.SandBox;
import gov.nasa.jpf.vm.Verify;
import gov.nasa.jpf.util.test.TestJPF;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * regression test for @SandBox checker
 */
public class SandBoxTest extends TestJPF {

  static String[] JPF_ARGS = {"+listener=.aprop.listener.SandBoxChecker",
                              "+report.console.property_violation=error",
                              "+report.console.finished=result"};
  int d;
  static int s;


  static class X {

    Object d;

    void setD(Object o) {
      d = o;
    }
  }

  @SandBox
  static class SB1 {

    static Object s;
    Object d;

    public void modifyThis(Object o) {
      d = o;
    }

    public void modifyArray(int[] a) {
      a[0] = 42;
    }
    
    public void modifyOwnedArray () {
      int[] a = new int[1];
      modifyArray(a);
    }

    public void modifyNonOwned(SandBoxTest o) {
      o.d = 42;
    }

    public void modifyOwnedSimple() {
      X x = new X();
      modifyX(x);
    }

    public void modify (SB1 o){
      o.d = "maybe";
    }

    public void modifyContainer (HashMap<String,String> map){
      map.put("answer to the ultimate question", "42");
    }

    public void modifyOwnedContainer() {
      // <2do> pcm - in Java 7 this causes a violation in HashMap.<clinit>
      // if HashMap hasn't been initialized yet
      HashMap<String,String> map = new HashMap<String,String>();
      modifyContainer(map);
    }

    public X createX() {
      X x = new X();
      x.setD("that's Ok");
      return x;
    }

    public void modifyX (X x){
      x.setD("whatever");
    }

    public HashMap<String,X> createMap() {
      HashMap<String,X> map = new HashMap<String,X>();
      X x = new X();
      map.put("myX", x);
      return map;
    }

    public static void modifyStatic (Object o){
      s = o;
    }

    public static void modifyOwnStatic() {
      modifyStatic("whatever");
    }

    public static void modifyExternalStaticFromStatic() {
      SandBoxTest.s = 42;
    }

    public void modifyExternalStaticFromInstance() {
      SandBoxTest.s = 42;
    }

  }


  @Test
  public void testSimpleOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("modifying own fields Ok");
      sb.modifyThis("something"); // throwing something into the sandbox is Ok
    }
  }

  @Test
  public void testSimpleFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("modifying external object not good!");
      sb.modifyNonOwned(this); // should throw AssertionError
    }
  }

  @Test
  public void testArrayOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("modifying owned array is Ok");
      sb.modifyOwnedArray(); // throwing something into the sandbox is Ok
    }
  }

  @Test
  public void testArrayFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      int[] a = new int[2];
      System.out.println("modifying external array not good!");
      sb.modifyArray(a); // should throw AssertionError
    }
  }

  @Test
  public void testOwnedSimpleOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("Ok to set fields in something we created");
      sb.modifyOwnedSimple();
    }
  }

  @Test
  public void testOwnedContainerOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      HashMap<Object,Object> dummy = new HashMap<Object,Object>(); // otherwise we get a sandbox violation in clinit

      SB1 sb = new SB1();
      System.out.println("Ok to modify owned container");
      sb.modifyOwnedContainer();
    }
  }

  @Test
  public void testContainerFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("modifying external container not good!");
      HashMap<String,String> map = new HashMap<String,String>();
      sb.modifyContainer(map);
    }
  }

  @Test
  public void testSimpleLeakFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      X x = sb.createX();

      System.out.println("modifying leaked object not good!");
      sb.modifyX(x);
    }
  }

  @Test
  public void testContainerLeakFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      HashMap<String,X> map = sb.createMap();
      X x = map.get("myX");

      // toss in a CG to see if we restore the sandbox context properly
      boolean b = Verify.getBoolean();

      System.out.println("modifying object from leaked map not good!");
      sb.modifyX(x);
    }
  }


  @Test
  public void testModifyStaticOk() {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("Ok to modify own static fields from static sandbox methods");
      sb.modifyOwnStatic();
    }
  }

  @Test
  public void testModifyStaticFromStaticFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      System.out.println("modifying external statics from static method not good");
      SB1.modifyExternalStaticFromStatic();
    }
  }

  @Test
  public void testModifyStaticFromInstanceFail() {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("modifying external statics from instance method not good");
      sb.modifyExternalStaticFromInstance();
    }
  }

  @Test
  public void testModifySelfArgOk () {
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      SB1 sb = new SB1();
      System.out.println("Ok to modify self via argument");
      sb.modify(sb);
    }
  }
  
  @Test
  public void testModifyOtherArgFail () {
    if (verifyAssertionError(JPF_ARGS)) {
      SB1 sb = new SB1();
      SB1 other = new SB1();
      System.out.println("modifying other sandbox instance not good");
      sb.modify(other);
    }
  }

}
