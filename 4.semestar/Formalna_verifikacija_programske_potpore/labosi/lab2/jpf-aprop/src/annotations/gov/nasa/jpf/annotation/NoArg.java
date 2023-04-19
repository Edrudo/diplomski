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

package gov.nasa.jpf.annotation;

/**
 * class annotation to specify that instances are not allowed to be
 * used as arguments for the listed methods
 *
 * methods are given as patterns over classes, method names and signatures
 *
 * argument signatures are given with dotted Java type names, a missing
 * package qualifier is resolved either as current package or java.lang
 *
 * argument positions that are not allowed for this type are marked with
 * a preceeding '^'
 *
 * a type name postfix of '+' means this type or anyone that extends/implements it
 *
 * this annotation is transitive, i.e. all subclasses inherit it unless
 * they explicitly specify OverrideNoArg
 *
 * examples:
 *   NoArg("java.util.HashMap.add+(java.lang.Object,^java.lang.Object)")
 *   NoArg("x.y.*")
 */
public @interface NoArg {
  String[] value();
}
