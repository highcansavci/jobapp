package com.savci.companyms;

import com.savci.companyms.company.entity.Company;
import com.savci.companyms.company.entity.CompanyId;
import com.savci.companyms.company.repository.CompanyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("It should handle the integration tests successfully.")
class CompanymsApplicationTests {

	@LocalServerPort
	private int port;

	private String base_url;

	private static final RestTemplate REST_TEMPLATE = new RestTemplate();

	@Autowired
	private CompanyRepository companyRepository;

	private Company firstCompany;
	private Company secondCompany;
	private Company nullCompanyName;
	private Company blankCompanyName;
	private Company nullCompanyDescription;
	private Company blankCompanyDescription;
	private CompanyId nullCompanyId;
	private CompanyId negativeCompanyId;
	private CompanyId companyId;
	private CompanyId outOfBoundsCompanyId;


	@BeforeEach
	@DisplayName("It sets up the data for integration tests.")
	public void setupData() {
		base_url = "http://localhost:" + port + "/companies";

		firstCompany = new Company();
		firstCompany.setName("Company 1");
		firstCompany.setDescription("Description 1");

		secondCompany = new Company();
		secondCompany.setName("Company 2");
		secondCompany.setDescription("Description 2");

		nullCompanyName = new Company();
		nullCompanyName.setName(null);
		nullCompanyName.setDescription("Description 1");

		blankCompanyName = new Company();
		blankCompanyName.setName("");
		blankCompanyName.setDescription("Description 1");

		nullCompanyDescription = new Company();
		nullCompanyDescription.setName("Company 1");
		nullCompanyDescription.setDescription(null);

		blankCompanyDescription = new Company();
		blankCompanyDescription.setName("Company 1");
		blankCompanyDescription.setDescription("");

		companyId = new CompanyId(1L);
		nullCompanyId = new CompanyId();
		negativeCompanyId = new CompanyId(-1L);
		outOfBoundsCompanyId = new CompanyId(Long.MAX_VALUE);
	}

	@AfterEach
	public void deleteEntities() {
		companyRepository.deleteAll();
	}

	@Test
	@DisplayName("It should create the company with completely filled attributes successfully!")
	void createCompany() {
		String response = REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		Assertions.assertAll("Successful Company Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The company is created successfully!"));
	}

	@Test
	@DisplayName("It should not create the company with null company name!")
	void createNullCompanyName() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, nullCompanyName, String.class));
		Assertions.assertAll("Failed Company Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be null.")));
	}

	@Test
	@DisplayName("It should not create the company with blank company name!")
	void createBlankCompanyName() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.postForObject(base_url, blankCompanyName, String.class));
		Assertions.assertAll("Failed Company Creation",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be blank.")));
	}

	@Test
	@DisplayName("It should create the company with null company description successfully!")
	void createNullCompanyDescription() {
		String response = REST_TEMPLATE.postForObject(base_url, nullCompanyDescription, String.class);
		Assertions.assertAll("Successful Company Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The company is created successfully!"));
	}

	@Test
	@DisplayName("It should create the company with blank company description successfully!")
	void createBlankCompanyDescription() {
		String response = REST_TEMPLATE.postForObject(base_url, blankCompanyDescription, String.class);
		Assertions.assertAll("Successful Company Creation",
				() -> Assertions.assertNotNull(response),
				() -> Assertions.assertEquals(response, "The company is created successfully!"));
	}

	@Test
	@DisplayName("It should fetch all of the companies in the database.")
	void getAllCompanies() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		Assertions.assertAll("Successful Companies Retrieval",
				() -> Assertions.assertNotNull(companyList),
				() -> Assertions.assertEquals(companyList.size() , 2),
				() -> Assertions.assertNotNull(companyList.get(0).getId()),
				() -> Assertions.assertNotNull(companyList.get(1).getId()),
				() -> Assertions.assertEquals(companyList.get(0), firstCompany),
				() -> Assertions.assertEquals(companyList.get(1), secondCompany));
	}

	@Test
	@DisplayName("It should fetch the specific company given its id in the database.")
	void getCompanyById() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(0).getId());
		Company company = REST_TEMPLATE.getForObject(base_url + "/" + companyId.getId(), Company.class);
		Assertions.assertAll("Successful Company Retrieval",
				() -> Assertions.assertNotNull(company),
				() -> Assertions.assertEquals(company, firstCompany));
	}

	@Test
	@DisplayName("It should not fetch the specific company given its id in the database because of the null id constraint violation.")
	void getCompanyByIdNullCompanyId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + nullCompanyId.getId(), Company.class));
		Assertions.assertAll("Failed Company Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should not be null.")));
	}

	@Test
	@DisplayName("It should not fetch the specific company given its id in the database because of the negative id constraint violation.")
	void getJobByIdNegativeJobId() {
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + negativeCompanyId.getId(), Company.class));
		Assertions.assertAll("Failed Company Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should be positive.")));
	}

	@Test
	@DisplayName("It should not fetch the specific company given its id in the database because of the out of bounds id.")
	void getCompanyByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(0).getId() + companyList.size());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.getForObject(base_url + "/" + companyId.getId(), Company.class));
		Assertions.assertAll("Failed Company Retrieval",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company is not found!")));
	}

	@Test
	@DisplayName("It should update the specific company with completely filled attributes successfully!")
	void updateCompany() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", firstCompany, companyId.getId());
		companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		List<Company> finalCompanyList = companyList;
		Assertions.assertAll("Successful Company Update",
				() -> Assertions.assertNotNull(finalCompanyList),
				() -> Assertions.assertEquals(finalCompanyList.size() , 2),
				() -> Assertions.assertNotNull(finalCompanyList.get(0).getId()),
				() -> Assertions.assertNotNull(finalCompanyList.get(1).getId()),
				() -> Assertions.assertEquals(finalCompanyList.get(0), firstCompany),
				() -> Assertions.assertEquals(finalCompanyList.get(1), firstCompany));
	}

	@Test
	@DisplayName("It should not update the company with null company name!")
	void updateNullCompanyName() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", nullCompanyName, companyId.getId()));
		Assertions.assertAll("Failed Company Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be blank.")),
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be null.")));
	}

	@Test
	@DisplayName("It should not update the company with blank company name!")
	void updateBlankCompanyName() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", blankCompanyName, companyId.getId()));
		Assertions.assertAll("Failed Company Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("Please provide a valid name, company name should not be blank.")));
	}

	@Test
	@DisplayName("It should update the company with null company description successfully!")
	void updateNullCompanyDescription() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", nullCompanyDescription, companyId.getId());
		companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		List<Company> finalCompanyList = companyList;
		Assertions.assertAll("Successful Company Update",
				() -> Assertions.assertNotNull(finalCompanyList),
				() -> Assertions.assertEquals(finalCompanyList.size() , 2),
				() -> Assertions.assertNotNull(finalCompanyList.get(0).getId()),
				() -> Assertions.assertNotNull(finalCompanyList.get(1).getId()),
				() -> Assertions.assertEquals(finalCompanyList.get(0), firstCompany),
				() -> Assertions.assertEquals(finalCompanyList.get(1), nullCompanyDescription));
	}

	@Test
	@DisplayName("It should update the company with blank company description successfully!")
	void updateBlankCompanyDescription() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		REST_TEMPLATE.put(base_url + "/{id}", blankCompanyDescription, companyId.getId());
		companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		List<Company> finalCompanyList = companyList;
		Assertions.assertAll("Successful Company Update",
				() -> Assertions.assertNotNull(finalCompanyList),
				() -> Assertions.assertEquals(finalCompanyList.size() , 2),
				() -> Assertions.assertNotNull(finalCompanyList.get(0).getId()),
				() -> Assertions.assertNotNull(finalCompanyList.get(1).getId()),
				() -> Assertions.assertEquals(finalCompanyList.get(0), firstCompany),
				() -> Assertions.assertEquals(finalCompanyList.get(1), blankCompanyDescription));
	}

	@Test
	@DisplayName("It should not update the specific company given its id in the database because of the negative id constraint violation.")
	void updateCompanyByIdNegativeCompanyId() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstCompany, negativeCompanyId.getId()));
		Assertions.assertAll("Failed Company Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should be positive.")));
	}

	@Test
	@DisplayName("It should not update the specific company given its id in the database because of the out of bounds id.")
	void updateCompanyByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.put(base_url + "/{id}", firstCompany, outOfBoundsCompanyId.getId()));
		Assertions.assertAll("Failed Company Update",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company is not found!")));
	}

	@Test
	@DisplayName("It should not delete the specific company given its id in the database because of the negative id constraint violation.")
	void deleteJobByIdNegativeJobId() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", negativeCompanyId.getId()));
		Assertions.assertAll("Failed Company Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company id should be positive.")));
	}

	@Test
	@DisplayName("It should not delete the specific company given its id in the database because of the out of bounds id.")
	void deleteJobByIdOutOfBounds() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		HttpClientErrorException exception = Assertions.assertThrows(HttpClientErrorException.class, () -> REST_TEMPLATE.delete(base_url + "/{id}", outOfBoundsCompanyId.getId()));
		Assertions.assertAll("Failed Company Deletion",
				() -> Assertions.assertTrue(exception.getLocalizedMessage().contains("The company is not found!")));
	}


	@Test
	@DisplayName("It should delete the specific company given its id in the database.")
	void deleteJobById() {
		REST_TEMPLATE.postForObject(base_url, firstCompany, String.class);
		REST_TEMPLATE.postForObject(base_url, secondCompany, String.class);
		ParameterizedTypeReference<List<Company>> responseType =
				new ParameterizedTypeReference<>() {};
		RequestEntity<Void> request = RequestEntity.get(base_url).accept(MediaType.APPLICATION_JSON).build();
		List<Company> companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		companyId.setId(companyList.get(1).getId());
		REST_TEMPLATE.delete(base_url + "/{id}", companyId.getId());
		companyList = REST_TEMPLATE.exchange(request, responseType).getBody();
		assert companyList != null;
		List<Company> finalCompanyList = companyList;
		Assertions.assertAll("Successful Company Deletion",
				() -> Assertions.assertEquals(finalCompanyList.size(), 1),
				() -> Assertions.assertNotEquals(finalCompanyList.get(0), secondCompany));
	}

}
