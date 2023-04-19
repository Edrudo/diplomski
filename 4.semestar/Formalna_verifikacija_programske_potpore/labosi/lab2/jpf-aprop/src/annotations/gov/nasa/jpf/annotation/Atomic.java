package gov.nasa.jpf.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Linearizability is a safery property of concurrent 
 * systems, over sequence of events corresponding to 
 * the invocations of the operations on shared objects.
 * 
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Atomic {
}
