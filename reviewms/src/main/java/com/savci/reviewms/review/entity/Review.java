package com.savci.reviewms.review.entity;

import com.savci.reviewms.review.validation.OnCreate;
import com.savci.reviewms.review.validation.OnUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    private String title;
    private String description;
    @Positive(message = "The rating should be positive.")
    @NotNull(message = "The rating should not be null.")
    @Max(value = 5, message = "The rating should be less than or equal to 5.")
    private Double rating;
    @Null(message = "Upon creation, company id should be null.", groups = {OnCreate.class})
    @NotNull(message = "Please provide a valid id, company id should not be null.", groups = {OnUpdate.class})
    private Long companyId;

    public void copy(Review other) {
        this.title = other.getTitle();
        this.description = other.getDescription();
        this.rating = other.getRating();
        this.companyId = other.getCompanyId();
    }
}
