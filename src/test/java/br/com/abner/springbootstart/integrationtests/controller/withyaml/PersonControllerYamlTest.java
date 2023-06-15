package br.com.abner.springbootstart.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.abner.springbootstart.config.TestConfig;
import br.com.abner.springbootstart.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.abner.springbootstart.integrationtests.vo.AccountCredentialsVO;
import br.com.abner.springbootstart.integrationtests.vo.PersonVO;
import br.com.abner.springbootstart.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
    private static YMLMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();
        person = new PersonVO();
    }

    @Test
    @Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("abner", "admin123");

        String accessToken = given()
            .config(RestAssuredConfig
                .config()
                .encoderConfig(EncoderConfig.encoderConfig()
                    .encodeContentTypeAs(
                        TestConfig.CONTENT_TYPE_YML,
                        ContentType.TEXT)))
            .basePath("/auth/signin")
                .port(TestConfig.SERVER_PORT)
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
            .body(user, objectMapper)
                .when()
            .post()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                            .as(TokenVO.class, objectMapper)
                        .getAccessToken();
        
        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

		PersonVO persistedPerson = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .body(person, objectMapper)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Isaac", persistedPerson.getFirstName());
        assertEquals("Newton", persistedPerson.getLastName());
        assertEquals("Reino Unido", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());  
	}

    @Test
    @Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
        person.setLastName("Newton RS");

		PersonVO persistedPerson = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .body(person, objectMapper)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Isaac", persistedPerson.getFirstName());
        assertEquals("Newton RS", persistedPerson.getLastName());
        assertEquals("Reino Unido", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());   
	}

    @Test
    @Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
        PersonVO persistedPerson = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .pathParam("id", person.getId())
				    .when()
					.patch("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Isaac", persistedPerson.getFirstName());
        assertEquals("Newton RS", persistedPerson.getLastName());
        assertEquals("Reino Unido", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());   
	}

    @Test
    @Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
        PersonVO persistedPerson = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .pathParam("id", person.getId())
				    .when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(PersonVO.class, objectMapper);
		
        person = persistedPerson;
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Isaac", persistedPerson.getFirstName());
        assertEquals("Newton RS", persistedPerson.getLastName());
        assertEquals("Reino Unido", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());   
	}

    @Test
    @Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
        given().spec(specification)
            .config(RestAssuredConfig
                .config()
                .encoderConfig(EncoderConfig.encoderConfig()
                    .encodeContentTypeAs(
                        TestConfig.CONTENT_TYPE_YML,
                        ContentType.TEXT)))
            .contentType(TestConfig.CONTENT_TYPE_YML)
            .accept(TestConfig.CONTENT_TYPE_YML)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
            .then()
                .statusCode(204);
	}


    @Test
    @Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		PersonVO[] content = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
				    .when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
                        .as(PersonVO[].class, objectMapper);
		
        List<PersonVO> personList = Arrays.asList(content);
        PersonVO foundPersonOne = personList.get(0);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());
        assertTrue(foundPersonOne.getEnabled());

        assertEquals(1, foundPersonOne.getId());

        assertEquals("Abner Henrique", foundPersonOne.getFirstName());
        assertEquals("dos Santos Simas", foundPersonOne.getLastName());
        assertEquals("Rua Itororó, 410", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());
		
        PersonVO foundPersonFive = personList.get(4);

        assertNotNull(foundPersonFive.getId());
        assertNotNull(foundPersonFive.getFirstName());
        assertNotNull(foundPersonFive.getLastName());
        assertNotNull(foundPersonFive.getAddress());
        assertNotNull(foundPersonFive.getGender());
        assertTrue(foundPersonFive.getEnabled());

        assertEquals(5, foundPersonFive.getId());

        assertEquals("Jane", foundPersonFive.getFirstName());
        assertEquals("Smith", foundPersonFive.getLastName());
        assertEquals("456 Elm St", foundPersonFive.getAddress());
        assertEquals("Female", foundPersonFive.getGender());
	}

    @Test
    @Order(7)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
            .setBasePath("/api/person/v1")
            .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
        
        given().spec(specificationWithoutToken)
            .config(RestAssuredConfig
                .config()
                .encoderConfig(EncoderConfig.encoderConfig()
                    .encodeContentTypeAs(
                        TestConfig.CONTENT_TYPE_YML,
                        ContentType.TEXT)))
            .contentType(TestConfig.CONTENT_TYPE_YML)
            .accept(TestConfig.CONTENT_TYPE_YML)
                .when()
                .get()
            .then()
                .statusCode(403);
	}

    private void mockPerson() {
        person.setFirstName("Isaac");
        person.setLastName("Newton");
        person.setAddress("Reino Unido");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
