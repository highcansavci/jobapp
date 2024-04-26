package com.savci.reviewms.review.repository;

import com.savci.reviewms.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCompanyId(Long companyId);
    @Modifying
    @Query("delete from Review r where r.companyId=:companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
}
