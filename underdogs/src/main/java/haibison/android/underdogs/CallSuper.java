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
 * Indicates that you should call super method.
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface CallSuper {
}