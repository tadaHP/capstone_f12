package io.f12.notionlinkedblog.api;

import static org.assertj.core.api.Assertions.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.f12.notionlinkedblog.domain.Tests;
import io.f12.notionlinkedblog.domain.TestsRepository;
import io.f12.notionlinkedblog.domain.dto.TestsResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestApiControllerTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TestsRepository testsRepository;

	private Tests tests;

	@BeforeEach
	void setUp() {
		tests = Tests.builder().name("test").build();
	}

	@DisplayName("GET 테스트")
	@Test
	void testGet() {
		//given
		Tests savedTests = testsRepository.save(tests);
		String url = "http://localhost:" + port + "/api/test/" + savedTests.getId();

		//when
		ResponseEntity<TestsResponseDto> responseEntity = restTemplate.getForEntity(url, TestsResponseDto.class);

		//then
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(Objects.requireNonNull(responseEntity.getBody()).getName()).isEqualTo(savedTests.getName());
	}

	@DisplayName("POST 테스트")
	@Test
	void testPost() {
		//given
		String url = "http://localhost:" + port + "/api/test";

		//when
		ResponseEntity<TestsResponseDto> responseEntity = restTemplate.postForEntity(url, tests.getName(),
			TestsResponseDto.class);

		//then
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(Objects.requireNonNull(responseEntity.getBody()).getName()).isEqualTo(tests.getName());
	}
}
