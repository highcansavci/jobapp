package com.savci.reviewms.review.service;
import java.util.List;
import java.util.Map;

import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.validation.OnCreate;
import com.savci.reviewms.review.validation.OnUpdate;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ReviewService {
    List<Review> getAllReviews(CompanyId companyId);
    @Validated(OnCreate.class)
    Map<String, HttpStatus> createReview(CompanyId companyId, @Valid Review review);
    Map<Object, HttpStatus> getReviewById(ReviewId reviewId);
    @Validated(OnUpdate.class)
    Map<Object, HttpStatus> updateReviewById(ReviewId reviewId, @Valid Review updatedReview);
    Map<String, HttpStatus> deleteReviewById(ReviewId reviewId);
    Double getAverageRatingById(CompanyId companyId);
    void deleteReviewByCompanyId(Long companyId);
}
