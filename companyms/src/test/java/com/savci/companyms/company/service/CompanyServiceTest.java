package com.savci.companyms.company.service;

import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.messaging.CompanyJobDeleteProducer;
import com.savci.companyms.company.messaging.CompanyReviewDeleteProducer;
import com.savci.companyms.company.repository.CompanyRepository;
import com.savci.companyms.company.service.impl.CompanyServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("It should handle the service layer operations successfully.")
public class CompanyServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private Validator validator;
    @Mock
    private CompanyReviewDeleteProducer companyReviewDeleteProducer;
    @Mock
    private CompanyJobDeleteProducer companyJobDeleteProducer;

    private Company firstCompany;
    private Company secondCompany;
    private CompanyId companyId;
    private Set<ConstraintViolation<CompanyId>> companyIdConstraintViolations;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstCompany = new Company();
        firstCompany.setId(1L);
        firstCompany.setName("Job 1");
        firstCompany.setDescription("Description 1");

        secondCompany = new Company();
        secondCompany.setId(2L);
        secondCompany.setName("Job 2");
        secondCompany.setDescription("Description 2");

        companyId = new CompanyId();
        companyId.setId(1L);

        companyIdConstraintViolations = new HashSet<>();
        companyIdConstraintViolations.add(new ConstraintViolation<CompanyId>() {
            @Override
            public String getMessage() {
                return "Dummy message";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public CompanyId getRootBean() {
                return null;
            }

            @Override
            public Class<CompanyId> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        });
    }

    @Test
    @DisplayName("The company service should save the company to the database and return success message with 201 status code.")
    void save() {
        Map<Object, HttpStatus> statusMap = companyService.createCompany(firstCompany);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is created successfully!", HttpStatus.CREATED));
    }

    @Test
    @DisplayName("The company service should not save the company to the database and return company is not found message with 404 status code.")
    void tryToSaveNullJob() {
        Map<Object, HttpStatus> statusMap = companyService.createCompany(null);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is not valid!", HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("The company service should get all the companies in the database.")
    void getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        companies.add(firstCompany);
        companies.add(secondCompany);
        Mockito.when(companyRepository.findAll()).thenReturn(companies);
        List<Company> companyList = companyService.getAllCompanies();
        Assertions.assertAll("Companies In Database",
                () -> Assertions.assertNotNull(companyList),
                () -> Assertions.assertEquals(companyList, companies),
                () -> Assertions.assertEquals(companyList.size() , 2),
                () -> Assertions.assertNotNull(companyList.get(0).getId()),
                () -> Assertions.assertNotNull(companyList.get(1).getId()),
                () -> Assertions.assertEquals(companyList.get(0), firstCompany),
                () -> Assertions.assertEquals(companyList.get(1), secondCompany));
    }

    @Test
    @DisplayName("The company service should not get the specific company by id in the database and it should throw ConstraintViolationException.")
    void getCompanyByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> companyService.getCompanyById(companyId));
    }

    @Test
    @DisplayName("The company service should not get the specific company by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void getCompanyByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        companyId.setId(3L);
        Map<?, HttpStatus> statusMap = companyService.getCompanyById(companyId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The company service should get the specific company by id in the database and it should return success message with 200 status code.")
    void getCompanyById() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(companyRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> (Long) invocationOnMock.getArgument(0) == 1L ? Optional.of(firstCompany) : Optional.empty());
        Map<?, HttpStatus> statusMap = companyService.getCompanyById(companyId);
        Assertions.assertEquals(statusMap, Collections.singletonMap(firstCompany, HttpStatus.OK));
    }

    @Test
    @DisplayName("The company service should not update the specific company by id in the database and it should throw ConstraintViolationException.")
    void updateCompanyByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> companyService.updateCompanyById(firstCompany, companyId));
    }

    @Test
    @DisplayName("The company service should not update the specific company by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void updateCompanyByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        companyId.setId(3L);
        Map<?, HttpStatus> statusMap = companyService.updateCompanyById(firstCompany, companyId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The company service should update the specific company by id in the database and it should return success message with 200 status code.")
    void updateCompanyById() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(companyRepository.findById(Mockito.anyLong())).thenAnswer(invocationOnMock -> (Long) invocationOnMock.getArgument(0) == 1L ? Optional.of(firstCompany) : Optional.empty());
        Map<?, HttpStatus> statusMap = companyService.updateCompanyById(secondCompany, companyId);
        secondCompany.setId(1L);
        Assertions.assertEquals(statusMap, Collections.singletonMap(secondCompany, HttpStatus.OK));
    }

    @Test
    @DisplayName("The company service should not delete the specific company by id in the database and it should throw ConstraintViolationException.")
    void deleteCompanyByIdConstraintViolation() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(companyIdConstraintViolations);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> companyService.deleteCompanyById(companyId));
    }

    @Test
    @DisplayName("The company service should not delete the specific company by id in the database because of the invalid id and it should return failed message with 400 status code.")
    void deleteCompanyByIdIdDoesntExist() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);
        companyId.setId(3L);
        Map<?, HttpStatus> statusMap = companyService.deleteCompanyById(companyId);
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("The company service should delete the specific company by id in the database and it should return success message with 200 status code.")
    void deleteJobById() {
        Mockito.when(validator.validate(Mockito.any(CompanyId.class))).thenReturn(Collections.emptySet());
        Mockito.when(companyRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(companyRepository).deleteById(companyId.getId());
        Mockito.doNothing().when(companyReviewDeleteProducer).sendMessage(companyId.getId());
        Mockito.doNothing().when(companyJobDeleteProducer).sendMessage(companyId.getId());
        Map<?, HttpStatus> statusMap = companyService.deleteCompanyById(companyId);
        Mockito.verify(companyRepository, Mockito.times(1)).deleteById(companyId.getId());
        Assertions.assertEquals(statusMap, Collections.singletonMap("The company is deleted successfully!", HttpStatus.OK));
    }
}
