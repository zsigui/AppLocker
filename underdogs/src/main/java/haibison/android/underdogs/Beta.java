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
 * Indicating that the object is in beta state.
 */
@Documented
@Target({
        ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE,
        ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER
})
public @interface Beta {}