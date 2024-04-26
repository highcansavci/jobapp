package com.savci.reviewms;

import com.savci.reviewms.review.entity.CompanyId;
import com.savci.reviewms.review.entity.Review;
import com.savci.reviewms.review.entity.ReviewId;
import com.savci.reviewms.review.repository.ReviewRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("It should handle the integration tests successfully.")
class ReviewmsApplicationTests {

	@LocalServerPort
	private int port;

	private String base_url;

	private String cr_url;

	private static final RestTemplate REST_TEMPLATE = new RestTemplate();

	@Autowired
	private ReviewRepository reviewRepository;

	private Review firstReview;
	private Review secondReview;
	private Review nullReviewTitle;
	private Review blankReviewTitle;
	private Review nullReviewDescription;
	private Review blankReviewDescription;
	private Review nullRating;
	private Review negativeRating;
	private Review ratingGreaterThan5;
	private Review notNullCompanyIdReview;
	private ReviewId nullReviewId;
	private ReviewId negativeReviewId;
	private ReviewId reviewId;
	private ReviewId outOfBoundsReviewId;
	private CompanyId nullCompanyId;
	private CompanyId negativeCompanyId;
	private CompanyId companyId;
	private CompanyId outOfBoundsCompanyId;



	@BeforeEach
	@DisplayName("It sets up the data for integration tests.")
	public void setupData() {
		base_url = "http://localhost:" + port + "/reviews";
		cr_url = base_url + "?companyId=";

		firstReview = new Review();
		firstReview.setTitle("Title 1");
		firstReview.setDescription("Description 1");
		firstReview.setRating(3.5D);

		secondReview = new Review();
		secondReview.setTitle("Job 2");
		secondReview.setDescription("Description 2");
		secondReview.setRating(2.5D);

		nullReviewTitle = new Review();
		nullReviewTitle.setTitle(null);
		nullReviewTitle.setDescription("Description 1");
		nullReviewTitle.setRating(3.5D);

		blankReviewTitle = new Review();
		blankReviewTitle.setTitle("");
		blankReviewTitle.setDescription("Description 2");
		blankReviewTitle.setRating(2.5D);

		nullReviewDescription = new Review();
		nullReviewDescription.setTitle("Title 1");
		nullReviewDescription.setDescription(null);
		nullReviewDescription.setRating(3.5D);

		blankReviewDescription = new Review();
		blankReviewDescription.setTitle("Job 2");
		blankReviewDescription.setDescription("");
		blankReviewDescription.setRating(2.5D);

		nullRating = new Review();
		nullRating.setId(1L);
		nullRating.setTitle("Title 1");
		nullRating.setDescription("Description 1");
		nullRating.setRating(null);

		negativeRating = new Review();
		negativeRating.setTitle("Title 1");
		negativeRating.setDescription("Description 1");
		negativeRating.setRating(-1D);

		ratingGreaterThan5 = new Review();
		ratingGreaterThan5.setTitle("Title 1");
		ratingGreaterThan5.setDescription("Description 1");
		ratingGreaterThan5.setRating(10D);

		notNullCompanyIdReview = new Review();
		notNullCompanyIdReview.setTitle("Title 1");
		notNullCompanyIdReview.setDescription("Description 1");
		notNullCompanyIdReview.setRating(3.5D);
		notNullCompanyIdReview.setCompanyId(1L);

		reviewId = new ReviewId(1L);
		nullReviewId = new ReviewId();
		negativeReviewId = new ReviewId(-1L);
		outOfBoundsReviewId = new ReviewId(Long.MAX_VALUE);

		companyId = new CompanyId(1L);
		nullCompanyId = new CompanyId();
		negativeCompanyId = new CompanyId(-1L);
		outOfBoundsCompanyId = new CompanyId(Long.MAX_VALUE);
	}

	@AfterEach
	public void deleteEntities() {
		reviewRepository.deleteAll();
	}

	@Test
	@DisplayName("It should create the review with completely filled attributes successfully!")
	void createReview() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the review with null review rating!")
	void createNullReviewRating() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), nullRating, String.class));
		Assertions.assertAll("Failed Review Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should not be null.")));
	}

	@Test
	@DisplayName("It should not create the review with negative review rating!")
	void createNegativeReviewRating() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), negativeRating, String.class));
		Assertions.assertAll("Failed Review Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should be positive.")));
	}

	@Test
	@DisplayName("It should not create the review with review rating which exceeds the limit!")
	void createGreaterThan5ReviewRating() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), ratingGreaterThan5, String.class));
		Assertions.assertAll("Failed Review Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should be less than or equal to 5.")));
	}

	@Test
	@DisplayName("It should create the title with null review title successfully!")
	void createNullReviewTitle() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), nullReviewTitle, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should create the title with blank review title successfully!")
	void createBlankReviewTitle() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), blankReviewTitle, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should create the job with null review description successfully!")
	void createNullReviewDescription() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), nullReviewDescription, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should create the job with blank review description successfully!")
	void createBlankReviewDescription() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), blankReviewDescription, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the review with null company id!")
	void createNullCompanyId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", nullCompanyId.getId()), firstReview, String.class));
		Assertions.assertAll("Failed Review Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should not be null.")));
	}

	@Test
	@DisplayName("It should not create the review with negative company id!")
	void createNegativeCompanyId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", negativeCompanyId.getId()), firstReview, String.class));
		Assertions.assertAll("Failed Review Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should be positive.")));
	}

	@Test
	@DisplayName("It should not create the review with out of bounds company id!")
	void createOutOfBoundsCompanyId() {
		String response = REST_TEMPLATE.postForObject(cr_url + String.format("%d", outOfBoundsCompanyId.getId()), firstReview, String.class);
		Assertions.assertAll("Successful Review Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The review is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the job with existing job company id!")
	void createExistingReviewCompanyId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), notNullCompanyIdReview, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Upon creation, company id should be null.")));
	}

	@Test
	@DisplayName("It should fetch all of the reviews in the database.")
	void getAllReviews() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		firstReview.setCompanyId(companyId.getId());
		secondReview.setCompanyId(companyId.getId());
		Assertions.assertAll("Successful Reviews Retrieval",
				() -> Assertions.assertNotNull(reviewList),
				() -> Assertions.assertEquals(reviewList.size() , 2),
				() -> Assertions.assertNotNull(reviewList.get(0).getId()),
				() -> Assertions.assertNotNull(reviewList.get(1).getId()),
				() -> Assertions.assertEquals(reviewList.get(0), firstReview),
				() -> Assertions.assertEquals(reviewList.get(1), secondReview));
	}

	@Test
	@DisplayName("It should fetch the specific review given its id in the database.")
	void getReviewById() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(0).getId());
		firstReview.setCompanyId(companyId.getId());
		Review review = REST_TEMPLATE.getForObject(base_url + "/" + reviewId.getId(), Review.class);
		Assertions.assertAll("Successful Review Retrieval",
				() -> Assertions.assertNotNull(review),
				() -> Assertions.assertEquals(review, firstReview));
	}

	@Test
	@DisplayName("It should not fetch the specific review given its id in the database because of the null id constraint violation.")
	void getReviewByIdNullReviewId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + nullReviewId.getId(), Review.class));
		Assertions.assertAll("Failed Review Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review id should not be null.")));
	}

	@Test
	@DisplayName("It should not fetch the specific review given its id in the database because of the negative id constraint violation.")
	void getReviewByIdNegativeReviewId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + negativeReviewId.getId(), Review.class));
		Assertions.assertAll("Failed Job Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review id should be positive.")));
	}

	@Test
	@DisplayName("It should not fetch the specific review given its id in the database because of the out of bounds id.")
	void getReviewByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(0).getId() + reviewList.size());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + reviewId.getId(), Review.class));
		Assertions.assertAll("Failed Review Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review is not found!")));
	}

	@Test
	@DisplayName("It should update the specific review with completely filled attributes successfully!")
	void updateReview() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		REST_TEMPLATE.put(base_url + "/{id}", firstReview, reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Review Update",
				() -> Assertions.assertNotNull(finalReviewList),
				() -> Assertions.assertEquals(finalReviewList.size() , 2),
				() -> Assertions.assertNotNull(finalReviewList.get(0).getId()),
				() -> Assertions.assertNotNull(finalReviewList.get(1).getId()),
				() -> Assertions.assertEquals(finalReviewList.get(0), firstReview),
				() -> Assertions.assertEquals(finalReviewList.get(1), firstReview));
	}

	@Test
	@DisplayName("It should update the review with null review title!")
	void updateNullReviewTitle() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		nullReviewTitle.setCompanyId(companyId.getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullReviewTitle, reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Review Update",
				() -> Assertions.assertNotNull(finalReviewList),
				() -> Assertions.assertEquals(finalReviewList.size() , 2),
				() -> Assertions.assertNotNull(finalReviewList.get(0).getId()),
				() -> Assertions.assertNotNull(finalReviewList.get(1).getId()),
				() -> Assertions.assertEquals(finalReviewList.get(0), firstReview),
				() -> Assertions.assertEquals(finalReviewList.get(1), nullReviewTitle));
	}

	@Test
	@DisplayName("It should update the review with blank review title!")
	void updateBlankReviewTitle() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		blankReviewTitle.setCompanyId(companyId.getId());
		REST_TEMPLATE.put(base_url + "/{id}", blankReviewTitle, reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Review Update",
				() -> Assertions.assertNotNull(finalReviewList),
				() -> Assertions.assertEquals(finalReviewList.size() , 2),
				() -> Assertions.assertNotNull(finalReviewList.get(0).getId()),
				() -> Assertions.assertNotNull(finalReviewList.get(1).getId()),
				() -> Assertions.assertEquals(finalReviewList.get(0), firstReview),
				() -> Assertions.assertEquals(finalReviewList.get(1), blankReviewTitle));
	}

	@Test
	@DisplayName("It should update the review with null review description!")
	void updateNullReviewDescription() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		nullReviewDescription.setCompanyId(companyId.getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullReviewDescription, reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Review Update",
				() -> Assertions.assertNotNull(finalReviewList),
				() -> Assertions.assertEquals(finalReviewList.size() , 2),
				() -> Assertions.assertNotNull(finalReviewList.get(0).getId()),
				() -> Assertions.assertNotNull(finalReviewList.get(1).getId()),
				() -> Assertions.assertEquals(finalReviewList.get(0), firstReview),
				() -> Assertions.assertEquals(finalReviewList.get(1), nullReviewDescription));
	}

	@Test
	@DisplayName("It should update the review with blank review description!")
	void updateBlankReviewDescription() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		blankReviewDescription.setCompanyId(companyId.getId());
		REST_TEMPLATE.put(base_url + "/{id}", blankReviewDescription, reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Review Update",
				() -> Assertions.assertNotNull(finalReviewList),
				() -> Assertions.assertEquals(finalReviewList.size() , 2),
				() -> Assertions.assertNotNull(finalReviewList.get(0).getId()),
				() -> Assertions.assertNotNull(finalReviewList.get(1).getId()),
				() -> Assertions.assertEquals(finalReviewList.get(0), firstReview),
				() -> Assertions.assertEquals(finalReviewList.get(1), blankReviewDescription));
	}

	@Test
	@DisplayName("It should not update the review with null review rating!")
	void updateNullReviewRating() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		nullRating.setCompanyId(companyId.getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", nullRating, reviewId.getId()));
		Assertions.assertAll("Failed Review Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should not be null.")));
	}

	@Test
	@DisplayName("It should not update the review with negative review rating!")
	void updateNegativeReviewRating() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		negativeRating.setCompanyId(companyId.getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", negativeRating, reviewId.getId()));
		Assertions.assertAll("Failed Review Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should be positive.")));
	}

	@Test
	@DisplayName("It should not update the review with the review rating which is greater than 5!")
	void updateExceedingReviewRating() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		ratingGreaterThan5.setCompanyId(companyId.getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", ratingGreaterThan5, reviewId.getId()));
		Assertions.assertAll("Failed Review Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The rating should be less than or equal to 5.")));
	}

	@Test
	@DisplayName("It should not update the review with null review company id!")
	void updateNullJobCompanyId() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		notNullCompanyIdReview.setCompanyId(null);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", notNullCompanyIdReview, reviewId.getId()));
		Assertions.assertAll("Failed Review Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid id, company id should not be null.")));
	}

	@Test
	@DisplayName("It should not update the specific review given its id in the database because of the negative id constraint violation.")
	void updateReviewByIdNegativeReviewId() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstReview, negativeReviewId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review id should be positive.")));
	}

	@Test
	@DisplayName("It should not update the specific review given its id in the database because of the out of bounds id.")
	void updateReviewByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		firstReview.setCompanyId(companyId.getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstReview, outOfBoundsReviewId.getId()));
		Assertions.assertAll("Failed Review Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review is not found!")));
	}

	@Test
	@DisplayName("It should not delete the specific review given its id in the database because of the negative id constraint violation.")
	void deleteReviewByIdNegativeReviewId() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", negativeReviewId.getId()));
		Assertions.assertAll("Failed Review Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review id should be positive.")));
	}

	@Test
	@DisplayName("It should not delete the specific review given its id in the database because of the out of bounds id.")
	void deleteReviewByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", outOfBoundsReviewId.getId()));
		Assertions.assertAll("Failed Review Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The review is not found!")));
	}


	@Test
	@DisplayName("It should delete the specific job given its id in the database.")
	void deleteJobById() {
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), firstReview, String.class);
		REST_TEMPLATE.postForObject(cr_url + String.format("%d", companyId.getId()), secondReview, String.class);
		ParameterizedTypeReference<List<Review>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(cr_url + String.format("%d", companyId.getId())).accept(MediaType.APPLICATION_JSON).build();
		List<Review> reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		reviewId.setId(reviewList.get(1).getId());
		REST_TEMPLATE.delete(base_url + "/{id}", reviewId.getId());
		reviewList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert reviewList != null;
		secondReview.setCompanyId(companyId.getId());
		List<Review> finalReviewList = reviewList;
		Assertions.assertAll("Successful Job Deletion",
				() -> Assertions.assertEquals(finalReviewList.size(), 1),
				() -> Assertions.assertNotEquals(finalReviewList.get(0), secondReview));
	}


}
