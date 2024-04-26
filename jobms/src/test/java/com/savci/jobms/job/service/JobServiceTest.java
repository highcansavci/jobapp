package com.savci.jobms.job.service;

import com.savci.jobms.job.client.CompanyClient;
import com.savci.jobms.job.client.ReviewClient;
import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import com.savci.jobms.job.external.Company;
import com.savci.jobms.job.external.Review;
import com.savci.jobms.job.repository.JobRepository;
import com.savci.jobms.job.service.impl.JobServiceImpl;
import jakarta.validation.*;
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
public class JobServiceTest {

    @InjectMocks
    private JobServiceImpl jobService;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private Validator validator;
    @Mock
    private CompanyClient companyClient;
    @Mock
    private ReviewClient reviewClient;


    private Job firstJob;
    private Job secondJob;
    private Company company;
    private Review firstReview;
    private Review secondReview;
    private JobDTO firstJobDTO;
    private JobDTO secondJobDTO;
    private JobId jobId;
    private Set<ConstraintViolation<JobId>> jobIdConstraintViolations;
    private List<Review> reviewList;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstJob = new Job();
        firstJob.setId(1L);
        firstJob.setName("Job 1");
        firstJob.setDescription("Description 1");
        firstJob.setMinSalary("30000");
        firstJob.setMaxSalary("40000");
        firstJob.setLocation("Location 1");
        firstJob.setCompanyId(1L);

        secondJob = new Job();
        secondJob.setId(2L);
        secondJob.setName("Job 2");
        secondJob.setDescription("Description 2");
        secondJob.setMinSalary("40000");
        secondJob.setMaxSalary("50000");
        secondJob.setLocation("Location 2");
        secondJob.setCompanyId(2L);

        firstReview = Review.builder().id(1L).title("Title 1").description("Description 1").rating(5D).companyId(1L).build();
        secondReview = Review.builder().id(1L).title("Title 2").description("Description 2").rating(3.5D).companyId(1L).build();

        reviewList = new ArrayList<>();
        reviewList.add(firstReview);
        reviewList.add(secondReview);

        jobId = new JobId();
        jobId.setId(1L);

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

        jobIdConstraintViolations = new HashSet<>();
        jobIdConstraintViolations.add(new ConstraintViolation<JobId>() {
            @Override
            public String getMessage() {
                return "Dummy message";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public JobId getRootBean() {
                return null;
            }

            @Override
            public Class<JobId> getRootBeanClass() {
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
    @DisplayName("The job service should save the job to the database and return success message with 201 status code.")
    void save() {
        Mockito.when(companyClient.getCompany(firstJob.getCompanyId())).thenReturn(company);
        Map<String, HttpStatus> statusMap = jobService.createJob(firstJob);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is created successfully!", HttpStatus.CREATED));
    }

    @Test
    @DisplayName("The job service should not save the job to the database and return job is not found message with 404 status code.")
    void tryToSaveNullJob() {
        Map<String, HttpStatus> statusMap = jobService.createJob(null);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is not valid!", HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("The job service should get all the jobs in the database.")
    void getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(firstJob);
        jobs.add(secondJob);
        Mockito.when(jobRepository.findAll()).thenReturn(jobs);
        List<JobDTO> jobList = jobService.getAllJobs();
        Assertions.assertAll("Jobs In Database",
                () -> Assertions.assertNotNull(jobList),
                () -> Assertions.assertEquals(jobList.size() , 2),
                () -> Assertions.assertNotNull(jobList.get(0).getId()),
                () -> Assertions.assertNotNull(jobList.get(1).getId()));
    }

    @Test
    @DisplayName("The job service should not get the specific job by id in the database and it should throw ConstraintViolationException.")
    void getJobByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(jobIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> jobService.getJobById(jobId));
    }

    @Test
    @DisplayName("The job service should not get the specific job by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void getJobByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(Collections.emptySet());
        Mockito.when(jobRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        jobId.setId(3L);
        Map<?, HttpStatus> statusMap = jobService.getJobById(jobId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The job service should get the specific job by id in the database and it should return success message with 200 status code.")
    void getJobById() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(Collections.emptySet());
        Mockito.when(jobRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(jobRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> (Long) invocationOnMock.getArgument(0) == 1L ? Optional.of(firstJob) : Optional.empty());
        Mockito.when(companyClient.getCompany(firstJob.getCompanyId())).thenReturn(company);
        Mockito.when(reviewClient.getReviews(firstJob.getCompanyId())).thenReturn(reviewList);
        Map<?, HttpStatus> statusMap = jobService.getJobById(jobId);
        Assertions.assertEquals(statusMap, Collections.singletonMap(firstJobDTO, HttpStatus.OK));
    }

    @Test
    @DisplayName("The job service should not update the specific job by id in the database and it should throw ConstraintViolationException.")
    void updateJobByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(jobIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> jobService.updateJobById(firstJob, jobId));
    }

    @Test
    @DisplayName("The job service should not update the specific job by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void updateJobByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(Collections.emptySet());
        Mockito.when(jobRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        Mockito.when(companyClient.getCompany(firstJob.getCompanyId())).thenReturn(company);
        jobId.setId(3L);
        Map<?, HttpStatus> statusMap = jobService.updateJobById(firstJob, jobId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
    }


    @Test
    @DisplayName("The job service should not delete the specific job by id in the database and it should throw ConstraintViolationException.")
    void deleteJobByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(jobIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> jobService.deleteJobById(jobId));
    }

    @Test
    @DisplayName("The job service should not delete the specific job by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void deleteJobByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(Collections.emptySet());
        Mockito.when(jobRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        jobId.setId(3L);
        Map<?, HttpStatus> statusMap = jobService.deleteJobById(jobId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The job service should delete the specific job by id in the database and it should return success message with 200 status code.")
    void deleteJobById() {
        Mockito.when(validator.validate(Mockito.any(JobId.class))).thenReturn(Collections.emptySet());
        Mockito.when(jobRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(jobRepository).deleteById(jobId.getId());
        Map<?, HttpStatus> statusMap = jobService.deleteJobById(jobId);
        Mockito.verify(jobRepository, Mockito.times(1)).deleteById(jobId.getId());
        Assertions.assertEquals(statusMap, Collections.singletonMap("The job is deleted successfully!", HttpStatus.OK));
    }
}
