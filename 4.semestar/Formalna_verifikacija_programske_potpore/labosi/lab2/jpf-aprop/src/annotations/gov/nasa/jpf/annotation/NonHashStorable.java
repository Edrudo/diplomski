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

package gov.nasa.jpf.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java API specs demand consistent Object.equals(), Object.hashCode() and
 * Compareable.copareTo() implementations. I you only override one of them, but
 * leave the other ones untouched, chances are your objects will not be handled
 * correctly inside of (sorted) maps. On the other hand, sometimes it's not
 * worth the effort if all you have is a little auxilliary wrapper class that
 * is never supposed to be sortable. Mark such classes as @NonHashSortable, and
 * get warned if they are used as keys in Maps
 *
 * this annotation is a short version for the more generic
 *   @NoArg({"java.util.HashMap.put(java.lang.Object,^java.lang.Object)",
 *           "java.util.HashSet.add(^java.lang.Object)"})
 */

@Retention(RetentionPolicy.RUNTIME)

public @interface NonHashStorable {
}
