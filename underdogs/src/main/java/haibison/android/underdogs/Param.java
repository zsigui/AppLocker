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
 * Parameter info.
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Param {

    /**
     * Parameter types.
     */
    enum Type {

        /**
         * Input.
         */
        INPUT,

        /**
         * Output.
         */
        OUTPUT,

        /**
         * Input and output.
         */
        IN_OUT;

    }//Type

    /**
     * Type.
     */
    @NonNull Type type();

    /**
     * Flag indicating that this (input) parameter is required or optional. Default is {@code false}.
     */
    boolean required() default false;

    /**
     * Data types.
     */
    @NonNull Class<?>[] dataTypes() default {};

    /**
     * Item data types, usefule in case data types are collections.
     */
    @NonNull Class<?>[] itemDataTypes() default {};

}