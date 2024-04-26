package com.savci.jobms.job.service.impl;

import com.savci.jobms.job.client.CompanyClient;
import com.savci.jobms.job.client.ReviewClient;
import com.savci.jobms.job.entity.CompanyId;
import com.savci.jobms.job.external.Company;
import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import com.savci.jobms.job.repository.JobRepository;
import com.savci.jobms.job.service.JobService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
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
public class JobServiceImpl implements JobService {
    private Validator validator;
    private JobRepository jobRepository;
    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    @Override
    @RateLimiter(name = "companyBreaker")
    @CircuitBreaker(name = "reviewBreaker")
    public List<JobDTO> getAllJobs() {
        log.info("Fetch all the job-company data transfer objects operation is successfully done in the service layer!");
        return jobRepository.findAll().stream().map(job -> JobDTO.createJobDTO(job, companyClient.getCompany(job.getCompanyId()), reviewClient.getReviews(job.getCompanyId()))).toList();
    }

    @Override
    @RateLimiter(name = "companyBreaker")
    @CircuitBreaker(name = "reviewBreaker")
    public Map<Object, HttpStatus> getJobById(JobId id) {
        Set<ConstraintViolation<JobId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()){
            log.error("Constraints of the job id is not met when fetching in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(!jobRepository.existsById(id.getId())) {
            log.error("The job is not found in the job repository when querying in the service layer!");
            return Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND);
        }
        Job job = jobRepository.findById(id.getId()).orElse(null);
        assert job != null;
        Company company = companyClient.getCompany(job.getCompanyId());
        if(company == null) {
            log.error("The company which belongs to the job is not found when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        log.info(String.format("The job-company data transfer object with the id %d is created successfully in the service layer!", id.getId()));
        return Collections.singletonMap(JobDTO.createJobDTO(job, company, reviewClient.getReviews(job.getCompanyId())), HttpStatus.OK);
    }

    @Override
    @RateLimiter(name = "companyBreaker")
    public Map<String, HttpStatus> createJob(@Valid Job job) {
        if(job == null) {
            log.error("The job is not valid upon the creation in the service layer!");
            return Collections.singletonMap("The job is not valid!", HttpStatus.BAD_REQUEST);
        }
        Company company = companyClient.getCompany(job.getCompanyId());
        if(company == null) {
            log.error("The company which belongs to the job is not found when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        jobRepository.save(job);
        log.info("The job is created successfully in the service layer!");
        return Collections.singletonMap("The job is created successfully!", HttpStatus.CREATED);
    }

    @Override
    @RateLimiter(name = "companyBreaker")
    @CircuitBreaker(name = "reviewBreaker")
    public Map<Object, HttpStatus> updateJobById(@Valid Job job, JobId id) {
        Set<ConstraintViolation<JobId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()) {
            log.error("Constraints of the job id is not met when updating in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(job == null) {
            log.error("The job is not valid upon the update in the service layer!");
            return Collections.singletonMap("The job is not valid!", HttpStatus.BAD_REQUEST);
        }
        Company company = companyClient.getCompany(job.getCompanyId());
        if(company == null) {
            log.error("The company which belongs to the job is not found when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        if(!jobRepository.existsById(id.getId())) {
            log.error("The job is not found in the job repository when querying in the service layer!");
            return Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND);
        }
        Job updatedJob = jobRepository.findById(id.getId()).orElse(null);
        assert updatedJob != null;
        Company updatedCompany = companyClient.getCompany(updatedJob.getCompanyId());
        if(updatedCompany == null) {
            log.error("The updated company which belongs to the job is not found when querying in the service layer!");
            return Collections.singletonMap("The updated company is not found!", HttpStatus.NOT_FOUND);
        }
        updatedJob.copy(job);
        jobRepository.save(updatedJob);
        log.info("The job is updated successfully in the service layer!");
        return Collections.singletonMap(JobDTO.createJobDTO(updatedJob, company, reviewClient.getReviews(job.getCompanyId())), HttpStatus.OK);
    }

    @Override
    public Map<String, HttpStatus> deleteJobById(JobId id) {
        Set<ConstraintViolation<JobId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()) {
            log.error("Constraints of the job id is not met when deleting in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(!jobRepository.existsById(id.getId())) {
            log.error("The job is not found in the job repository when querying in the service layer!");
            return Collections.singletonMap("The job is not found!", HttpStatus.NOT_FOUND);
        }
        jobRepository.deleteById(id.getId());
        log.info("The job is deleted successfully in the service layer!");
        return Collections.singletonMap("The job is deleted successfully!", HttpStatus.OK);
    }

    @Override
    @Transactional
    public void deleteJobByCompanyId(Long companyId) {
        CompanyId id = new CompanyId(companyId);
        Set<ConstraintViolation<CompanyId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()) {
            log.error("Constraints of the job id is not met when deleting in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        jobRepository.deleteByCompanyId(companyId);
    }
}
