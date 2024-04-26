package com.savci.companyms.company.service;

import com.savci.companyms.company.dto.ReviewMessage;
import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    List<Company> getAllCompanies();
    Map<Object, HttpStatus> getCompanyById(CompanyId id);
    Map<Object, HttpStatus> createCompany(Company company);
    Map<Object, HttpStatus> updateCompanyById(Company company, CompanyId id);
    Map<Object, HttpStatus> deleteCompanyById(CompanyId id);
    void updateCompanyRating(ReviewMessage reviewMessage);
}
