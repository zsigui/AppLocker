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
 * API level for classes, methods...
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Api {

    /**
     * API level.
     */
    int level() default 1;

    /**
     * Flag indicating that this API level is required or optional. Default is {@code true}.
     */
    boolean required() default true;

}