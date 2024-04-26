package com.savci.jobms.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savci.jobms.job.external.Company;
import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import com.savci.jobms.job.external.Review;
import com.savci.jobms.job.service.JobService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@DisplayName("It should handle the controller layer operations successfully.")
public class JobControllerTest {
    @MockBean
    private JobService jobService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Job firstJob;
    private Job secondJob;

    private Company company;

    private Review firstReview;
    private Review secondReview;

    private JobDTO firstJobDTO;
    private JobDTO secondJobDTO;

    private JobId jobId;
    private Set<ConstraintViolation<JobId>> jobIdConstraintViolations;

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

        List<Review> reviewList = new ArrayList<>();
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
    @DisplayName("It should create a new job using the appropriate controller method.")
    void save() throws Exception {
        Mockito.when(jobService.createJob(Mockito.any(Job.class))).thenReturn(Collections.singletonMap("The job is created successfully!", HttpStatus.CREATED));
        Assertions.assertEquals(mockMvc.perform(post("/jobs").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is created successfully!");
    }

    @Test
    @DisplayName("It should not create a new job using the appropriate controller method. It should return 404 status code.")
    void saveFailedFirstCase() throws Exception {
        Mockito.when(jobService.createJob(Mockito.any(Job.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(post("/jobs").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fetch all the jobs in the database.")
    void getAllJobs() throws Exception {
        List<JobDTO> jobDTOList = new ArrayList<>();
        jobDTOList.add(firstJobDTO);
        jobDTOList.add(secondJobDTO);
        Mockito.when(jobService.getAllJobs()).thenReturn(jobDTOList);
        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(jobDTOList.size()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific job by id in the database and throw the ConstraintViolationException.")
    void getJobByIdConstraintViolationException() throws Exception {
        Mockito.when(jobService.getJobById(Mockito.any(JobId.class))).thenThrow(new ConstraintViolationException(jobIdConstraintViolations));
        mockMvc.perform(get("/jobs/{id}", jobId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific job by id in the database because of the invalid id.")
    void getJobByIdInvalidId() throws Exception {
        Mockito.when(jobService.getJobById(Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(get("/jobs/{id}", jobId))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific job by id in the database because of the invalid job.")
    void getJobByIdInvalidJob() throws Exception {
        Mockito.when(jobService.getJobById(Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(get("/jobs/{id}", jobId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to fetch the specific job by id in the database.")
    void getJobById() throws Exception {
        Mockito.when(jobService.getJobById(Mockito.any(JobId.class))).thenReturn(Collections.singletonMap(firstJob, HttpStatus.OK));
        mockMvc.perform(get("/jobs/{id}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value( "Job 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"))
                .andExpect(jsonPath("$.minSalary").value("30000"))
                .andExpect(jsonPath("$.maxSalary").value("40000"))
                .andExpect(jsonPath("$.location").value("Location 1"))
                .andExpect(jsonPath("$.companyId").value(1L));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific job by id in the database and throw the ConstraintViolationException.")
    void deleteJobByIdConstraintViolationException() throws Exception {
        Mockito.when(jobService.deleteJobById(Mockito.any(JobId.class))).thenThrow(new ConstraintViolationException(jobIdConstraintViolations));
        mockMvc.perform(delete("/jobs/{id}", jobId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific job by id in the database because of the invalid id.")
    void deleteJobByIdInvalidId() throws Exception {
        Mockito.when(jobService.deleteJobById(Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(delete("/jobs/{id}", jobId.getId()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to delete the specific job by id in the database.")
    void deleteJobById() throws Exception {
        Mockito.when(jobService.deleteJobById(Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is deleted successfully!", HttpStatus.OK));
        Assertions.assertEquals(mockMvc.perform(delete("/jobs/{id}", jobId.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is deleted successfully!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific job by id in the database and throw the ConstraintViolationException.")
    void updateJobByIdConstraintViolationException() throws Exception {
        Mockito.when(jobService.updateJobById(Mockito.any(Job.class), Mockito.any(JobId.class))).thenThrow(new ConstraintViolationException(jobIdConstraintViolations));
        mockMvc.perform(put("/jobs/{id}", jobId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific job by id in the database because of the invalid id.")
    void updateJobByIdInvalidId() throws Exception {
        Mockito.when(jobService.updateJobById(Mockito.any(Job.class), Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(put("/jobs/{id}", jobId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific job by id in the database because of the invalid job.")
    void updatedJobByIdInvalidJob() throws Exception {
        Mockito.when(jobService.updateJobById(Mockito.any(Job.class), Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/jobs/{id}", jobId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific job by id in the database because of the invalid job.")
    void updatedJobByIdInvalidUpdatedJob() throws Exception {
        Mockito.when(jobService.updateJobById(Mockito.any(Job.class), Mockito.any(JobId.class))).thenReturn(Collections.singletonMap("The updated job is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/jobs/{id}", jobId.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The updated job is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to update the specific job by id in the database.")
    void updateJobById() throws Exception {
        Mockito.when(jobService.updateJobById(Mockito.any(Job.class), Mockito.any(JobId.class))).thenReturn(Collections.singletonMap(firstJob, HttpStatus.OK));
        mockMvc.perform(put("/jobs/{id}", jobId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstJob)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value( "Job 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"))
                .andExpect(jsonPath("$.minSalary").value("30000"))
                .andExpect(jsonPath("$.maxSalary").value("40000"))
                .andExpect(jsonPath("$.location").value("Location 1"))
                .andExpect(jsonPath("$.companyId").value(1L));
    }
}
