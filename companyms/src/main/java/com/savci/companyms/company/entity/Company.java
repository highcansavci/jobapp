package com.savci.companyms.company.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull(message = "Please provide a valid name, company name should not be null.")
    @NotBlank(message = "Please provide a valid name, company name should not be blank.")
    private String name;
    private String description;
    private Double averageRating;

    public void copy(Company other) {
        this.name = other.getName();
        this.description = other.getDescription();
    }
}
