package com.savci.jobms.job.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinMaxSalayValidator.class)
public @interface MinMaxSalaryValidation {
    String message() default "The minimum salary should not be greater than the maximum salary.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
