package com.savci.companyms.company.repository;

import com.savci.companyms.company.entity.Company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@DisplayName("It should handle the repository operations successfully.")
public class CompanyRepositoryTest {
    @Autowired
    private CompanyRepository companyRepositoryTest;
    private Company firstCompany;
    private Company secondCompany;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstCompany = new Company();
        firstCompany.setName("Company 1");
        firstCompany.setDescription("Description 1");

        secondCompany = new Company();
        secondCompany.setName("Company 2");
        secondCompany.setDescription("Description 2");
    }

    @Test
    @DisplayName("It should save the company to the database.")
    void save() {
        Company savedCompany = companyRepositoryTest.save(firstCompany);
        Assertions.assertAll("Companies Saved In Database",
                () -> Assertions.assertNotNull(savedCompany),
                () -> Assertions.assertNotSame(firstCompany.getId(), null));
    }

    @Test
    @DisplayName("It should return the two companies stored in the database.")
    void getAllJobs() {
        companyRepositoryTest.save(firstCompany);
        companyRepositoryTest.save(secondCompany);

        List<Company> companyList = companyRepositoryTest.findAll();
        Assertions.assertAll("Companies In Database",
                () -> Assertions.assertNotNull(companyList),
                () -> Assertions.assertEquals(companyList.size() , 2),
                () -> Assertions.assertNotNull(companyList.get(0).getId()),
                () -> Assertions.assertNotNull(companyList.get(1).getId()),
                () -> Assertions.assertEquals(companyList.get(0), firstCompany),
                () -> Assertions.assertEquals(companyList.get(1), secondCompany));
    }

    @Test
    @DisplayName("It should obtain the specific company via its id in the database.")
    void getJobById() {
        companyRepositoryTest.save(firstCompany);
        companyRepositoryTest.save(secondCompany);

        Company company = companyRepositoryTest.findById(firstCompany.getId()).orElse(null);
        Assertions.assertAll("Specific Company in the Database",
                () -> Assertions.assertNotNull(company),
                () -> Assertions.assertSame(company, firstCompany));
    }

    @Test
    @DisplayName("It should completely update the specific company in the database.")
    void fullyUpdateCompanyById() {
        companyRepositoryTest.save(firstCompany);
        Company existingCompany = companyRepositoryTest.findById(firstCompany.getId()).orElse(null);
        Assertions.assertNotNull(existingCompany);
        existingCompany.copy(secondCompany);
        Company updatedCompany = companyRepositoryTest.save(existingCompany);

        Assertions.assertAll("Fully Updated Company in the Database",
                () -> Assertions.assertNotNull(updatedCompany),
                () -> Assertions.assertEquals(updatedCompany.getName(), "Company 2"),
                () -> Assertions.assertEquals(updatedCompany.getDescription(), "Description 2"));
    }

    @Test
    @DisplayName("It should partially update the specific company in the database.")
    void partiallyUpdateCompanyById() {
        companyRepositoryTest.save(firstCompany);
        Company existingCompany = companyRepositoryTest.findById(firstCompany.getId()).orElse(null);
        Assertions.assertNotNull(existingCompany);
        existingCompany.setDescription("Description 2");
        Company updatedCompany = companyRepositoryTest.save(existingCompany);

        Assertions.assertAll("Partially Updated Company in the Database",
                () -> Assertions.assertNotNull(updatedCompany),
                () -> Assertions.assertEquals(updatedCompany.getName(), "Company 1"),
                () -> Assertions.assertEquals(updatedCompany.getDescription(), "Description 2"));
    }

    @Test
    @DisplayName("It should delete the existing object by id from the database")
    void deleteCompanyById() {
        companyRepositoryTest.save(firstCompany);
        Long id = firstCompany.getId();
        companyRepositoryTest.save(secondCompany);

        companyRepositoryTest.deleteById(id);
        Company company = companyRepositoryTest.findById(id).orElse(null);
        List<Company> companyList = companyRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Company by Id in the Database",
                () -> Assertions.assertEquals(companyList.size(), 1),
                () -> Assertions.assertNull(company),
                () -> Assertions.assertNotSame(company, firstCompany));
    }

    @Test
    @DisplayName("It should delete the existing company object from the database.")
    void deleteCompany() {
        companyRepositoryTest.save(firstCompany);
        Long id = firstCompany.getId();
        companyRepositoryTest.save(secondCompany);

        companyRepositoryTest.delete(firstCompany);
        Company company = companyRepositoryTest.findById(id).orElse(null);
        List<Company> companyList = companyRepositoryTest.findAll();
        Assertions.assertAll("Delete Specific Company in the Database",
                () -> Assertions.assertEquals(companyList.size(), 1),
                () -> Assertions.assertNull(company),
                () -> Assertions.assertNotEquals(company, firstCompany));
    }
}
