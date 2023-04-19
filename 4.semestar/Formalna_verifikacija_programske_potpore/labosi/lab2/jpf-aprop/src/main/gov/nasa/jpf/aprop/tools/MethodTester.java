//
// Copyright (C) 2007 United States Government as represented by the
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

package gov.nasa.jpf.aprop.tools;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import gov.nasa.jpf.annotation.Test;
import gov.nasa.jpf.aprop.ArgList;
import gov.nasa.jpf.aprop.FieldReference;
import gov.nasa.jpf.aprop.Goal;
import gov.nasa.jpf.aprop.TestException;
import gov.nasa.jpf.aprop.TestContext;
import gov.nasa.jpf.aprop.TestSpec;
import gov.nasa.jpf.aprop.TestSpecLexer;
import gov.nasa.jpf.aprop.TestSpecParser;
import gov.nasa.jpf.util.StringSetMatcher;


/**
 * simple standalone tool that looks for @Test annotations, parses their
 * specs, and performs the corresponding test calls
 *
 * <2do> needs to be refactored so that all SUT code execution is delegated to
 * the goal, which might use different execution environments (like JPF)
 */
public class MethodTester {
  static final ArgList EMPTY_ARGS = new ArgList();
  
  String[] mthPatterns;
  StringSetMatcher mthMatcher;
  
  PrintWriter err;
  PrintWriter log;
  TestContext tctx;
  
  Class<?> targetCls;
  Constructor[] targetCtors;
  
  int nTotalMth, nTestMth;
  int nTests;
  int nOk, nFailure, nMalformed;

  boolean throwOnFailure;

  public MethodTester (String[] args){

    log = new PrintWriter(System.out, true);
    err = new PrintWriter(System.err, true);
    
    if (args.length == 0){
      showUsage();
    } else {
      int i = 0;
      if ("-x".equals(args[0])){
        throwOnFailure = true;
        i++;
      }

      targetCls = loadClass(args[i]);
      if (targetCls != null){
        i++;
        targetCtors = targetCls.getDeclaredConstructors();
        
        tctx = new TestContext(targetCls, log, err);
        
        if (args.length > i){
          mthPatterns = new String[args.length-i];
          System.arraycopy(args,i,mthPatterns,0,mthPatterns.length);
          mthMatcher = new StringSetMatcher(mthPatterns);
        }
      }
    }
  }

  void log (String msg){
    log.println("@ " + msg);
  }

  void error (String msg){
    err.println("@ ERROR " + msg);

    if (throwOnFailure){
      throw new TestException(msg);
    }
  }

  public void fail (String msg){
    nFailure++;

    log("FAILURE\n");

    if (throwOnFailure){
      throw new AssertionError(msg);
    }
  }

  public void malformed (String msg){
    nMalformed++;
    error(msg);
  }

  public void ok (String msg) {
    nOk++;
    if (msg != null){
      log("Ok: " + msg + '\n');
    } else {
      log("Ok\n");
    }
  }

  
  Class<?> loadClass(String clsName){
    try {
      Class<?> cls = Class.forName(clsName);
      return cls;
      
    } catch (ClassNotFoundException cnfx){
      error("class \"" + clsName + "\" not found");
      return null;
    }
  }
  
  Constructor getCtor (ArgList args){
    for (Constructor ctor : targetCtors){
      if (argsCompatible(ctor.getParameterTypes(), args)){
        ctor.setAccessible(true);
        return ctor;
      }
    }
    
    nMalformed++;
    error("no suitable constructor");
    return null;
  }
  
  boolean isTypeCompatible (Class<?> p, Class<?> a){

    if (p == a){ // identity - that's easy
      return true;
    }
    
    // builtin types - check basic type
    // <2do> - we might want to add cast support here
    if (a == Integer.class){
      if (p == int.class) return true;
    } else if (a == Long.class){
      if (p == long.class) return true;
    } else if (a == Double.class){
      if (p == double.class) return true;
    } else if (a == Boolean.class){
      if (p == boolean.class) return true;
    } else if (a == Byte.class){
      if (p == byte.class) return true;
    } else if (a == Character.class){
      if (p == char.class) return true;
    } else if (a == Short.class){
      if (p == short.class) return true;
    } else if (a == Float.class){
      if (p == float.class) return true;
      
    } else { // arbitrary types
      if (p.isAssignableFrom(a)){
        return true;
      }            
    }
    
    return false;
  }

  boolean argsCompatible (Class<?>[] pTypes, ArgList args){
    Class<?>[] aTypes = args.getArgTypes();
    
    if (aTypes.length == pTypes.length){
      for (int i=0; i<aTypes.length; i++){
        if (!isTypeCompatible(pTypes[i], aTypes[i])){
          return false;
        }
      }
      return true;
      
    } else {
      return false;
    }
  }
  
  boolean returnTypeCompatible (Method m, Goal g){
    Class<?> gType = g.getGoalType();
    
    if (g != null){
      Class<?> rType = m.getReturnType();
      return isTypeCompatible(rType, gType); // note, it's flipped
    } else {
      return true;
    }
  }
  
  String argsToString (Object[] args){
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (int i=0; i<args.length; i++){
      if (i>0){
        sb.append(',');
      }
      sb.append(args[i]);
    }
    sb.append(')');
    return sb.toString();
  }
  
  boolean resolveFieldReferences (Object[] mthArgs){
    for (int i=0; i<mthArgs.length; i++){
      if (mthArgs[i] instanceof FieldReference){
        FieldReference fr = (FieldReference)mthArgs[i];
        Object val = tctx.getFieldValue(fr);
        
        if (val == null){
          malformed( "cannot get field value for: " + fr.getId());
          return false;
        } else {
          mthArgs[i] = val;
        }
      }
    }
    
    return true;
  }
  
  boolean makeArgsCompatible (Object[] args, Class<?>[] pTypes){
    if (args.length != pTypes.length){
      return false;
    }
    
    for (int i=0; i<args.length; i++){
      Object a = tctx.getCompatibleObject(args[i], pTypes[i]);
      if (a == null){
        malformed("incompatible argument: " + args[i] + ", expected " + pTypes[i].getName());
        return false;
      } else {
        args[i] = a;
      }
    }
    
    return true;
  }
  
  public TestContext getTestContext() {
    return tctx;
  }

  String describeTest (Object tgt, Method m, Object[] ctorArgs, Object[] mthArgs, Goal goal){
    StringBuilder sb = new StringBuilder();

    sb.append(targetCls.getSimpleName());
    if (tgt != null){
      sb.append(argsToString(ctorArgs));
      sb.append('.');
    }
    sb.append(m.getName());
    sb.append(argsToString(mthArgs));

    if (goal != null){
      sb.append(" ");
      sb.append( goal);
    }

    return sb.toString();
  }

  void execute (Method m, Object tgt, Object[] ctorArgs, Object[] mthArgs, Goal goal){
    Object ret = null;
    Throwable ex = null;

    nTests++;
    
    if (preCheck( m, tgt, ctorArgs, mthArgs, goal)){
      log("execute: " + describeTest(tgt, m, ctorArgs, mthArgs, goal));

      try {
        if (resolveFieldReferences(mthArgs)) {
          if (makeArgsCompatible(mthArgs, m.getParameterTypes())) {

            // the actual method execution needs to be delegated to the goal
            // since it might involve a different execution environment (e.g. JPF)
            ret = goal.execute(this, m, tgt, mthArgs);

            if (m.getReturnType() != void.class) {
              log("returns: " + getLiteral(ret));
            }
            postCheck( m, tgt, ctorArgs, mthArgs, goal, ret, ex);

          } else {
            malformed("incompatible arguments");
          }
        } else {
          malformed("unresolved field referneces");
        }

      } catch (IllegalAccessException iax) {
        error("illegal access"); // shouldn't happen, it's set accessible
      } catch (InvocationTargetException t) {
        error("throws: " + t.getCause());
      }
    }
  }
    
  boolean preCheck (Method m, Object tgt, Object[] ctorArgs, Object[] mthArgs, Goal goal){
    if (goal == null){ // no goal for this spec
      return false;
      
    } else {
    
      // we should check compatibility between goal type and method return type here

      try {
        if (!goal.preCheck(tctx, m)){
          fail( describeTest(tgt,m,ctorArgs,mthArgs,goal));
          return false;
        }
      } catch (TestException tx){
        malformed(tx.getMessage());
      }

      return true;
    }
  }

  void postCheck (Method m, Object tgt, Object[] ctorArgs, Object[] mthArgs, Goal goal, Object ret, Throwable ex) {
    Class<?> gType = goal.getGoalType();
    Object r=ret;
    if (gType != null){
      r = TestContext.getCompatibleObject(ret,gType);
      if (r == null){
        malformed("incompatible return object: " + ret.getClass().getName());
        return;
      }
    }
    
    try {
      if (goal.postCheck(tctx,m,r,ex)){
        ok(null);
      } else {
        fail( "failed test: " + describeTest(tgt,m,ctorArgs,mthArgs,goal));
      }
    } catch (TestException ox){
      malformed(ox.getMessage());
    }
  }
  
  String getLiteral (Object o){
    String s;
    
    if (o == null){
      s = "null";
    } else if (o instanceof String){
      // <2do> what about escape chars?
      s = "\"" + o + '"';
    } else {
      s = o.toString();
    }
    
    // <2do> - should probably do a few more conversions (char etc.)
    
    return s;
  }
  
  void test (Method m, TestSpec tspec){
        
    m.setAccessible(true); // we don't care about visibility
    
    for (Goal goal : tspec.getGoals()){

      // <2do> this needs to be refactored so that the target object creation
      // and method execution is wrapped by the goal, which might involve
      // a different execution environment (e.g. JPF)

      if (Modifier.isStatic(m.getModifiers())){ // static method test
        // no need for a ctor, so things get simple
        for (Object[] mthArgs : tspec.getCallArgCombinations()){
          execute(m, null, null, mthArgs, goal);
        }

      } else {  // instance method test

        List<ArgList> tgtArgs = tspec.getTargetArgs();
        if (tgtArgs.isEmpty()){  // try the default ctor
          tgtArgs = new ArrayList<ArgList>();
          tgtArgs.add(EMPTY_ARGS);
        }

        for (ArgList ta : tgtArgs){
          Constructor ctor = getCtor(ta);
          if (ctor != null){
            for (Object[] ctorArgs : ta.getArgCombinations()){
              tctx.setTargetObject(null);
              if (resolveFieldReferences(ctorArgs)){
                if (makeArgsCompatible(ctorArgs, ctor.getParameterTypes())){
                  Object tgt;

                  try {
                    tgt = ctor.newInstance(ctorArgs);
                  } catch (Throwable t){
                    malformed("instantiating target: " + t + " for args: " + argsToString(ctorArgs));
                    return;
                  }

                  tctx.setTargetObject(tgt);

                  for (Object[] mthArgs : tspec.getCallArgCombinations()) {
                    execute(m, tgt, ctorArgs, mthArgs, goal);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  TestSpec parseTestSpec (String spec){
    ANTLRStringStream input = new ANTLRStringStream(spec);
    TestSpecLexer lexer = new TestSpecLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    
    TestSpecParser parser = new TestSpecParser(tokens,
                                               new PrintWriter(System.err, true), tctx);
    
    try {
      TestSpec tspec = parser.testspec();
      return tspec;
      
    } catch (RecognitionException rx){
      malformed("spec did not parse: " + rx);
      return null;
    } catch (TestException tx){
      error(tx.getMessage());
      return null;      
    }
  }
  
  void showUsage() {
    System.err.println("usage: \"java gov.nasa.jpf.aprop.MethodTester <clsName>\"");
  }
  
  void showBanner() {
    log("");
    log("MethodTester - run @Test expressions, v1.0");
    log("target class:   " + targetCls.getName());
    if (mthPatterns != null){
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<mthPatterns.length; i++){
        if (i > 0) sb.append(',');
        sb.append(mthPatterns[i]);
      }
      log("test methods: " + sb);
    }
    log("==================================== tests:");
    
  }

  void showStatistics() {
    log("==================================== statistics:");
    log("tested methods: " + nTestMth + " of " + nTotalMth);
    log("tests:          " + nTests);
    log("passed:         " + nOk);
    if (nMalformed > 0){
      log("MALFORMED:      " + nMalformed);
    }
    if (nFailure > 0){
      log("FAILED:         " + nFailure);
    }    
  }
  
  public void run () {
    showBanner();
    
    for (Method m : targetCls.getDeclaredMethods()){
      nTotalMth++;

      if (mthMatcher == null || mthMatcher.matchesAny(m.getName())){
        
        Test t = m.getAnnotation(Test.class);
        if (t != null){
          log("-------- testing method: " + m);
          nTestMth++;

          for (String s : t.value()){
            log("--- test spec: \"" + s + "\"");
            log("");

            TestSpec tspec = parseTestSpec(s);
            if (tspec != null){
              test( m, tspec);
            }
          }
          
          log("");
        }
      }
    }
    
    showStatistics();
  }
  
  Class<?> getTargetCls() {
    return targetCls;
  }

  public int getNumberOfFailures() {
    return nFailure;
  }

  public int getNumberOfTests() {
    return nTests;
  }

  public static void main (String[] args){
    MethodTester tester = new MethodTester(args);
    if (tester.getTargetCls() != null){
      tester.run();
    }
  }
}
