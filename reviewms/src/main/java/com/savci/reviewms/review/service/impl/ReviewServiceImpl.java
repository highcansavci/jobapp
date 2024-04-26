package com.savci.reviewms.review.service.impl;

import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.messaging.ReviewMessageProducer;
import com.savci.reviewms.review.repository.ReviewRepository;
import com.savci.reviewms.review.service.ReviewService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private Validator validator;
    private ReviewRepository reviewRepository;
    private ReviewMessageProducer reviewMessageProducer;

    @Override
    public List<Review> getAllReviews(CompanyId companyId) {
        Set<ConstraintViolation<CompanyId>> companyIdConstraintViolation = validator.validate(companyId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the company id is not met when fetching reviews in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        log.info("Fetch all the review data operation is successfully done in the service layer!");
        return reviewRepository.findByCompanyId(companyId.getId());
    }

    @Override
    public Map<String, HttpStatus> createReview(CompanyId companyId, Review review) {
        Set<ConstraintViolation<CompanyId>> companyIdConstraintViolation = validator.validate(companyId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the company id is not met when creating review in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        if(review == null) {
            log.error("The review is not valid upon the creation in the service layer!");
            return Collections.singletonMap("The review is not valid!", HttpStatus.BAD_REQUEST);
        }
        review.setCompanyId(companyId.getId());
        reviewRepository.save(review);
        reviewMessageProducer.sendMessage(review);
        log.info("The review is created successfully in the service layer!");
        return Collections.singletonMap("The review is created successfully!", HttpStatus.CREATED);
    }

    @Override
    public Map<Object, HttpStatus> getReviewById(ReviewId reviewId) {
        Set<ConstraintViolation<ReviewId>> companyIdConstraintViolation = validator.validate(reviewId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the company id is not met when fetching the review in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        if(!reviewRepository.existsById(reviewId.getId())) {
            log.error("The review is not found in the review repository when querying in the service layer!");
            return Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND);
        }
        Review review = reviewRepository.findById(reviewId.getId()).orElse(null);
        assert review != null;
        log.info(String.format("The review data with the id %d is fetched successfully in the service layer!", reviewId.getId()));
        return Collections.singletonMap(review, HttpStatus.OK);
    }

    @Override
    public Map<Object, HttpStatus> updateReviewById(ReviewId reviewId, @Valid Review updatedReview) {
        Set<ConstraintViolation<ReviewId>> companyIdConstraintViolation = validator.validate(reviewId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the review id is not met when updating in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        if(updatedReview == null) {
            log.error("The review is not valid upon the update in the service layer!");
            return Collections.singletonMap("The review is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!reviewRepository.existsById(reviewId.getId())) {
            log.error("The review is not found in the review repository when querying in the service layer!");
            return Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND);
        }
        Review review = reviewRepository.findById(reviewId.getId()).orElse(null);
        assert review != null;
        review.copy(updatedReview);
        reviewRepository.save(review);
        reviewMessageProducer.sendMessage(review);
        log.info("The review is updated successfully in the service layer!");
        return Collections.singletonMap(review, HttpStatus.OK);
    }

    @Override
    public Map<String, HttpStatus> deleteReviewById(ReviewId reviewId) {
        Set<ConstraintViolation<ReviewId>> companyIdConstraintViolation = validator.validate(reviewId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the review id is not met when deleting in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        if(!reviewRepository.existsById(reviewId.getId())) {
            log.error("The review is not found in the review repository when querying in the service layer!");
            return Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND);
        }
        reviewRepository.deleteById(reviewId.getId());
        log.info("The review is deleted successfully in the service layer!");
        return Collections.singletonMap("The review is deleted successfully!", HttpStatus.OK);
    }

    @Override
    public Double getAverageRatingById(CompanyId companyId) {
        Set<ConstraintViolation<CompanyId>> companyIdConstraintViolation = validator.validate(companyId);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the company id is not met when getting average rating in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        log.info("Fetch average rating of reviews operation is successfully done in the service layer!");
        return reviewRepository.findByCompanyId(companyId.getId()).stream().mapToDouble(Review::getRating).average().orElse(0.0D);
    }

    @Override
    @Transactional
    public void deleteReviewByCompanyId(Long companyId) {
        CompanyId companyId_ = new CompanyId(companyId);
        Set<ConstraintViolation<CompanyId>> companyIdConstraintViolation = validator.validate(companyId_);
        if(!companyIdConstraintViolation.isEmpty()) {
            log.error("Constraints of the company id is not met when deleting reviews in the service layer!");
            throw new ConstraintViolationException(companyIdConstraintViolation);
        }
        reviewRepository.deleteByCompanyId(companyId);
    }
}
