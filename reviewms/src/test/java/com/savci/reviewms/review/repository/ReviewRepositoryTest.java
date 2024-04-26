package com.savci.reviewms.review.repository;

import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.entity.Review;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@DisplayName("It should handle the repository operations successfully.")
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepositoryTest;
    private Review firstReview;
    private Review secondReview;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstReview = new Review();
        firstReview.setTitle("Title 1");
        firstReview.setDescription("Description 1");
        firstReview.setRating(3.5D);
        firstReview.setCompanyId(1L);

        secondReview = new Review();
        secondReview.setTitle("Title 2");
        secondReview.setDescription("Description 2");
        secondReview.setRating(2.5D);
        secondReview.setCompanyId(2L);
    }

    @Test
    @DisplayName("It should save the review to the database.")
    void save() {
        Review savedReview = reviewRepositoryTest.save(firstReview);
        Assertions.assertAll("Reviews Saved In Database",
                () -> Assertions.assertNotNull(savedReview),
                () -> Assertions.assertNotSame(firstReview.getId(), null));
    }

    @Test
    @DisplayName("It should return the two reviews stored in the database.")
    void getAllReviews() {
        reviewRepositoryTest.save(firstReview);
        reviewRepositoryTest.save(secondReview);

        List<Review> reviewList = reviewRepositoryTest.findAll();
        Assertions.assertAll("Reviews In Database",
                () -> Assertions.assertNotNull(reviewList),
                () -> Assertions.assertEquals(reviewList.size() , 2),
                () -> Assertions.assertNotNull(reviewList.get(0).getId()),
                () -> Assertions.assertNotNull(reviewList.get(1).getId()),
                () -> Assertions.assertEquals(reviewList.get(0), firstReview),
                () -> Assertions.assertEquals(reviewList.get(1), secondReview));
    }

    @Test
    @DisplayName("It should return a review with company id 1 stored in the database.")
    void getAllReviewsByCompanyId() {
        reviewRepositoryTest.save(firstReview);
        reviewRepositoryTest.save(secondReview);

        List<Review> reviewList = reviewRepositoryTest.findByCompanyId(new CompanyId(1L).getId());
        Assertions.assertAll("Reviews In Database",
                () -> Assertions.assertNotNull(reviewList),
                () -> Assertions.assertEquals(reviewList.size() , 1),
                () -> Assertions.assertNotNull(reviewList.get(0).getId()),
                () -> Assertions.assertEquals(reviewList.get(0), firstReview));
    }

    @Test
    @DisplayName("It should obtain the specific review via its id in the database.")
    void getReviewById() {
        reviewRepositoryTest.save(firstReview);
        reviewRepositoryTest.save(secondReview);

        Review review = reviewRepositoryTest.findById(firstReview.getId()).orElse(null);
        Assertions.assertAll("Specific Review in the Database",
                () -> Assertions.assertNotNull(review),
                () -> Assertions.assertSame(review, firstReview));
    }

    @Test
    @DisplayName("It should completely update the specific review in the database.")
    void fullyUpdateReviewById() {
        reviewRepositoryTest.save(firstReview);
        Review existingReview = reviewRepositoryTest.findById(firstReview.getId()).orElse(null);
        Assertions.assertNotNull(existingReview);
        existingReview.copy(secondReview);
        Review updatedReview = reviewRepositoryTest.save(existingReview);

        Assertions.assertAll("Fully Updated Review in the Database",
                () -> Assertions.assertNotNull(updatedReview),
                () -> Assertions.assertEquals(updatedReview.getTitle(), "Title 2"),
                () -> Assertions.assertEquals(updatedReview.getDescription(), "Description 2"),
                () -> Assertions.assertEquals(updatedReview.getRating(), 2.5D),
                () -> Assertions.assertEquals(updatedReview.getCompanyId(), 2L));
    }

    @Test
    @DisplayName("It should partially update the specific review in the database.")
    void partiallyUpdateJobById() {
        reviewRepositoryTest.save(firstReview);
        Review existingReview = reviewRepositoryTest.findById(firstReview.getId()).orElse(null);
        Assertions.assertNotNull(existingReview);
        existingReview.setDescription("Description 2");
        existingReview.setRating(4D);
        Review updatedReview = reviewRepositoryTest.save(existingReview);

        Assertions.assertAll("Partially Updated Review in the Database",
                () -> Assertions.assertNotNull(updatedReview),
                () -> Assertions.assertEquals(updatedReview.getTitle(), "Title 1"),
                () -> Assertions.assertEquals(updatedReview.getDescription(), "Description 2"),
                () -> Assertions.assertEquals(updatedReview.getRating(), 4D),
                () -> Assertions.assertEquals(updatedReview.getCompanyId(), 1L));
    }

    @Test
    @DisplayName("It should delete the existing object by id from the database")
    void deleteReviewById() {
        reviewRepositoryTest.save(firstReview);
        Long id = firstReview.getId();
        reviewRepositoryTest.save(secondReview);

        reviewRepositoryTest.deleteById(id);
        Review review = reviewRepositoryTest.findById(id).orElse(null);
        List<Review> reviewList = reviewRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Review by Id in the Database",
                () -> Assertions.assertEquals(reviewList.size(), 1),
                () -> Assertions.assertNull(review),
                () -> Assertions.assertNotSame(review, firstReview));
    }

    @Test
    @DisplayName("It should delete the existing review object from the database.")
    void deleteReview() {
        reviewRepositoryTest.save(firstReview);
        Long id = firstReview.getId();
        reviewRepositoryTest.save(secondReview);

        reviewRepositoryTest.delete(firstReview);
        Review review = reviewRepositoryTest.findById(id).orElse(null);
        List<Review> reviewList = reviewRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Review in the Database",
                () -> Assertions.assertEquals(reviewList.size(), 1),
                () -> Assertions.assertNull(review),
                () -> Assertions.assertNotEquals(review, firstReview));
    }

}
