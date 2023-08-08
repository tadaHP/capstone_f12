package io.f12.notionlinkedblog.repository.series;

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

import io.f12.notionlinkedblog.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.series.Series;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
@Slf4j
class SeriesDataRepositoryTest {

	@Autowired
	private PostDataRepository postDataRepository;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private SeriesDataRepository seriesDataRepository;
	@Autowired
	private EntityManager entityManager;

	private User user;
	private Series series;
	private Post postA;
	private Post postB;
	private Post postC;

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
		User savedUser = User.builder()
			.id(1L)
			.username("tester")
			.email("test@test.com")
			.password("nope")
			.build();
		user = userDataRepository.save(savedUser);

		Series savedSeries = Series.builder()
			.id(1L)
			.user(user)
			.title("testSeries")
			.build();
		series = seriesDataRepository.save(savedSeries);

		Post savedPostA = Post.builder()
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
		postA = postDataRepository.save(savedPostA);
		Post savedPostB = Post.builder()
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
		postB = postDataRepository.save(savedPostB);
		Post savedPostC = Post.builder()
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
		postC = postDataRepository.save(savedPostC);

		List<Post> postList = new ArrayList<>();
		postList.add(postA);
		postList.add(postB);
		postList.add(postC);

		List<Post> post = series.getPost();
		post = postList;

		entityManager.flush();
		entityManager.clear();

	}

	@AfterEach
	void clear() {
		postDataRepository.deleteAll();
		seriesDataRepository.deleteAll();
		userDataRepository.deleteAll();
	}

	@DisplayName("간단 시리즈 조회")
	@Nested
	class SimpleSeriesLookUp {
		@DisplayName("성공케이스")
		@Test
		void successCase() {
			//given
			Long seriesId = series.getId();
			//when
			Series searchSeries = seriesDataRepository.findSeriesById(seriesId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID"));

			//then
			assertThat(searchSeries).extracting("id").isEqualTo(series.getId());
			assertThat(searchSeries).extracting("title").isEqualTo(series.getTitle());
			assertThat(searchSeries.getPost()).size().isEqualTo(3);
			assertThat(searchSeries.getPost().get(0).getId()).isEqualTo(postA.getId());
			assertThat(searchSeries.getPost().get(2).getId()).isEqualTo(postC.getId());

		}

		@DisplayName("실패케이스")
		@Test
		void failureCase() {
			//given
			Long seriesId = 0L;
			//when
			assertThatThrownBy(() -> {
				Series searchSeries = seriesDataRepository.findSeriesById(seriesId)
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID"));
			}).isInstanceOf(IllegalArgumentException.class);

		}
	}
}
