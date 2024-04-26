package com.savci.jobms.job.service;

import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public interface JobService {
    List<JobDTO> getAllJobs();
    Map<Object, HttpStatus> getJobById(JobId id);
    Map<String, HttpStatus> createJob(Job job);
    Map<Object, HttpStatus> updateJobById(Job job, JobId id);
    Map<String, HttpStatus> deleteJobById(JobId id);
    void deleteJobByCompanyId(Long companyId);
}
