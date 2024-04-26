package com.savci.reviewms.review.controller;

import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.service.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam CompanyId companyId) {
        log.info("Fetch all reviews operation is done in the controller layer.");
        return ResponseEntity.ok(reviewService.getAllReviews(companyId));
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestParam CompanyId companyId, @RequestBody Review review) {
        Map<String, HttpStatus> responseMap = reviewService.createReview(companyId, review);
        Map.Entry<String, HttpStatus> statusEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Create review with company id %d operation is done in the controller layer.", companyId.getId()));
        return new ResponseEntity<>(statusEntry.getKey(), statusEntry.getValue());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable ReviewId reviewId) {
        Map<Object, HttpStatus> responseMap = reviewService.getReviewById(reviewId);
        Map.Entry<Object, HttpStatus> statusEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Fetch review with review id %d operation is done in the controller layer.", reviewId.getId()));
        return new ResponseEntity<>(statusEntry.getKey(), statusEntry.getValue());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReviewById(@PathVariable ReviewId reviewId, @RequestBody Review updatedReview) {
        Map<Object, HttpStatus> responseMap = reviewService.updateReviewById(reviewId, updatedReview);
        Map.Entry<Object, HttpStatus> statusEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Update review with review id %d operation is done in the controller layer.", reviewId.getId()));
        return new ResponseEntity<>(statusEntry.getKey(), statusEntry.getValue());
    }

    @GetMapping("/averageRating")
    public ResponseEntity<Double> getAverageRating(@RequestParam CompanyId companyId) {
        log.info("Fetch average rating of reviews operation is done in the controller layer.");
        return new ResponseEntity<>(reviewService.getAverageRatingById(companyId), HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReviewById(@PathVariable ReviewId reviewId) {
        Map<String, HttpStatus> responseMap = reviewService.deleteReviewById(reviewId);
        Map.Entry<String, HttpStatus> statusEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Delete review with review id %d operation is done in the controller layer.", reviewId.getId()));
        return new ResponseEntity<>(statusEntry.getKey(), statusEntry.getValue());
    }
}
