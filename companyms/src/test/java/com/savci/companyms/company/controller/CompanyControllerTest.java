package com.savci.companyms.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.service.CompanyService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@DisplayName("It should handle the controller layer operations successfully.")
public class CompanyControllerTest {
    @MockBean
    private CompanyService companyService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Company firstCompany;
    private Company secondCompany;

    private CompanyId companyId;
    private Set<ConstraintViolation<CompanyId>> companyIdConstraintViolations;

    @BeforeEach
    @DisplayName("Setting up the data for test cases.")
    void init() {
        firstCompany = new Company();
        firstCompany.setId(1L);
        firstCompany.setName("Company 1");
        firstCompany.setDescription("Description 1");

        secondCompany = new Company();
        secondCompany.setId(2L);
        secondCompany.setName("Company 2");
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
    @DisplayName("It should create a new company using the appropriate controller method.")
    void save() throws Exception {
        Mockito.when(companyService.createCompany(Mockito.any(Company.class))).thenReturn(Collections.singletonMap("The company is created successfully!", HttpStatus.CREATED));
        Assertions.assertEquals(mockMvc.perform(post("/companies").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is created successfully!");
    }

    @Test
    @DisplayName("It should not create a new company using the appropriate controller method. It should return 404 status code.")
    void saveFailedFirstCase() throws Exception {
        Mockito.when(companyService.createCompany(Mockito.any(Company.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(post("/companies").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fetch all the companies in the database.")
    void getAllCompanies() throws Exception {
        List<Company> companyList = new ArrayList<>();
        companyList.add(firstCompany);
        companyList.add(secondCompany);
        Mockito.when(companyService.getAllCompanies()).thenReturn(companyList);
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(companyList.size()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific company by id in the database and throw the ConstraintViolationException.")
    void getCompanyByIdConstraintViolationException() throws Exception {
        Mockito.when(companyService.getCompanyById(Mockito.any(CompanyId.class))).thenThrow(new ConstraintViolationException(companyIdConstraintViolations));
        mockMvc.perform(get("/companies/{id}", companyId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific company by id in the database because of the invalid id.")
    void getCompanyByIdInvalidId() throws Exception {
        Mockito.when(companyService.getCompanyById(Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(get("/companies/{id}", companyId))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to fetch the specific company by id in the database because of the invalid job.")
    void getCompanyByIdInvalidCompany() throws Exception {
        Mockito.when(companyService.getCompanyById(Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(get("/companies/{id}", companyId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to fetch the specific company by id in the database.")
    void getCompanyById() throws Exception {
        Mockito.when(companyService.getCompanyById(Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap(firstCompany, HttpStatus.OK));
        mockMvc.perform(get("/companies/{id}", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value( "Company 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific company by id in the database and throw the ConstraintViolationException.")
    void deleteCompanyByIdConstraintViolationException() throws Exception {
        Mockito.when(companyService.deleteCompanyById(Mockito.any(CompanyId.class))).thenThrow(new ConstraintViolationException(companyIdConstraintViolations));
        mockMvc.perform(delete("/companies/{id}", companyId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to delete the specific company by id in the database because of the invalid id.")
    void deleteCompanyByIdInvalidId() throws Exception {
        Mockito.when(companyService.deleteCompanyById(Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(delete("/companies/{id}", companyId.getId()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to delete the specific company by id in the database.")
    void deleteJobById() throws Exception {
        Mockito.when(companyService.deleteCompanyById(Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is deleted successfully!", HttpStatus.OK));
        Assertions.assertEquals(mockMvc.perform(delete("/companies/{id}", companyId.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is deleted successfully!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific company by id in the database and throw the ConstraintViolationException.")
    void updateCompanyByIdConstraintViolationException() throws Exception {
        Mockito.when(companyService.updateCompanyById(Mockito.any(Company.class), Mockito.any(CompanyId.class))).thenThrow(new ConstraintViolationException(companyIdConstraintViolations));
        mockMvc.perform(put("/companies/{id}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific company by id in the database because of the invalid id.")
    void updateCompanyByIdInvalidId() throws Exception {
        Mockito.when(companyService.updateCompanyById(Mockito.any(Company.class), Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.BAD_REQUEST));
        Assertions.assertEquals(mockMvc.perform(put("/companies/{id}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific company by id in the database because of the invalid job.")
    void updatedCompanyByIdInvalidCompany() throws Exception {
        Mockito.when(companyService.updateCompanyById(Mockito.any(Company.class), Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The company is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/companies/{id}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to fail to update the specific company by id in the database because of the invalid job.")
    void updatedCompanyByIdInvalidUpdatedCompany() throws Exception {
        Mockito.when(companyService.updateCompanyById(Mockito.any(Company.class), Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap("The updated company is not found!", HttpStatus.NOT_FOUND));
        Assertions.assertEquals(mockMvc.perform(put("/companies/{id}", companyId.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(), "The updated company is not found!");
    }

    @Test
    @DisplayName("It should use the controller to succeed to update the specific company by id in the database.")
    void updateJobById() throws Exception {
        Mockito.when(companyService.updateCompanyById(Mockito.any(Company.class), Mockito.any(CompanyId.class))).thenReturn(Collections.singletonMap(firstCompany, HttpStatus.OK));
        mockMvc.perform(put("/companies/{id}", companyId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(firstCompany)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value( "Company 1"))
                .andExpect(jsonPath("$.description").value( "Description 1"));
    }
}

