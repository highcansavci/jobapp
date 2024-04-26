package com.savci.jobms.job.client;

import com.savci.jobms.job.external.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "REVIEWMS", url = "${reviewms.url}")
public interface ReviewClient {
    @GetMapping("/reviews?companyId={companyId}")
    List<Review> getReviews(@RequestParam("companyId") Long companyId);
}
