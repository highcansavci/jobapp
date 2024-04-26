package com.savci.jobms.job.entity;

import com.savci.jobms.job.validation.DependentValidations;
import com.savci.jobms.job.validation.MinMaxSalaryValidation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@MinMaxSalaryValidation(groups = DependentValidations.class)
@GroupSequence({Job.class, DependentValidations.class})
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull(message = "Please provide a valid name, job name should not be null.")
    @NotBlank(message = "Please provide a valid name, job name should not be blank.")
    private String name;
    private String description;
    @Pattern(regexp = "[0-9]+", message = "Please provide a positive number for minimum salary.")
    private String minSalary;
    @Pattern(regexp = "[0-9]+", message = "Please provide a positive number for maximum salary.")
    private String maxSalary;
    @NotNull(message = "Please provide a valid location, the location should not be null.")
    @NotBlank(message = "Please provide a valid location, the location should not be blank.")
    private String location;
    @NotNull(message = "Please provide a valid id, company id should not be null.")
    private Long companyId;

    public void copy(Job other) {
        this.name = other.getName();
        this.description = other.getDescription();
        this.minSalary = other.getMinSalary();
        this.maxSalary = other.getMaxSalary();
        this.location = other.getLocation();
        this.companyId = other.getCompanyId();
    }

}
