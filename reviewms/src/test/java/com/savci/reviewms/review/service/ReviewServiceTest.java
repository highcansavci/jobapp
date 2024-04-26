package com.savci.reviewms.review.service;

import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.messaging.ReviewMessageProducer;
import com.savci.reviewms.review.repository.ReviewRepository;
import com.savci.reviewms.review.service.impl.ReviewServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("It should handle the service layer operations successfully.")
public class ReviewServiceTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private Validator validator;
    @Mock
    private ReviewMessageProducer reviewMessageProducer;

    private Review firstReview;
    private Review secondReview;

    private CompanyId companyId;
    private CompanyId nullCompanyId;
    private CompanyId negativeCompanyId;
    private CompanyId outOfBoundsCompanyId;

    private ReviewId nullReviewId;
    private ReviewId negativeReviewId;
    private ReviewId reviewId;
    private ReviewId outOfBoundsReviewId;

    private Set<ConstraintViolation<CompanyId>> companyIdConstraintViolations;
    private Set<ConstraintViolation<ReviewId>> reviewIdConstraintViolations;


    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstReview = new Review();
        firstReview.setId(1L);
        firstReview.setTitle("Title 1");
        firstReview.setDescription("Description 1");
        firstReview.setRating(3.5D);

        secondReview = new Review();
        secondReview.setId(2L);
        secondReview.setTitle("Title 2");
        secondReview.setDescription("Description 2");
        secondReview.setRating(2.5D);

        companyId = new CompanyId();
        companyId.setId(1L);

        nullCompanyId = new CompanyId(null);
        negativeCompanyId = new CompanyId(-2L);
        outOfBoundsCompanyId = new CompanyId(Long.MAX_VALUE);

        reviewId = new ReviewId();
        reviewId.setId(1L);
        nullReviewId = new ReviewId(null);
        negativeReviewId = new ReviewId(-2L);
        outOfBoundsReviewId = new ReviewId(Long.MAX_VALUE);

        companyIdConstraintViolations = new HashSet<>();
        companyIdConstraintViolations.add(new ConstraintViolation<CompanyId>() {
            @Override
            public String getMessage() {
                return "Dummy message";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public CompanyId getRootBean() {
                return null;
            }

            @Override
            public Class<CompanyId> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        });

        reviewIdConstraintViolations = new HashSet<>();
        reviewIdConstraintViolations.add(new ConstraintViolation<ReviewId>() {
            @Override
            public String getMessage() {
                return "Dummy message";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public ReviewId getRootBean() {
                return null;
            }

            @Override
            public Class<ReviewId> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        });
    }

    @Test
    @DisplayName("The review service should save the review to the database and return success message with 201 status code.")
    void save() {
        Mockito.doNothing().when(reviewMessageProducer).sendMessage(firstReview);
        Map<String, HttpStatus> statusMap = reviewService.createReview(companyId, firstReview);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is created successfully!", HttpStatus.CREATED));
    }

    @Test
    @DisplayName("The review service should not save the review to the database and return ConstraintViolationException with 432 status code.")
    void tryToSaveReviewNullId() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.createReview(nullCompanyId, firstReview));
    }

    @Test
    @DisplayName("The review service should not save the review to the database and return ConstraintViolationException with 432 status code.")
    void tryToSaveReviewNegativeId() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.createReview(negativeCompanyId, firstReview));
    }

    @Test
    @DisplayName("The review service should not save the review to the database and return review is not found message with 404 status code.")
    void tryToSaveNullReview() {
        Map<String, HttpStatus> statusMap = reviewService.createReview(companyId, null);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is not valid!", HttpStatus.BAD_REQUEST));
    }


    @Test
    @DisplayName("The review service should not fetch all reviews by company id in the database and it should throw ConstraintViolationException.")
    void getAllReviewsByCompanyIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.getAllReviews(nullCompanyId));
    }

    @Test
    @DisplayName("The review service should not fetch all reviews by company id in the database and it should throw ConstraintViolationException.")
    void getAllReviewsByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.getAllReviews(companyId));
    }

    
    @Test
    @DisplayName("The review service should not get the specific review by id in the database and it should throw ConstraintViolationException.")
    void getReviewByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(reviewIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.getReviewById(reviewId));
    }

    @Test
    @DisplayName("The review service should not get the specific review by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void getReviewByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        Map<?, HttpStatus> statusMap = reviewService.getReviewById(outOfBoundsReviewId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The review service should get the specific review by id in the database and it should return success message with 200 status code.")
    void getReviewbById() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> (Long) invocationOnMock.getArgument(0) == 1L ? Optional.of(firstReview) : Optional.empty());
        Map<?, HttpStatus> statusMap = reviewService.getReviewById(reviewId);
        Assertions.assertEquals(statusMap, Collections.singletonMap(firstReview, HttpStatus.OK));
    }

    @Test
    @DisplayName("The review service should not update the specific review by id in the database and it should throw ConstraintViolationException.")
    void updateReviewByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(reviewIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.updateReviewById(reviewId, firstReview));
    }

    @Test
    @DisplayName("The review service should not update the specific review by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void updateReviewByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        Map<?, HttpStatus> statusMap = reviewService.updateReviewById(outOfBoundsReviewId, firstReview);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The review service should not save the review to the database and return review is not found message with 404 status code.")
    void tryToUpdateNullReview() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Map<?, HttpStatus> statusMap = reviewService.updateReviewById(reviewId, null);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is not valid!", HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("The review service should update the specific review by id in the database and it should return success message with 200 status code.")
    void updateReviewById() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> (Long) invocationOnMock.getArgument(0) == 1L ? Optional.of(firstReview) : Optional.empty());
        Mockito.doNothing().when(reviewMessageProducer).sendMessage(firstReview);
        Map<?, HttpStatus> statusMap = reviewService.updateReviewById(reviewId, secondReview);
        secondReview.setId(1L);
        Assertions.assertEquals(statusMap, Collections.singletonMap(secondReview, HttpStatus.OK));
    }

    @Test
    @DisplayName("The review service should not delete the specific review by id in the database and it should throw ConstraintViolationException.")
    void deleteReviewByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(reviewIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> reviewService.deleteReviewById(reviewId));
    }

    @Test
    @DisplayName("The review service should not delete the specific review by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void deleteReviewByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        Map<?, HttpStatus> statusMap = reviewService.deleteReviewById(outOfBoundsReviewId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The review service should delete the specific review by id in the database and it should return success message with 200 status code.")
    void deleteJobById() {
        Mockito.when(validator.validate(Mockito.any(ReviewId.class))).thenReturn(Collections.emptySet());
        Mockito.when(reviewRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(reviewRepository).deleteById(reviewId.getId());
        Map<?, HttpStatus> statusMap = reviewService.deleteReviewById(reviewId);
        Mockito.verify(reviewRepository, Mockito.times(1)).deleteById(reviewId.getId());
        Assertions.assertEquals(statusMap, Collections.singletonMap("The review is deleted successfully!", HttpStatus.OK));
    }
}
