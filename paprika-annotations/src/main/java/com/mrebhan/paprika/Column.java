package com.mrebhan.paprika;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Optional column declaration. Useful for DB versioning when columns are added
 */
@Retention(CLASS)
@Target(FIELD)
public @interface Column {
    int version() default 1;
}
