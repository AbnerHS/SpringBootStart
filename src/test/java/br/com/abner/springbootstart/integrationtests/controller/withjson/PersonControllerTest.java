package br.com.abner.springbootstart.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.abner.springbootstart.config.TestConfig;
import br.com.abner.springbootstart.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.abner.springbootstart.integrationtests.vo.PersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
    }

    @Test
    @Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_GOOGLE)
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(person)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("George", persistedPerson.getFirstName());
        assertEquals("Young", persistedPerson.getLastName());
        assertEquals("São Paulo - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());  
	}

    @Test
    @Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_TWITTER)
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .body(person)
				    .when()
					.post()
				.then()
					.statusCode(403)
				.extract()
					.body()
						.asString();
                        
        assertNotNull(content);
        assertEquals("Invalid CORS request", content);        
	}

    @Test
    @Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_GOOGLE)
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .pathParam("id", person.getId())
				    .when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("George", persistedPerson.getFirstName());
        assertEquals("Young", persistedPerson.getLastName());
        assertEquals("São Paulo - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());  
	}

    @Test
    @Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_TWITTER)
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .pathParam("id", person.getId())
				    .when()
					.get("{id}")
				.then()
					.statusCode(403)
				.extract()
					.body()
						.asString();
		
        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
	}

    private void mockPerson() {
        person.setFirstName("George");
        person.setLastName("Young");
        person.setAddress("São Paulo - Brasil");
        person.setGender("Male");
    }
}
