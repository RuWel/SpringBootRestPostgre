package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tutorial.controller.TutorialController;
import com.tutorial.model.Tutorial;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TutorialControllerTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private TutorialController tutorialController;
		
	private String url;
	
	private Long id1;
	private Long id2;
	
	@BeforeEach
	void setupDatabaseForTest() {
		url = "http://localhost:" + port + "/api/tutorials";
		
		// delete all tutorials
		testRestTemplate.delete(url, Tutorial.class);
	
		Tutorial input = new Tutorial("testtitle1", "testdescription1", false);
		ResponseEntity<Tutorial> result = this.testRestTemplate.postForEntity(url, input, Tutorial.class);
		id1 = result.getBody().getId();

		input = new Tutorial("testtitle2", "testdescription2", false);
		result = this.testRestTemplate.postForEntity(url, input, Tutorial.class);
		id2 = result.getBody().getId();
	}
	
	@Test
	void contextLoads() throws Exception {
		assertThat(tutorialController).isNotNull();
	}
	
	@Test
	void getAllTutorialsTest() {
		assertThat(this.testRestTemplate.getForObject(url, List.class)).isNotNull();
	}
	
	@Test
	void createTutorialTest() {
		Tutorial input = new Tutorial("testtitle3", "testdescription3", false);
		
		ResponseEntity<Tutorial> result = this.testRestTemplate.postForEntity(url, input, Tutorial.class);

		Assertions.assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());

		assertThat(result.getBody().getId()).isNotEqualTo(0);
		assertThat(result.getBody().getTitle()).isEqualTo("testtitle3");
		assertThat(result.getBody().getDescription()).isEqualTo("testdescription3");
		assertThat(result.getBody().isPublished()).isFalse();
	}

	@Test
	void createTutorialTestWithException() {
		Tutorial input = new Tutorial("", "testdescription4", false);

		ResponseEntity<Tutorial> result = this.testRestTemplate.postForEntity(url, input, Tutorial.class);
		
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode().value());
	}

	@Test
	void updateTutorialTest() {
		Tutorial input = new Tutorial("testtitle_1", "testdescription_1", false);

		this.testRestTemplate.put(url + "/" + id1, input);
	}

	@Test
	void updateTutorialNotFoundTest() {
		Tutorial input = new Tutorial("testtitle_1", "testdescription_1", false);

		ResponseEntity<Tutorial> response = testRestTemplate.exchange(
	                url + "/-1",
	                HttpMethod.PUT,
	                new HttpEntity<>(input),
	                Tutorial.class
	        );
		
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void deleteTutorialTest() {
		ResponseEntity<Tutorial> response = testRestTemplate.exchange(
                url + "/" + id1,
                HttpMethod.DELETE,
                null,
                Tutorial.class
        );

		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void deleteTutorialNotFoundTest() {
		ResponseEntity<Tutorial> response = testRestTemplate.exchange(
                url + "/-1",
                HttpMethod.DELETE,
                null,
                Tutorial.class
        );

		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void deleteAllTutorialTest() {
		ResponseEntity<Tutorial> response = testRestTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Tutorial.class
        );

		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void findAllPublishedTutorialNonePublishedTest() {
		ResponseEntity<List> response = testRestTemplate.getForEntity(url + "/published", List.class);
		
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void findAllPublishedTutorialOnePublishedTest() {
		Tutorial input = new Tutorial("anewone", "testdescription", true);

		this.testRestTemplate.put(url + "/" + id2, input);
		
		ResponseEntity<List> response = testRestTemplate.getForEntity(url + "/published", List.class);
		
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
