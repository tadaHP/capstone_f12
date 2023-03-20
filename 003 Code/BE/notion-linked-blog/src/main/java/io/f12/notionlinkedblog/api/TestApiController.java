package io.f12.notionlinkedblog.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.domain.Tests;
import io.f12.notionlinkedblog.domain.TestsRepository;
import io.f12.notionlinkedblog.domain.dto.TestsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/test")
@RestController
public class TestApiController {

	private final TestsRepository testsRepository;

	@GetMapping("/{id}")
	public ResponseEntity<TestsResponseDto> getTest(@PathVariable Long id) {
		Tests foundTests = testsRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("아이디가 조회되지 않습니다. id: " + id));
		TestsResponseDto responseDto = TestsResponseDto.builder().name(foundTests.getName()).build();
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping
	public ResponseEntity<TestsResponseDto> postTest(@RequestBody String name) {
		Tests tests = Tests.builder().name(name).build();
		Tests savedTests = testsRepository.save(tests);
		TestsResponseDto responseDto = TestsResponseDto.builder().name(savedTests.getName()).build();
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
}
