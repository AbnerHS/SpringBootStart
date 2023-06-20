package br.com.abner.springbootstart.integrationtests.controller.cors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

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
import br.com.abner.springbootstart.data.vo.v1.AccountCredentialsVO;
import br.com.abner.springbootstart.data.vo.v1.TokenVO;
import br.com.abner.springbootstart.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.abner.springbootstart.integrationtests.vo.BookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerCorsTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();
    }

    @Test
    @Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("abner", "admin123");

        String accessToken = given()
            .basePath("/auth/signin")
                .port(TestConfig.SERVER_PORT)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
            .body(user)
                .when()
            .post()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                            .as(TokenVO.class)
                        .getAccessToken();
        
        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
            .setBasePath("/api/book/v1")
            .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
        mockBook();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_GOOGLE)
                    .body(book)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        book = persistedBook;
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertTrue(persistedBook.getId() > 0);

        assertEquals("Dan Brown", persistedBook.getAuthor());
        assertEquals("Origin", persistedBook.getTitle());
        assertEquals(25.00, persistedBook.getPrice());
	}

    @Test
    @Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        mockBook();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_TWITTER)
                    .body(book)
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
        mockBook();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_GOOGLE)
                    .pathParam("id", book.getId())
				    .when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        book = persistedBook;
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Dan Brown", persistedBook.getAuthor());
        assertEquals("Origin", persistedBook.getTitle());
        assertEquals(25.00, persistedBook.getPrice()); 
	}

    @Test
    @Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
        mockBook();

		String content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                    .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_TWITTER)
                    .pathParam("id", book.getId())
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

    private void mockBook() {
        book.setAuthor("Dan Brown");
        book.setTitle("Origin");
        book.setLaunchDate(new Date());
        book.setPrice(Double.valueOf(25.00));
    }
}
