//
// Copyright (C) 2010 United States Government as represented by the
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

package gov.nasa.jpf.aprop.listener;

import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.util.MethodSpec;

/**
 * a specialized NoArgChecker for classes that cannot be kept in
 * HashMaps or HashSets because they do not implement consistent
 * equals()/hashCode()/compareTo() triples
 */
public class NonHashStorableChecker extends ArgChecker {

  // the reference argument type to check
  static final String NON_HASH_STORABLE = "gov.nasa.jpf.annotation.NonHashStorable";
  
  // the calls that are not allowed to have arguments of this type
  // NOTE - this could not be enforced by the compiler since the argument type is generic
  static MethodSpec ms1 = MethodSpec.createMethodSpec("java.util.HashMap.put(java.lang.Object,^java.lang.Object)");
  static MethodSpec ms2 = MethodSpec.createMethodSpec("java.util.HashSet.add(^java.lang.Object)");

  protected ForbiddenMethods getAttr (ClassInfo ci){
    AnnotationInfo ai = ci.getAnnotation(NON_HASH_STORABLE);
    if ( ai != null){
      ForbiddenMethods a = new ForbiddenMethods();
      a.add(ms1);
      a.add(ms2);
      return a;
    }

    return null;
  }

}
