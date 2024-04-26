package com.savci.jobms.job.repository;

import com.savci.jobms.job.entity.Job;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@DisplayName("It should handle the repository operations successfully.")
public class JobRepositoryTest {
    @Autowired
    private JobRepository jobRepositoryTest;
    private Job firstJob;
    private Job secondJob;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
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
        secondJob.setMinSalary("40000");
        secondJob.setMaxSalary("50000");
        secondJob.setLocation("Location 2");
        secondJob.setCompanyId(2L);
    }

    @Test
    @DisplayName("It should save the job to the database.")
    void save() {
        Job savedJob = jobRepositoryTest.save(firstJob);
        Assertions.assertAll("Jobs Saved In Database",
                () -> Assertions.assertNotNull(savedJob),
                () -> Assertions.assertNotSame(firstJob.getId(), null));
    }

    @Test
    @DisplayName("It should return the two jobs stored in the database.")
    void getAllJobs() {
        jobRepositoryTest.save(firstJob);
        jobRepositoryTest.save(secondJob);

        List<Job> jobList = jobRepositoryTest.findAll();
        Assertions.assertAll("Jobs In Database",
                () -> Assertions.assertNotNull(jobList),
                () -> Assertions.assertEquals(jobList.size() , 2),
                () -> Assertions.assertNotNull(jobList.get(0).getId()),
                () -> Assertions.assertNotNull(jobList.get(1).getId()),
                () -> Assertions.assertEquals(jobList.get(0), firstJob),
                () -> Assertions.assertEquals(jobList.get(1), secondJob));
    }

    @Test
    @DisplayName("It should obtain the specific job via its id in the database.")
    void getJobById() {
        jobRepositoryTest.save(firstJob);
        jobRepositoryTest.save(secondJob);

        Job job = jobRepositoryTest.findById(firstJob.getId()).orElse(null);
        Assertions.assertAll("Specific Job in the Database",
                () -> Assertions.assertNotNull(job),
                () -> Assertions.assertSame(job, firstJob));
    }

    @Test
    @DisplayName("It should completely update the specific job in the database.")
    void fullyUpdateJobById() {
        jobRepositoryTest.save(firstJob);
        Job existingJob = jobRepositoryTest.findById(firstJob.getId()).orElse(null);
        Assertions.assertNotNull(existingJob);
        existingJob.copy(secondJob);
        Job updatedJob = jobRepositoryTest.save(existingJob);

        Assertions.assertAll("Fully Updated Job in the Database",
                () -> Assertions.assertNotNull(updatedJob),
                () -> Assertions.assertEquals(updatedJob.getName(), "Job 2"),
                () -> Assertions.assertEquals(updatedJob.getDescription(), "Description 2"),
                () -> Assertions.assertEquals(updatedJob.getMinSalary(), "40000"),
                () -> Assertions.assertEquals(updatedJob.getMaxSalary(), "50000"),
                () -> Assertions.assertEquals(updatedJob.getLocation(), "Location 2"),
                () -> Assertions.assertEquals(updatedJob.getCompanyId(), 2L));
    }

    @Test
    @DisplayName("It should partially update the specific job in the database.")
    void partiallyUpdateJobById() {
        jobRepositoryTest.save(firstJob);
        Job existingJob = jobRepositoryTest.findById(firstJob.getId()).orElse(null);
        Assertions.assertNotNull(existingJob);
        existingJob.setDescription("Description 2");
        existingJob.setMaxSalary("60000");
        existingJob.setLocation("Location 2");
        Job updatedJob = jobRepositoryTest.save(existingJob);

        Assertions.assertAll("Partially Updated Job in the Database",
                () -> Assertions.assertNotNull(updatedJob),
                () -> Assertions.assertEquals(updatedJob.getName(), "Job 1"),
                () -> Assertions.assertEquals(updatedJob.getDescription(), "Description 2"),
                () -> Assertions.assertEquals(updatedJob.getMinSalary(), "30000"),
                () -> Assertions.assertEquals(updatedJob.getMaxSalary(), "60000"),
                () -> Assertions.assertEquals(updatedJob.getLocation(), "Location 2"),
                () -> Assertions.assertEquals(updatedJob.getCompanyId(), 1L));
    }

    @Test
    @DisplayName("It should delete the existing object by id from the database")
    void deleteJobById() {
        jobRepositoryTest.save(firstJob);
        Long id = firstJob.getId();
        jobRepositoryTest.save(secondJob);

        jobRepositoryTest.deleteById(id);
        Job job = jobRepositoryTest.findById(id).orElse(null);
        List<Job> jobList = jobRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Job by Id in the Database",
                () -> Assertions.assertEquals(jobList.size(), 1),
                () -> Assertions.assertNull(job),
                () -> Assertions.assertNotSame(job, firstJob));
    }

    @Test
    @DisplayName("It should delete the existing job object from the database.")
    void deleteJob() {
        jobRepositoryTest.save(firstJob);
        Long id = firstJob.getId();
        jobRepositoryTest.save(secondJob);

        jobRepositoryTest.delete(firstJob);
        Job job = jobRepositoryTest.findById(id).orElse(null);
        List<Job> jobList = jobRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Job in the Database",
                () -> Assertions.assertEquals(jobList.size(), 1),
                () -> Assertions.assertNull(job),
                () -> Assertions.assertNotEquals(job, firstJob));
    }
}
