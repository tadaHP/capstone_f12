package io.f12.notionlinkedblog.repository.series;

import static org.assertj.core.api.Assertions.*;

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
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
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
	void init() throws InterruptedException {
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
			.build();
		postC = postDataRepository.save(savedPostC);

		List<Post> postList = new ArrayList<>();
		postList.add(postA);
		postList.add(postB);
		postList.add(postC);

		List<Post> post = series.getPost();
		post = postList;

	}

	@AfterEach
	void clear() {
		seriesDataRepository.deleteAll();
		postDataRepository.deleteAll();
		userDataRepository.deleteAll();
		// entityManager.createNativeQuery("ALTER SEQUENCE user_seq RESTART WITH 1").executeUpdate();
		// entityManager.createNativeQuery("ALTER SEQUENCE post_seq RESTART WITH 1").executeUpdate();
		// entityManager.createNativeQuery("ALTER SEQUENCE series_seq RESTART WITH 1").executeUpdate();
	}

	@DisplayName("시리즈로 포스트 조회")
	@Nested
	class PostLookupBySeries {

		@DisplayName("시리즈 오름차순 조회") //asc
		@Nested
		class OrderByAsc {
			@DisplayName("성공 케이스")
			@Nested
			class SuccessfulCase {
				@DisplayName("데이터 존재")
				@Test
				void dataExist() {
					//given
					//when
					List<PostSearchDto> posts
						= seriesDataRepository.findPostDtosBySeriesIdOrderByCreatedAtAsc(series.getId());
					//then
					assertThat(posts).size().isEqualTo(3);
				}

				@DisplayName("데이터 미존재")
				@Test
				void dataNonExist() {
					//given
					//when
					List<PostSearchDto> posts
						= seriesDataRepository.findPostDtosBySeriesIdOrderByCreatedAtAsc(-1L);
					//then
					assertThat(posts).size().isEqualTo(0);
				}
			}
		}

		@DisplayName("시리즈 내림차순 조회") //desc
		@Nested
		class OrderByDesc {
			@DisplayName("성공 케이스")
			@Nested
			class SuccessfulCase {
				@DisplayName("데이터 존재")
				@Test
				void dataExist() {
					//given
					//when
					List<PostSearchDto> posts
						= seriesDataRepository.findPostDtosBySeriesIdOrderByCreatedAtDesc(series.getId());
					//then
					assertThat(posts).size().isEqualTo(3);

				}

				@DisplayName("데이터 미존재")
				@Test
				void dataNonExist() {
					//given
					//when
					List<PostSearchDto> posts
						= seriesDataRepository.findPostDtosBySeriesIdOrderByCreatedAtDesc(-1L);
					//then
					assertThat(posts).size().isEqualTo(0);

				}
			}

		}

	}

}
