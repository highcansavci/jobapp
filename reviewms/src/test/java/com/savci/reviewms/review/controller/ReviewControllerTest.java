package com.savci.reviewms.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.service.ReviewService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

@WebMvcTest
@DisplayName("It should handle the controller layer operations successfully.")
public class ReviewControllerTest {
    @MockBean
    private ReviewService reviewService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Review firstReview;
    private Review secondReview;

    private CompanyId companyId;
    private ReviewId reviewId;

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
        firstReview.setCompanyId(1L);

        secondReview = new Review();
        secondReview.setId(2L);
        secondReview.setTitle("Title 2");
        secondReview.setDescription("Description 2");
        secondReview.setRating(2.5D);
        secondReview.setCompanyId(2L);

        companyId = new CompanyId();
        companyId.setId(1L);

        reviewId = new ReviewId();
        reviewId.setId(1L);

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
    @DisplayName("It should create a new review using the appropriate controller method.")
    void save() throws Exception {
        Mockito.when(reviewService.createReview(Mockito.any(CompanyId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap("The review is created successfully!", HttpStatus.CREATED));
        Assertions.assertEquals(mockMvc.perform(post("/reviews?companyId={companyId}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is created successfully!");
    }

    @Test
    @DisplayName("It should not create a new job using the appropriate controller method. It should return 404 status code.")
    void saveFailedNullJob() throws Exception {
        Mockito.when(reviewService.createReview(Mockito.any(CompanyId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(post("/reviews?companyId={companyId}", companyId.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should not create a new job using the appropriate controller method. It should throw ConstraintViolationException.")
    void saveFailedInvalidId() throws Exception {
        Mockito.when(reviewService.createReview(Mockito.any(CompanyId.class), Mockito.any(Review.class))).thenThrow(new ConstraintViolationException(companyIdConstraintViolations));
        mockMvc.perform(post("/reviews?companyId={companyId}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect(result -> Assertions.assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

//    @Test
//    @DisplayName("It should use the controller to fetch all the reviews by company id in the database.")
//    void getAllReviewsByCompanyId() throws Exception {
//        List<Review> reviewList = new ArrayList<>();
//        reviewList.add(firstReview);
//        reviewList.add(secondReview);
//        Mockito.when(reviewService.getAllReviews(companyId)).thenReturn(reviewList);
//        mockMvc.perform(get("/reviews?companyId={companyId}", companyId.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(reviewList.size()));
//    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the available jobs by company id in the database and throw the ConstraintViolationException.")
    void getAllReviewsByCompanyIdConstraintViolation() throws Exception {
        Mockito.when(reviewService.getAllReviews(Mockito.any(CompanyId.class))).thenThrow(new ConstraintViolationException(companyIdConstraintViolations));
        mockMvc.perform(get("/reviews?companyId={companyId}", companyId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> Assertions.assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific review by review id in the database and throw the ConstraintViolationException.")
    void getReviewByIdConstraintViolationException() throws Exception {
        Mockito.when(reviewService.getReviewById(Mockito.any(ReviewId.class))).thenThrow(new ConstraintViolationException(reviewIdConstraintViolations));
        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific review by review id in the database because of the invalid id.")
    void getReviewByIdInvalidId() throws Exception {
        Mockito.when(reviewService.getReviewById(Mockito.any(ReviewId.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific review by review id in the database because of the invalid review.")
    void getReviewByIdInvalidReview() throws Exception {
        Mockito.when(reviewService.getReviewById(Mockito.any(ReviewId.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to fetch the specific review by review id in the database.")
    void getReviewById() throws Exception {
        Mockito.when(reviewService.getReviewById(Mockito.any(ReviewId.class))).thenReturn(Collections.singletonMap(firstReview, HttpStatus.OK));
        mockMvc.perform(get("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value( "Title 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"))
                .andExpect(jsonPath("$.rating").value(3.5D))
                .andExpect(jsonPath("$.companyId").value(1L));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific review by review id in the database and throw the ConstraintViolationException.")
    void deleteReviewByIdConstraintViolationException() throws Exception {
        Mockito.when(reviewService.deleteReviewById(Mockito.any(ReviewId.class))).thenThrow(new ConstraintViolationException(reviewIdConstraintViolations));
        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific review by review id in the database because of the invalid id.")
    void deleteJobByIdInvalidId() throws Exception {
        Mockito.when(reviewService.deleteReviewById(Mockito.any(ReviewId.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to delete the specific review by review id in the database.")
    void deleteReviewById() throws Exception {
        Mockito.when(reviewService.deleteReviewById(Mockito.any(ReviewId.class))).thenReturn(Collections.singletonMap("The review is deleted successfully!", HttpStatus.OK));
        Assertions.assertEquals(mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is deleted successfully!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific review by review id in the database and throw the ConstraintViolationException.")
    void updateReviewByIdConstraintViolationException() throws Exception {
        Mockito.when(reviewService.updateReviewById(Mockito.any(ReviewId.class), Mockito.any(Review.class))).thenThrow(new ConstraintViolationException(reviewIdConstraintViolations));
        mockMvc.perform(put("/reviews/{id}", reviewId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific review by review id in the database because of the invalid id.")
    void updateReviewByIdInvalidId() throws Exception {
        Mockito.when(reviewService.updateReviewById(Mockito.any(ReviewId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(put("/reviews/{id}", reviewId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific review by review id in the database because of the invalid review.")
    void updatedReviewByIdInvalidReview() throws Exception {
        Mockito.when(reviewService.updateReviewById(Mockito.any(ReviewId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap("The review is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/reviews/{id}", reviewId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific review by review id in the database because of the invalid review.")
    void updatedReviewByIdInvalidUpdatedReview() throws Exception {
        Mockito.when(reviewService.updateReviewById(Mockito.any(ReviewId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap("The updated review is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/reviews/{id}", reviewId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The updated review is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to update the specific review by reivew id in the database.")
    void updateReviewById() throws Exception {
        Mockito.when(reviewService.updateReviewById(Mockito.any(ReviewId.class), Mockito.any(Review.class))).thenReturn(Collections.singletonMap(firstReview, HttpStatus.OK));
        mockMvc.perform(put("/reviews/{id}", reviewId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value( "Title 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"))
                .andExpect(jsonPath("$.rating").value(3.5D))
                .andExpect(jsonPath("$.companyId").value(1L));
    }
}
