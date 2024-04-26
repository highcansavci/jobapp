package com.savci.companyms.company.service.impl;

import com.savci.companyms.company.client.ReviewClient;
import com.savci.companyms.company.dto.ReviewMessage;
import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.messaging.CompanyJobDeleteProducer;
import com.savci.companyms.company.messaging.CompanyReviewDeleteProducer;
import com.savci.companyms.company.repository.CompanyRepository;
import com.savci.companyms.company.service.CompanyService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private Validator validator;
    private CompanyRepository companyRepository;
    private ReviewClient reviewClient;
    private CompanyReviewDeleteProducer companyReviewDeleteProducer;
    private CompanyJobDeleteProducer companyJobDeleteProducer;

    @Override
    public List<Company> getAllCompanies() {
        log.info("Fetch all the companies operation is successfully done in the service layer!");
        return companyRepository.findAll();
    }

    @Override
    public Map<Object, HttpStatus> getCompanyById(CompanyId id) {
        Set<ConstraintViolation<CompanyId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()){
            log.error("Constraints of the company id is not met when fetching in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(!companyRepository.existsById(id.getId())) {
            log.error("The company is not found in the company repository when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        Company company = companyRepository.findById(id.getId()).orElse(null);
        log.info(String.format("The company with the id %d is created successfully in the service layer!", id.getId()));
        return Collections.singletonMap(company, HttpStatus.OK);
    }

    @Override
    public Map<Object, HttpStatus> createCompany(@Valid Company company) {
        if(company == null){
            log.error("The company is not valid upon the creation in the service layer!");
            return Collections.singletonMap("The company is not valid!", HttpStatus.BAD_REQUEST);
        }
        companyRepository.save(company);
        log.info("The company is created successfully in the service layer!");
        return Collections.singletonMap("The company is created successfully!", HttpStatus.CREATED);
    }

    @Override
    public Map<Object, HttpStatus> updateCompanyById(@Valid Company company, CompanyId id) {
        Set<ConstraintViolation<CompanyId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()){
            log.error("Constraints of the company id is not met when updating in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(company == null){
            log.error("The company is not valid upon the creation in the service layer!");
            return Collections.singletonMap("The company is not valid!", HttpStatus.BAD_REQUEST);
        }
        if(!companyRepository.existsById(id.getId())) {
            log.error("The company is not found in the company repository when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        Company updatedCompany = companyRepository.findById(id.getId()).orElse(null);
        assert updatedCompany != null;
        updatedCompany.copy(company);
        companyRepository.save(updatedCompany);
        log.info("The company is updated successfully in the service layer!");
        return Collections.singletonMap(updatedCompany, HttpStatus.OK);
    }

    @Override
    public Map<Object, HttpStatus> deleteCompanyById(CompanyId id) {
        Set<ConstraintViolation<CompanyId>> idConstraintViolations = validator.validate(id);
        if(!idConstraintViolations.isEmpty()){
            log.error("Constraints of the company id is not met when deleting in the service layer!");
            throw new ConstraintViolationException(idConstraintViolations);
        }
        if(!companyRepository.existsById(id.getId())) {
            log.error("The company is not found in the company repository when querying in the service layer!");
            return Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND);
        }
        companyRepository.deleteById(id.getId());
        companyReviewDeleteProducer.sendMessage(id.getId());
        companyJobDeleteProducer.sendMessage(id.getId());
        log.info("The company is deleted successfully in the service layer!");
        return Collections.singletonMap("The company is deleted successfully!", HttpStatus.OK);
    }

    @Override
    public void updateCompanyRating(ReviewMessage reviewMessage) {
        Company company = companyRepository.findById(reviewMessage.getCompanyId()).orElse(null);
        if(company == null)
            return;
        company.setAverageRating(reviewClient.getAverageRatingForCompany(reviewMessage.getCompanyId()));
        companyRepository.save(company);
    }

}
