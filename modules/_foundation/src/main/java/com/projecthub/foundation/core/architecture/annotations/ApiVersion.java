package com.projecthub.core.architecture.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    int major();
    int minor() default 0;
    int patch() default 0;
    String since() default "";
    boolean deprecated() default false;
    String[] changes() default {};
    String migrationGuide() default "";
}