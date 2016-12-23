/*
 * Copyright (c) 2016 Hai Bison
 *
 * See the file LICENSE at the root directory of this project for copying permission.
 */

package haibison.android.underdogs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Float range.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface FloatRange {

    /**
     * Starting value.
     */
    double from() default Double.NEGATIVE_INFINITY;

    /**
     * Flag indicating whether {@link #from()} is inclusive or not.
     */
    boolean fromInclusive() default true;

    /**
     * Ending value.
     */
    double to() default Double.POSITIVE_INFINITY;

    /**
     * Flag indicating whether {@link #to()} is inclusive or not.
     */
    boolean toInclusive() default true;

}