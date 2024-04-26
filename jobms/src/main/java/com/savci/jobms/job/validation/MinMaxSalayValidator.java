package com.savci.jobms.job.validation;

import com.savci.jobms.job.entity.Job;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinMaxSalayValidator implements ConstraintValidator<MinMaxSalaryValidation, Job> {

    @Override
    public void initialize(MinMaxSalaryValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Job job, ConstraintValidatorContext constraintValidatorContext) {
        return !this.checkMinMaxSalaryConstraintViolation(job);
    }

    private final boolean checkMinMaxSalaryConstraintViolation(Job job) {
        return job.getMinSalary() != null && job.getMaxSalary() != null && Long.parseLong(job.getMinSalary()) > Long.parseLong(job.getMaxSalary());
    }
}
