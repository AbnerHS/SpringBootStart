package br.com.abner.springbootstart.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
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
import br.com.abner.springbootstart.integrationtests.vo.BookVO;
import br.com.abner.springbootstart.integrationtests.vo.PagedModelBook;
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
public class BookControllerYamlTest extends AbstractIntegrationTest {
    
    private static RequestSpecification specification;
    private static YMLMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();

        book = new BookVO();
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

		BookVO persistedBook = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .body(book, objectMapper)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(BookVO.class, objectMapper);
		
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
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
        book.setTitle("Origem");

		BookVO persistedBook = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .body(book, objectMapper)
				    .when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(BookVO.class, objectMapper);
		
        book = persistedBook;
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Dan Brown", persistedBook.getAuthor());
        assertEquals("Origem", persistedBook.getTitle());
        assertEquals(25.00, persistedBook.getPrice());   
	}

    @Test
    @Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
        BookVO persistedBook = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                    .pathParam("id", book.getId())
				    .when()
					.get("{id}")
				.then()
					.statusCode(200)
				.extract()
					.body()
						.as(BookVO.class, objectMapper);
		
        book = persistedBook;
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Dan Brown", persistedBook.getAuthor());
        assertEquals("Origem", persistedBook.getTitle());
        assertEquals(25.00, persistedBook.getPrice());   
	}

    @Test
    @Order(4)
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
                .pathParam("id", book.getId())
                .when()
                .delete("{id}")
            .then()
                .statusCode(204);
	}


    @Test
    @Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		PagedModelBook wrapper = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                .queryParams("page", 0, "size", 10, "direction", "asc")
				    .when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
                        .as(PagedModelBook.class, objectMapper);
		
        
        List<BookVO> bookList = wrapper.getContent();
        BookVO foundBookOne = bookList.get(0);

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getPrice());
        assertNotNull(foundBookOne.getLaunchDate());

        assertEquals(12, foundBookOne.getId());

        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informa\u00E7\u00E3o cotidiana", 
            foundBookOne.getTitle());
        assertEquals(54.0, foundBookOne.getPrice());
		
        BookVO foundBookFive = bookList.get(4);

        assertNotNull(foundBookFive.getId());
        assertNotNull(foundBookFive.getAuthor());
        assertNotNull(foundBookFive.getTitle());
        assertNotNull(foundBookFive.getPrice());
        assertNotNull(foundBookFive.getLaunchDate());

        assertEquals(8, foundBookFive.getId());

        assertEquals("Eric Evans", foundBookFive.getAuthor());
        assertEquals("Domain Driven Design", foundBookFive.getTitle());
        assertEquals(92.0, foundBookFive.getPrice());

	}

    @Test
    @Order(6)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
            .setBasePath("/api/book/v1")
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

    @Test
    @Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		String rawContent = given().spec(specification)
                .config(RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(
                            TestConfig.CONTENT_TYPE_YML,
                            ContentType.TEXT)))
                .contentType(TestConfig.CONTENT_TYPE_YML)
                .accept(TestConfig.CONTENT_TYPE_YML)
                .queryParams("page", 0, "size", 10, "direction", "asc")
				    .when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
                        .asString();

        String content = rawContent.replace("\n", "").replace("\r", "");

        assertTrue(content.contains("links:" +
                "  - rel: \"self\"" +
                "    href: \"http://localhost:8888/api/book/v1/12\""));   
        
        assertTrue(content.contains("- rel: \"first\"" +
                "  href: \"http://localhost:8888/api/book/v1?limit=10&direction=asc&page=0&size=10&sort=title,asc\""));   
        assertTrue(content.contains("- rel: \"self\"" +
                "  href: \"http://localhost:8888/api/book/v1?page=0&limit=10&direction=asc\""));   
        assertTrue(content.contains("- rel: \"next\"" +
                "  href: \"http://localhost:8888/api/book/v1?limit=10&direction=asc&page=1&size=10&sort=title,asc\""));
        assertTrue(content.contains("- rel: \"last\"" +
                "  href: \"http://localhost:8888/api/book/v1?limit=10&direction=asc&page=1&size=10&sort=title,asc\""));   
        
        assertTrue(content.contains("page:" +
                "  size: 10" +
                "  totalElements: 15" +
                "  totalPages: 2" + 
                "  number: 0"));   
    }

    private void mockBook() {
        book.setAuthor("Dan Brown");
        book.setTitle("Origin");
        book.setLaunchDate(new Date());
        book.setPrice(Double.valueOf(25.00));
    }
}
