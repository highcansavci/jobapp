package com.savci.reviewms.review.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@JsonDeserialize(builder = Company.CompanyBuilder.class)
@Value
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;
    @NotNull(message = "Please provide a valid name, company name should not be null.")
    @NotBlank(message = "Please provide a valid name, company name should not be blank.")
    String name;
    String description;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CompanyBuilder {

    }
}
