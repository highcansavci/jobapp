package com.savci.jobms.job.controller;

import com.savci.jobms.job.dto.JobDTO;
import com.savci.jobms.job.service.JobService;
import com.savci.jobms.job.entity.Job;
import com.savci.jobms.job.entity.JobId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
@AllArgsConstructor
@Slf4j
public class JobController {
    private JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        log.info("Fetch all jobs operation is done in the controller layer.");
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable JobId id) {
        Map<?, HttpStatus> responseMap = jobService.getJobById(id);
        Map.Entry<?, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Fetch job with job id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job) {
        Map<String, HttpStatus> responseMap = jobService.createJob(job);
        Map.Entry<String, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info("Create job operation is done in the controller layer.");
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobById(@RequestBody Job job, @PathVariable JobId id) {
        Map<?, HttpStatus> responseMap = jobService.updateJobById(job, id);
        Map.Entry<?, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Update job with job id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable JobId id) {
        Map<String, HttpStatus> responseMap = jobService.deleteJobById(id);
        Map.Entry<String, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Delete job with job id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }
}
