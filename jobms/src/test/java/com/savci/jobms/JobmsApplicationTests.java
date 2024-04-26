package com.savci.jobms;

import com.savci.jobms.job.client.CompanyClient;
import com.savci.jobms.job.client.ReviewClient;
import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import com.savci.jobms.job.external.Company;
import com.savci.jobms.job.external.Review;
import com.savci.jobms.job.repository.JobRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.utils.CircuitBreakerUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("It should handle the integration tests successfully.")
class JobmsApplicationTests {

	@LocalServerPort
	private int port;

	private String base_url;

	private static final RestTemplate REST_TEMPLATE = new RestTemplate();

	@Autowired
	private JobRepository jobRepository;
	@MockBean
	private CompanyClient companyClient;
	@MockBean
	private ReviewClient reviewClient;

	private Job firstJob;
	private Job secondJob;
	private Job nullJobName;
	private Job blankJobName;
	private Job nullJobDescription;
	private Job blankJobDescription;
	private Job nullMinSalary;
	private Job blankMinSalary;
	private Job alphaMinSalary;
	private Job alphanumMinSalary;
	private Job nullMaxSalary;
	private Job blankMaxSalary;
	private Job alphaMaxSalary;
	private Job alphanumMaxSalary;
	private Job minMaxSalaryViolation;
	private Job nullJobLocation;
	private Job blankJobLocation;
	private Job nullCompanyId;
	private JobId nullJobId;
	private JobId negativeJobId;
	private JobId jobId;
	private JobId outOfBoundsJobId;

	private Company company;

	private Review firstReview;
	private Review secondReview;

	private JobDTO firstJobDTO;
	private JobDTO secondJobDTO;

	@BeforeEach
	@DisplayName("It sets up the data for integration tests.")
	public void setupData() {
		base_url = "http://localhost:" + port + "/jobs";

		firstJob = new Job();
		firstJob.setName("Job 1");
		firstJob.setDescription("Description 1");
		firstJob.setMinSalary("30000");
		firstJob.setMaxSalary("40000");
		firstJob.setLocation("Location 1");
		firstJob.setCompanyId(1L);

		secondJob = new Job();
		secondJob.setName("Job 2");
		secondJob.setDescription("Description 2");
		secondJob.setMinSalary("50000");
		secondJob.setMaxSalary("60000");
		secondJob.setLocation("Location 2");
		secondJob.setCompanyId(2L);

		nullJobName = new Job();
		nullJobName.setId(1L);
		nullJobName.setName(null);
		nullJobName.setDescription("Description 1");
		nullJobName.setMinSalary("30000");
		nullJobName.setMaxSalary("40000");
		nullJobName.setLocation("Location 1");
		nullJobName.setCompanyId(1L);

		blankJobName = new Job();
		blankJobName.setId(1L);
		blankJobName.setName("");
		blankJobName.setDescription("Description 1");
		blankJobName.setMinSalary("30000");
		blankJobName.setMaxSalary("40000");
		blankJobName.setLocation("Location 1");
		blankJobName.setCompanyId(1L);

		nullJobDescription = new Job();
		nullJobDescription.setId(1L);
		nullJobDescription.setName("Job 1");
		nullJobDescription.setDescription(null);
		nullJobDescription.setMinSalary("30000");
		nullJobDescription.setMaxSalary("40000");
		nullJobDescription.setLocation("Location 1");
		nullJobDescription.setCompanyId(1L);

		blankJobDescription = new Job();
		blankJobDescription.setId(1L);
		blankJobDescription.setName("Job 1");
		blankJobDescription.setDescription("");
		blankJobDescription.setMinSalary("30000");
		blankJobDescription.setMaxSalary("40000");
		blankJobDescription.setLocation("Location 1");
		blankJobDescription.setCompanyId(1L);

		nullMinSalary = new Job();
		nullMinSalary.setId(1L);
		nullMinSalary.setName("Job 1");
		nullMinSalary.setDescription("Description 1");
		nullMinSalary.setMinSalary(null);
		nullMinSalary.setMaxSalary("40000");
		nullMinSalary.setLocation("Location 1");
		nullMinSalary.setCompanyId(1L);

		blankMinSalary = new Job();
		blankMinSalary.setId(1L);
		blankMinSalary.setName("Job 1");
		blankMinSalary.setDescription("Description 1");
		blankMinSalary.setMinSalary("");
		blankMinSalary.setMaxSalary("40000");
		blankMinSalary.setLocation("Location 1");
		blankMinSalary.setCompanyId(1L);

		alphaMinSalary = new Job();
		alphaMinSalary.setId(1L);
		alphaMinSalary.setName("Job 1");
		alphaMinSalary.setDescription("Description 1");
		alphaMinSalary.setMinSalary("abcde");
		alphaMinSalary.setMaxSalary("40000");
		alphaMinSalary.setLocation("Location 1");
		alphaMinSalary.setCompanyId(1L);

		alphanumMinSalary = new Job();
		alphanumMinSalary.setId(1L);
		alphanumMinSalary.setName("Job 1");
		alphanumMinSalary.setDescription("Description 1");
		alphanumMinSalary.setMinSalary("a3db2");
		alphanumMinSalary.setMaxSalary("40000");
		alphanumMinSalary.setLocation("Location 1");
		alphanumMinSalary.setCompanyId(1L);

		nullMaxSalary = new Job();
		nullMaxSalary.setId(1L);
		nullMaxSalary.setName("Job 1");
		nullMaxSalary.setDescription("Description 1");
		nullMaxSalary.setMaxSalary(null);
		nullMaxSalary.setMinSalary("40000");
		nullMaxSalary.setLocation("Location 1");
		nullMaxSalary.setCompanyId(1L);

		blankMaxSalary = new Job();
		blankMaxSalary.setId(1L);
		blankMaxSalary.setName("Job 1");
		blankMaxSalary.setDescription("Description 1");
		blankMaxSalary.setMaxSalary("");
		blankMaxSalary.setMinSalary("40000");
		blankMaxSalary.setLocation("Location 1");
		blankMaxSalary.setCompanyId(1L);

		alphaMaxSalary = new Job();
		alphaMaxSalary.setId(1L);
		alphaMaxSalary.setName("Job 1");
		alphaMaxSalary.setDescription("Description 1");
		alphaMaxSalary.setMaxSalary("abcde");
		alphaMaxSalary.setMinSalary("40000");
		alphaMaxSalary.setLocation("Location 1");
		alphaMaxSalary.setCompanyId(1L);

		alphanumMaxSalary = new Job();
		alphanumMaxSalary.setId(1L);
		alphanumMaxSalary.setName("Job 1");
		alphanumMaxSalary.setDescription("Description 1");
		alphanumMaxSalary.setMaxSalary("a3db2");
		alphanumMaxSalary.setMinSalary("40000");
		alphanumMaxSalary.setLocation("Location 1");
		alphanumMaxSalary.setCompanyId(1L);

		minMaxSalaryViolation = new Job();
		minMaxSalaryViolation.setId(1L);
		minMaxSalaryViolation.setName("Job 1");
		minMaxSalaryViolation.setDescription("Description 1");
		minMaxSalaryViolation.setMaxSalary("20000");
		minMaxSalaryViolation.setMinSalary("40000");
		minMaxSalaryViolation.setLocation("Location 1");
		minMaxSalaryViolation.setCompanyId(1L);

		nullJobLocation = new Job();
		nullJobLocation.setId(1L);
		nullJobLocation.setName("Job 1");
		nullJobLocation.setDescription("Description 1");
		nullJobLocation.setMinSalary("30000");
		nullJobLocation.setMaxSalary("40000");
		nullJobLocation.setLocation(null);
		nullJobLocation.setCompanyId(1L);

		blankJobLocation = new Job();
		blankJobLocation.setId(1L);
		blankJobLocation.setName("Job 1");
		blankJobLocation.setDescription("Description 1");
		blankJobLocation.setMinSalary("30000");
		blankJobLocation.setMaxSalary("40000");
		blankJobLocation.setLocation("");
		blankJobLocation.setCompanyId(1L);

		nullCompanyId = new Job();
		nullCompanyId.setId(1L);
		nullCompanyId.setName("Job 1");
		nullCompanyId.setDescription("Description 1");
		nullCompanyId.setMinSalary("30000");
		nullCompanyId.setMaxSalary("40000");
		nullCompanyId.setLocation("Location 1");
		nullCompanyId.setCompanyId(null);

		jobId = new JobId(1L);
		nullJobId = new JobId();
		negativeJobId = new JobId(-1L);
		outOfBoundsJobId = new JobId(Long.MAX_VALUE);

		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);

		company = Company.builder().id(1L).name("Company 1").description("Description 1").build();
		firstJobDTO = JobDTO.builder()
				.name(firstJob.getName())
				.id(firstJob.getId())
				.description(firstJob.getDescription())
				.minSalary(firstJob.getMinSalary())
				.maxSalary(firstJob.getMaxSalary())
				.location(firstJob.getLocation())
				.company(company)
				.reviews(reviewList)
				.build();
		secondJobDTO = JobDTO.builder()
				.name(secondJob.getName())
				.id(secondJob.getId())
				.description(secondJob.getDescription())
				.minSalary(secondJob.getMinSalary())
				.maxSalary(secondJob.getMaxSalary())
				.location(secondJob.getLocation())
				.company(company)
				.reviews(reviewList)
				.build();
	}

	@AfterEach
	public void deleteEntities() {
		CircuitBreaker.ofDefaults("companyBreaker").reset();
		CircuitBreaker.ofDefaults("reviewBreaker").reset();
		jobRepository.deleteAll();
	}

	@Test
	@DisplayName("It should create the job with completely filled attributes successfully!")
	void createJob() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		String response = REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		Assertions.assertAll("Successful Job Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The job is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the job with null job name!")
	void createNullJobName() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, nullJobName, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be null.")));
	}

	@Test
	@DisplayName("It should not create the job with blank job name!")
	void createBlankJobName() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, blankJobName, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be blank.")));
	}

	@Test
	@DisplayName("It should create the job with null job description successfully!")
	void createNullJobDescription() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		String response = REST_TEMPLATE.postForObject(base_url, nullJobDescription, String.class);
		Assertions.assertAll("Successful Job Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The job is created successfully!"));
	}

	@Test
	@DisplayName("It should create the job with blank job description successfully!")
	void createBlankJobDescription() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		String response = REST_TEMPLATE.postForObject(base_url, blankJobDescription, String.class);
		Assertions.assertAll("Successful Job Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The job is created successfully!"));
	}

	@Test
	@DisplayName("It should create the job with null job minimum salary successfully!")
	void createNullJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		String response = REST_TEMPLATE.postForObject(base_url, nullMinSalary, String.class);
		Assertions.assertAll("Successful Job Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The job is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the job with blank job minimum salary!")
	void createBlankJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, blankMinSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should not create the job with alphabetic job minimum salary!")
	void createAlphaJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, alphaMinSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should not create the job with alphanumeric job minimum salary!")
	void createAlphanumJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, alphanumMinSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should create the job with null job maximum salary successfully!")
	void createNullJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		String response = REST_TEMPLATE.postForObject(base_url, nullMaxSalary, String.class);
		Assertions.assertAll("Successful Job Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The job is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the job with blank job maximum salary!")
	void createBlankJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, blankMaxSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not create the job with alphabetic job maximum salary!")
	void createAlphaJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, alphaMaxSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not create the job with alphanumeric job maximum salary!")
	void createAlphanumJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, alphanumMaxSalary, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not create the job given the minimum and maximum salary because given minimum salary should not be greater than given maximum salary!")
	void createMinimumMaximumSalaryViolation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, minMaxSalaryViolation, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The minimum salary should not be greater than the maximum salary.")));
	}

	@Test
	@DisplayName("It should not create the job with null job location!")
	void createNullJobLocation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, nullJobLocation, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be null.")));
	}

	@Test
	@DisplayName("It should not create the job with blank job location!")
	void createBlankJobLocation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, blankJobLocation, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be blank.")));
	}

	@Test
	@DisplayName("It should not create the job with null job company id!")
	void createNullJobCompanyId() {
		Mockito.when(companyClient.getCompany(Mockito.any())).thenReturn(company);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, nullCompanyId, String.class));
		Assertions.assertAll("Failed Job Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid id, company id should not be null.")));
	}

	@Test
	@DisplayName("It should fetch all of the jobs in the database.")
	void getAllJobs() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		Assertions.assertAll("Successful Jobs Retrieval",
				() -> Assertions.assertNotNull(jobList),
				() -> Assertions.assertEquals(jobList.size() , 2),
				() -> Assertions.assertNotNull(jobList.get(0).getId()),
				() -> Assertions.assertNotNull(jobList.get(1).getId()),
				() -> Assertions.assertEquals(jobList.get(0), firstJobDTO),
				() -> Assertions.assertEquals(jobList.get(1), secondJobDTO));
	}

	@Test
	@DisplayName("It should fetch the specific job given its id in the database.")
	void getJobById() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
        assert jobList != null;
        jobId.setId(jobList.get(0).getId());
		JobDTO jobDTO = REST_TEMPLATE.getForObject(base_url + "/" + jobId.getId(), JobDTO.class);
		Assertions.assertAll("Successful Job Retrieval",
				() -> Assertions.assertNotNull(jobDTO),
				() -> Assertions.assertEquals(jobDTO, firstJobDTO));
	}

	@Test
	@DisplayName("It should not fetch the specific job given its id in the database because of the null id constraint violation.")
	void getJobByIdNullJobId() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + nullJobId.getId(), Job.class));
		Assertions.assertAll("Failed Job Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job id should not be null.")));
	}

	@Test
	@DisplayName("It should not fetch the specific job given its id in the database because of the negative id constraint violation.")
	void getJobByIdNegativeJobId() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + negativeJobId.getId(), Job.class));
		Assertions.assertAll("Failed Job Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job id should be positive.")));
	}

	@Test
	@DisplayName("It should not fetch the specific job given its id in the database because of the out of bounds id.")
	void getJobByIdOutOfBounds() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(0).getId() + jobList.size());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + jobId.getId(), JobDTO.class));
		Assertions.assertAll("Failed Job Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job is not found!")));
	}

	@Test
	@DisplayName("It should update the specific job with completely filled attributes successfully!")
	void updateJob() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", firstJob, jobId.getId());
		jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		Assertions.assertAll("Successful Job Update",
				() -> Assertions.assertNotNull(finalJobList),
				() -> Assertions.assertEquals(finalJobList.size() , 2),
				() -> Assertions.assertNotNull(finalJobList.get(0).getId()),
				() -> Assertions.assertNotNull(finalJobList.get(1).getId()));
	}

	@Test
	@DisplayName("It should not update the job with null job name!")
	void updateNullJobName() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobDTOList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobDTOList != null;
		jobId.setId(jobDTOList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", nullJobName, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be null.")));
	}

	@Test
	@DisplayName("It should not update the job with blank job name!")
	void updateBlankJobName() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobDTOList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobDTOList != null;
		jobId.setId(jobDTOList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", blankJobName, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, job name should not be blank.")));
	}

	@Test
	@DisplayName("It should update the job with null job description successfully!")
	void updateNullJobDescription() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullJobDescription, jobId.getId());
		jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		JobDTO nullJobDTO = JobDTO.createJobDTO(nullJobDescription, company, reviewList);
		Assertions.assertAll("Successful Job Update",
				() -> Assertions.assertNotNull(finalJobList),
				() -> Assertions.assertEquals(finalJobList.size() , 2),
				() -> Assertions.assertNotNull(finalJobList.get(0).getId()),
				() -> Assertions.assertNotNull(finalJobList.get(1).getId()),
				() -> Assertions.assertEquals(finalJobList.get(0), firstJobDTO),
				() -> Assertions.assertEquals(finalJobList.get(1), nullJobDTO));
	}

	@Test
	@DisplayName("It should update the job with blank job description successfully!")
	void updateBlankJobDescription() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", blankJobDescription, jobId.getId());
		jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		JobDTO blankJobDTO = JobDTO.createJobDTO(blankJobDescription, company, reviewList);
		Assertions.assertAll("Successful Job Update",
				() -> Assertions.assertNotNull(finalJobList),
				() -> Assertions.assertEquals(finalJobList.size() , 2),
				() -> Assertions.assertNotNull(finalJobList.get(0).getId()),
				() -> Assertions.assertNotNull(finalJobList.get(1).getId()),
				() -> Assertions.assertEquals(finalJobList.get(0), firstJobDTO),
				() -> Assertions.assertEquals(finalJobList.get(1), blankJobDTO));
	}

	@Test
	@DisplayName("It should update the job with null job minimum salary successfully!")
	void updateNullJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullMinSalary, jobId.getId());
		jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		JobDTO nullMinSalaryDTO = JobDTO.createJobDTO(nullMinSalary, company, reviewList);
		Assertions.assertAll("Successful Job Update",
				() -> Assertions.assertNotNull(finalJobList),
				() -> Assertions.assertEquals(finalJobList.size() , 2),
				() -> Assertions.assertNotNull(finalJobList.get(0).getId()),
				() -> Assertions.assertNotNull(finalJobList.get(1).getId()),
				() -> Assertions.assertEquals(finalJobList.get(0), firstJobDTO),
				() -> Assertions.assertEquals(finalJobList.get(1), nullMinSalaryDTO));
	}

	@Test
	@DisplayName("It should not update the job with blank job minimum salary!")
	void updateBlankJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", blankMinSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should not update the job with alphabetic job minimum salary!")
	void updateAlphaJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", alphaMinSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should not update the job with alphanumeric job minimum salary!")
	void updateAlphanumJobMinimumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", alphanumMinSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for minimum salary.")));
	}

	@Test
	@DisplayName("It should update the job with null job maximum salary successfully!")
	void updateNullJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullMaxSalary, jobId.getId());
		jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		JobDTO nullMaxSalaryDTO = JobDTO.createJobDTO(nullMaxSalary, company, reviewList);
		Assertions.assertAll("Successful Job Update",
				() -> Assertions.assertNotNull(finalJobList),
				() -> Assertions.assertEquals(finalJobList.size() , 2),
				() -> Assertions.assertNotNull(finalJobList.get(0).getId()),
				() -> Assertions.assertNotNull(finalJobList.get(1).getId()),
				() -> Assertions.assertEquals(finalJobList.get(0), firstJobDTO),
				() -> Assertions.assertEquals(finalJobList.get(1), nullMaxSalaryDTO));
	}

	@Test
	@DisplayName("It should not update the job with blank job maximum salary!")
	void updateBlankJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", blankMaxSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not update the job with alphabetic job maximum salary!")
	void updateAlphaJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", alphaMaxSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not update the job with alphanumeric job maximum salary!")
	void updateAlphanumJobMaximumSalary() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", alphanumMaxSalary, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a positive number for maximum salary.")));
	}

	@Test
	@DisplayName("It should not update the job given the minimum and maximum salary because given minimum salary should not be greater than given maximum salary!")
	void updateMinimumMaximumSalaryViolation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", minMaxSalaryViolation, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The minimum salary should not be greater than the maximum salary.")));
	}

	@Test
	@DisplayName("It should not update the job with null job location!")
	void updateNullJobLocation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", nullJobLocation, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be null.")));
	}

	@Test
	@DisplayName("It should not update the job with blank job location!")
	void updateBlankJobLocation() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", blankJobLocation, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid location, the location should not be blank.")));
	}

	@Test
	@DisplayName("It should not update the job with null job company id!")
	void updateNullJobCompanyId() {
		Mockito.when(companyClient.getCompany(Mockito.any())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", nullCompanyId, jobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid id, company id should not be null.")));
	}

	@Test
	@DisplayName("It should not update the specific job given its id in the database because of the negative id constraint violation.")
	void updateJobByIdNegativeJobId() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstJob, negativeJobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job id should be positive.")));
	}

	@Test
	@DisplayName("It should not update the specific job given its id in the database because of the out of bounds id.")
	void updateJobByIdOutOfBounds() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<Job>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Job> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstJob, outOfBoundsJobId.getId()));
		Assertions.assertAll("Failed Job Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job is not found!")));
	}

	@Test
	@DisplayName("It should not delete the specific job given its id in the database because of the negative id constraint violation.")
	void deleteJobByIdNegativeJobId() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", negativeJobId.getId()));
		Assertions.assertAll("Failed Job Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job id should be positive.")));
	}

	@Test
	@DisplayName("It should not delete the specific job given its id in the database because of the out of bounds id.")
	void deleteJobByIdOutOfBounds() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", outOfBoundsJobId.getId()));
		Assertions.assertAll("Failed Job Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The job is not found!")));
	}


	@Test
	@DisplayName("It should delete the specific job given its id in the database.")
	void deleteJobById() {
		Mockito.when(companyClient.getCompany(Mockito.anyLong())).thenReturn(company);
		List<Review> reviewList = new ArrayList<>();
		reviewList.add(firstReview);
		reviewList.add(secondReview);
		Mockito.when(reviewClient.getReviews(Mockito.anyLong())).thenReturn(reviewList);
		REST_TEMPLATE.postForObject(base_url, firstJob, String.class);
		REST_TEMPLATE.postForObject(base_url, secondJob, String.class);
		ParameterizedTypeReference<List<JobDTO>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<JobDTO> jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		jobId.setId(jobList.get(1).getId());
		REST_TEMPLATE.delete(base_url + "/{id}", jobId.getId());
	    jobList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert jobList != null;
		List<JobDTO> finalJobList = jobList;
		Assertions.assertAll("Successful Job Deletion",
				() -> Assertions.assertEquals(finalJobList.size(), 1),
				() -> Assertions.assertNotEquals(finalJobList.get(0), secondJobDTO));
	}

}
