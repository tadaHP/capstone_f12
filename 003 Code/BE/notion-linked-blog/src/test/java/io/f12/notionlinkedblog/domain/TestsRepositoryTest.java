package io.f12.notionlinkedblog.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TestsRepositoryTest {

	@Autowired
	private TestsRepository testsRepository;

	private Tests tests;

	@BeforeEach
	void setup() {
		tests = Tests.builder()
			.name("test")
			.build();
	}

	@DisplayName("findById() 테스트")
	@Test
	void findById() {
		Tests savedTests = testsRepository.save(tests);
		Tests foundTests = testsRepository.findById(savedTests.getId()).orElseThrow();

		assertThat(foundTests).isNotNull();
		assertThat("test").isEqualTo(foundTests.getName());
		assertThat(foundTests.getId()).isGreaterThan(0L);
	}

	@DisplayName("save() 테스트")
	@Test
	void save() {
		Tests savedTests = testsRepository.save(tests);

		assertThat(savedTests).isNotNull();
		assertThat("test").isEqualTo(savedTests.getName());
		assertThat(savedTests.getId()).isGreaterThan(0L);
	}
}
