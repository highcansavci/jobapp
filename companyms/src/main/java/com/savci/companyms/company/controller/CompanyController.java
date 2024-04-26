package com.savci.companyms.company.controller;

import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companies")
@AllArgsConstructor
@Slf4j
public class CompanyController {
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        log.info("Fetch all companies operation is done in the controller layer.");
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable CompanyId id) {
        Map<?, HttpStatus> responseMap = companyService.getCompanyById(id);
        Map.Entry<?, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Fetch company with company id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @PostMapping
    public ResponseEntity<Object> createCompany(@RequestBody Company company) {
        Map<Object, HttpStatus> responseMap = companyService.createCompany(company);
        Map.Entry<Object, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info("Create company operation is done in the controller layer.");
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompanyById(@RequestBody Company company, @PathVariable CompanyId id) {
        Map<?, HttpStatus> responseMap = companyService.updateCompanyById(company, id);
        Map.Entry<?, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Update company with company id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCompanyById(@PathVariable CompanyId id) {
        Map<Object, HttpStatus> responseMap = companyService.deleteCompanyById(id);
        Map.Entry<Object, HttpStatus> responseEntry = responseMap.entrySet().iterator().next();
        log.info(String.format("Delete company with company id %d operation is done in the controller layer.", id.getId()));
        return new ResponseEntity<>(responseEntry.getKey(), responseEntry.getValue());
    }
}
