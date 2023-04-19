
package javax.annotation.concurrent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * modeled after the JSR-305 GuardedBy field lock specification
 * values specify the lock object, which can be
 *
 * "this"                      : field owner object
 * <fieldname>                 : field of same object
 * <classname>.class           : class object
 * <classname>.<fieldname>     : static field
 * <methodName>()              : object returned by instance method
 * <classname>.<methodname>()  : object returned by static method
 *
 */

@Retention(RetentionPolicy.RUNTIME)

public @interface GuardedBy {
  String value();
}