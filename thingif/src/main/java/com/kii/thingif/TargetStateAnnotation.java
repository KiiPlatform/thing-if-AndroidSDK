package com.kii.thingif;

import java.lang.annotation.*;

@Documented
@java.lang.annotation.Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetStateAnnotation {
    String stateFieldName() default "";
}
