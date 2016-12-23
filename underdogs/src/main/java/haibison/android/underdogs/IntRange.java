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
 * Integer range.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface IntRange {

    /**
     * Starting value.
     */
    long from() default Long.MIN_VALUE;

    /**
     * Flag indicating whether {@link #from()} is inclusive or not.
     */
    boolean fromInclusive() default true;

    /**
     * Ending value.
     */
    long to() default Long.MAX_VALUE;

    /**
     * Flag indicating whether {@link #to()} is inclusive or not.
     */
    boolean toInclusive() default true;

}