package com.savci.reviewms.review.dto;

import com.savci.reviewms.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewMessage {
    private Long id;
    private String title;
    private String description;
    private Double rating;
    private Long companyId;

    public ReviewMessage(Review review) {
        this.id = review.getId();
        this.description = review.getDescription();
        this.title = review.getTitle();
        this.rating = review.getRating();
        this.companyId = review.getCompanyId();
    }
}
