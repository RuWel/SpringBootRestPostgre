package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tutorial.controller.TutorialController;
import com.tutorial.model.Tutorial;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TutorialControllerTest {

	@LocalServerPort
	private int port;
		
	@Autowired
	private TutorialController tutorialController;
		
	private Long id1;
	
	@BeforeEach
	void setupDatabaseForTest() {
		tutorialController.deleteAllTutorials();
	
		Tutorial input = new Tutorial("Hello World", "testdescription1", false);
		ResponseEntity<Tutorial> result = this.tutorialController.createTutorial(input);
		id1 = result.getBody().getId();

		input = new Tutorial("Miss World", "testdescription2", false);
		result = this.tutorialController.createTutorial(input);
	}
	
	@Test
	void contextLoads() throws Exception {
		assertThat(tutorialController).isNotNull();
	}
	
	@Test
	void getAllTutorialsTest() {
		assertThat(this.tutorialController.getAllTutorials(null)).isNotNull();
	}
	
	@Test
	void findTutorialsByKeywordHelloTest () {
		ResponseEntity<List<Tutorial>> result = this.tutorialController.getAllTutorials("Hello");
		Assertions.assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
		Assertions.assertEquals(1, result.getBody().size());

		Tutorial tutorial = result.getBody().get(0);
		assertThat(tutorial.getTitle().contains("Hello"));
	}
	
	@Test
	void findTutorialsByKeywordWorldTest () {
		ResponseEntity<List<Tutorial>> result = this.tutorialController.getAllTutorials("World");		
		Assertions.assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
		Assertions.assertEquals(2, result.getBody().size());

		Tutorial tutorial = result.getBody().get(0);
		assertThat(tutorial.getTitle().contains("World"));
		
		tutorial = result.getBody().get(1);
		assertThat(tutorial.getTitle().contains("World"));
	}

	@Test
	void createTutorialTest() {
		Tutorial tutorial = new Tutorial("testtitle3", "testdescription3", false);
		
		ResponseEntity<Tutorial> result = this.tutorialController.createTutorial(tutorial);
		
		Assertions.assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());

		assertThat(result.getBody().getId()).isNotEqualTo(0);
		assertThat(result.getBody().getTitle()).isEqualTo("testtitle3");
		assertThat(result.getBody().getDescription()).isEqualTo("testdescription3");
		assertThat(result.getBody().isPublished()).isFalse();
	}

	@Test
	void createTutorialTestWithException() {
		Tutorial input = new Tutorial("", "testdescription4", false);

		ResponseEntity<Tutorial> result = this.tutorialController.createTutorial(input);
		
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode().value());
	}

	@Test
	void updateTutorialTest() {
		Tutorial tutorial = new Tutorial("testtitle_1", "testdescription_1", false);

		ResponseEntity<Tutorial> result = this.tutorialController.updateTutorial(id1, tutorial);
		assertThat(HttpStatus.OK.equals(result.getStatusCode()));

		assertThat(tutorial.getTitle()).isEqualTo(result.getBody().getTitle());
		assertThat(tutorial.getDescription()).isEqualTo(result.getBody().getDescription());
		assertThat(tutorial.isPublished()).isEqualTo(result.getBody().isPublished());
	}

	@Test
	void updateTutorialNotFoundTest() {
		Tutorial tutorial = new Tutorial("testtitle_1", "testdescription_1", false);

		ResponseEntity<Tutorial> response = this.tutorialController.updateTutorial(-1L, tutorial);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void deleteTutorialTest() {
		ResponseEntity<Tutorial> response = this.tutorialController.deleteTutorial(id1);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void deleteTutorialNotFoundTest() {
		ResponseEntity<Tutorial> response = this.tutorialController.deleteTutorial(-1L);
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void deleteAllTutorialTest() {
		ResponseEntity<Tutorial> response = this.tutorialController.deleteAllTutorials();
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void findAllPublishedTutorialNonePublishedTest() {
		ResponseEntity<List<Tutorial>> response = this.tutorialController.findAllPublishedTutorials();
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void findAllPublishedTutorialOnePublishedTest() {
		Tutorial tutorial = new Tutorial("anewone", "testdescription", true);
		this.tutorialController.createTutorial(tutorial);
		
		ResponseEntity<List<Tutorial>> response = this.tutorialController.findAllPublishedTutorials();
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@AfterEach
	void cleanupDatabaseAfterTest() {
		tutorialController.deleteAllTutorials();
	}
}
