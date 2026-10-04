package com.example.lab1.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTaskCompletionValidator.class)
public @interface ValidTaskCompletion {

    String message() default
            "Task with DONE status must have a description";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}