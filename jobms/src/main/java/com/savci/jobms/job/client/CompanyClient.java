package com.savci.jobms.job.client;

import com.savci.jobms.job.external.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "COMPANYMS", dismiss404 = true, url = "${companyms.url}")
public interface CompanyClient {
    @GetMapping("/companies/{id}")
    Company getCompany(@PathVariable("id") Long id);
}
