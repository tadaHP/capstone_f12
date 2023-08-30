package io.f12.notionlinkedblog.medium.series.infrastructure;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import io.f12.notionlinkedblog.common.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.series.service.port.SeriesRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
@Slf4j
class SeriesDataRepositoryTest {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SeriesRepository seriesRepository;
	@Autowired
	private EntityManager entityManager;

	private UserEntity user;
	private SeriesEntity series;
	private PostEntity postA;
	private PostEntity postB;
	private PostEntity postC;

	private final String titleA = "testTitleA";
	private final String contentA = "testContentA";
	private final String thumbnailA = "testThumbnailA";
	private final String pathA = "pathA";
	private final String titleB = "testTitleB";
	private final String contentB = "testContentB";
	private final String thumbnailB = "testThumbnailB";
	private final String pathB = "pathB";
	private final String titleC = "testTitleC";
	private final String contentC = "testContentC";
	private final String thumbnailC = "testThumbnailc";
	private final String pathC = "pathC";

	@BeforeEach
	void init() {
		UserEntity savedUser = UserEntity.builder()
			.id(1L)
			.username("tester")
			.email("test@test.com")
			.password("nope")
			.build();
		user = userRepository.save(savedUser);

		SeriesEntity savedSeries = SeriesEntity.builder()
			.id(1L)
			.user(user)
			.title("testSeries")
			.build();
		series = seriesRepository.save(savedSeries);

		PostEntity savedPostA = PostEntity.builder()
			.id(1L)
			.title(titleA)
			.content(contentA)
			.user(user)
			.series(series)
			.thumbnailName(thumbnailA)
			.storedThumbnailPath(pathA)
			.isPublic(true)
			.createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
			.build();
		postA = postRepository.save(savedPostA);
		PostEntity savedPostB = PostEntity.builder()
			.id(2L)
			.title(titleB)
			.content(contentB)
			.user(user)
			.series(series)
			.thumbnailName(thumbnailB)
			.storedThumbnailPath(pathB)
			.isPublic(true)
			.createdAt(LocalDateTime.of(2023, 2, 1, 0, 0))
			.build();
		postB = postRepository.save(savedPostB);
		PostEntity savedPostC = PostEntity.builder()
			.id(3L)
			.title(titleC)
			.content(contentC)
			.user(user)
			.series(series)
			.thumbnailName(thumbnailC)
			.storedThumbnailPath(pathC)
			.isPublic(true)
			.createdAt(LocalDateTime.of(2023, 3, 1, 0, 0))
			.build();
		postC = postRepository.save(savedPostC);

		List<PostEntity> postList = new ArrayList<>();
		postList.add(postA);
		postList.add(postB);
		postList.add(postC);

		List<PostEntity> post = series.getPost();
		post = postList;

		entityManager.flush();
		entityManager.clear();

	}

	@AfterEach
	void clear() {
		postRepository.deleteAll();
		seriesRepository.deleteAll();
		userRepository.deleteAll();
	}

	@DisplayName("간단 시리즈 조회")
	@Nested
	class SimpleSeriesLookUp {

		@DisplayName("실패케이스")
		@Test
		void failureCase() {
			//given
			Long seriesId = 0L;
			//when
			assertThatThrownBy(() -> {
				SeriesEntity searchSeries = seriesRepository.findSeriesById(seriesId)
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID"));
			}).isInstanceOf(IllegalArgumentException.class);

		}
	}
}
