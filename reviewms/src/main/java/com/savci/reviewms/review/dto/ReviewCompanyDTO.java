package com.savci.reviewms.review.dto;

import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.validation.OnCreate;
import com.savci.reviewms.review.validation.OnUpdate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ReviewCompanyDTO {
    private Long id;
    private String title;
    private String description;
    private Double rating;
    @Valid
    @NotNull(message = "The company should not be null.")
    private Company company;

    public static ReviewCompanyDTO unpackReviewCompanyDTO(Review review, Company company) {
        return ReviewCompanyDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .description(review.getDescription())
                .rating(review.getRating())
                .company(company)
                .build();
    }
}
