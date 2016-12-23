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
 * List all permissions required by the object.
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Permissions {

    /**
     * Permission names.
     */
    @NonNull String[] names();

    /**
     * Flag indicating that <em>all</em> permissions are required or optional. Default is {@code true}.
     */
    boolean required() default true;

    /**
     * Description.
     */
    @NonNull String description() default "";

}