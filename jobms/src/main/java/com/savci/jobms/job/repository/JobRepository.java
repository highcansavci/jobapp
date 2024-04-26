package com.savci.jobms.job.repository;

import com.savci.jobms.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Modifying
    @Query("delete from Job j where j.companyId=:companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
}
