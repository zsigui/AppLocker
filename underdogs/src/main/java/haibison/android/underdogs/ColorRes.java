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
 * Indicates that the object should be an {@code R.color} resource.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface ColorRes {
}