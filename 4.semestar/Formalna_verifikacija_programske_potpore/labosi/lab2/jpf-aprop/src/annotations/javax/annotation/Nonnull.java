
package javax.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * this is supposed to model the upcoming JSR305 javax.annotation.Nonnull annotation
 * (to be replaced when this becomes available)
 *
 * @Nonnull means that a method parameter or method return value is not allowed to
 * be null. For a parameter, this is the callers responsibility. For a
 * return value, the callee has to guarantee it.
 *
 * We extend this towards fields, which is not synonym to final since
 * @Nonnull fields can change their values later-on, as long as its not the
 * null value
 *
 * This is also checked by the Findbugs(tm) static analyzer, but only at a
 * method local level.
 *
 * Note that javax.annotation.CheckForNull is a method local property, so we
 * leave this for static analyzers
 */

@Retention(RetentionPolicy.RUNTIME)

public @interface Nonnull {

}
