package io.f12.notionlinkedblog.post.infrastructure;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import io.f12.notionlinkedblog.common.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.series.service.port.SeriesRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
@Slf4j
class PostDataRepositoryTest {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private QuerydslPostRepository querydslPostRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SeriesRepository seriesRepository;

	private UserEntity user;
	private PostEntity post;
	private SeriesEntity series;

	String title = "testTitle";
	String content = "testContent";
	String thumbnail = "testThumbnail";
	String path = "path";

	@BeforeEach
	void init() {
		UserEntity savedUser = UserEntity.builder()
			.username("tester")
			.email("test@test.com")
			.password("nope")
			.build();
		user = userRepository.save(savedUser);

		SeriesEntity savedSeries = SeriesEntity.builder()
			.title("testSeries")
			.user(user)
			.post(new ArrayList<>())
			.build();
		series = seriesRepository.save(savedSeries);

		PostEntity savedPost = PostEntity.builder()
			.title(title)
			.content(content)
			.user(user)
			.thumbnailName(thumbnail)
			.storedThumbnailPath(path)
			.isPublic(true)
			.build();
		post = postRepository.save(savedPost);

	}

	@AfterEach
	void clear() {
		postRepository.deleteAll();
		seriesRepository.deleteAll();
		userRepository.deleteAll();
	}

	@DisplayName("포스트 조회")
	@Nested
	class PostLookup {

		@DisplayName("단건 조회")
		@Nested
		class SingleSearch {

			@DisplayName("ID로 PostDto 조회")
			@Nested
			class LookupPostDtoById {

				@DisplayName("정상 조회")
				@Test
				void successCase() {
					//given
					//when
					userRepository.findById(user.getId())
						.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
					PostEntity searchPost = postRepository.findById(post.getId())
						.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));

					//then
					assertThat(searchPost).extracting(PostEntity::getTitle).isEqualTo(title);
					assertThat(searchPost).extracting(PostEntity::getContent).isEqualTo(content);
				}

				@DisplayName("실패 케이스")
				@Nested
				class FailCase {
					@DisplayName("비정상 조회 - 없는 데이터 조회시")
					@Test
					void searchUnavailablePost() {
						//given
						String title = "testTitle";
						String content = "testContent";

						//when
						UserEntity savedUser = userRepository.findById(user.getId())
							.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

						PostEntity post = PostEntity.builder()
							.title(title)
							.content(content)
							.user(savedUser)
							.isPublic(true)
							.build();
						PostEntity save = postRepository.save(post);
						long searchId = save.getId() + 1;

						//then
						Optional<PostEntity> postById = postRepository.findById(searchId);
						assertThatThrownBy(() -> {
							postById.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
						}).isInstanceOf(IllegalArgumentException.class)
							.hasMessageContaining(POST_NOT_EXIST);

					}
				}
			}

			@DisplayName("ID로 PostEntity 조회")
			@Nested
			class LookupPostById {

				@DisplayName("정상 조회")
				@Test
				void successfulCase() {
					//given
					//when
					PostEntity searchPostById = postRepository.findById(post.getId())
						.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
					//then
					assertThat(searchPostById).extracting(PostEntity::getTitle).isEqualTo(title);
					assertThat(searchPostById).extracting(PostEntity::getContent).isEqualTo(content);

				}

				@DisplayName("실패 케이스")
				@Nested
				class FaileCase {

					@DisplayName("존재하지 않는 포스트 조회")
					@Test
					void searchUnavailablePost() {
						//given
						Long postId = post.getId() + 1L;
						//when
						//then
						assertThatThrownBy(() -> {
							postRepository.findById(postId)
								.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
						}).isInstanceOf(IllegalArgumentException.class)
							.hasMessageContaining(POST_NOT_EXIST);
					}

				}
			}

		}

		@DisplayName("다건 조회")
		@Nested
		class MultiSearch {
			@DisplayName("제목으로 조회")
			@Nested
			class LookupPostsByTitle {

				@DisplayName("성공 케이스")
				@Nested
				class SuccessCase {
					@DisplayName("정상 조회 - 데이터 0개")
					@Test
					void successfulCase_NoData() {
						//given
						String example = "NoData";
						PageRequest paging = PageRequest.of(0, 20);
						//when
						List<Long> ids = querydslPostRepository.findPostIdsByTitle(example, paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(
							ids);
						//then
						assertThat(posts).isEmpty();

					}

					@DisplayName("정상 조회 - 데이터 1개")
					@Test
					void successfulCase_SingleData() {
						//given
						PageRequest paging = PageRequest.of(0, 20);
						//when
						List<Long> ids = querydslPostRepository.findPostIdsByTitle(title, paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(
							ids);
						PostEntity post = posts.get(0);

						//then
						assertThat(posts).size().isEqualTo(1);
						assertThat(post).extracting("title").isEqualTo(title);
						assertThat(post).extracting("content").isEqualTo(content);

					}

					@DisplayName("정상 조회 - paging 갯수 이상")
					@Test
					void successfulCase_MultiData() {
						//given
						UserEntity savedUser = userRepository.findById(user.getId())
							.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
						for (int i = 0; i < 30; i++) {
							PostEntity savedPost = PostEntity.builder()
								.title(title + i)
								.content(content)
								.user(user)
								.isPublic(true)
								.build();
							postRepository.save(savedPost);
						}
						PageRequest paging1 = PageRequest.of(0, 20);
						PageRequest paging2 = PageRequest.of(1, 20);

						//when
						List<Long> ids1 = querydslPostRepository.findPostIdsByTitle(title, paging1);
						List<PostEntity> posts1 = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(
							ids1);

						List<Long> ids2 = querydslPostRepository.findPostIdsByTitle(title, paging2);
						List<PostEntity> posts2 = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(
							ids2);
						//then
						assertThat(posts1).size().isEqualTo(20);
						assertThat(posts2).size().isEqualTo(11);

					}
				}
			}

			@DisplayName("내용으로 조회")
			@Nested
			class LookupPostByContent {
				@DisplayName("성공 케이스")
				@Nested
				class SuccessCase {
					@DisplayName("정상 조회 - 데이터 0개")
					@Test
					void successfulCase_NoData() {
						//given
						String example = "NoData";
						PageRequest paging = PageRequest.of(0, 20);
						//when
						// Slice<PostEntity> postByContent = postRepository.findByContent(example, paging);
						List<Long> ids = querydslPostRepository.findPostIdsByContent(example, paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(
							ids);
						//then
						assertThat(posts).isEmpty();
					}

					@DisplayName("정상 조회 - 데이터 1개")
					@Test
					void successfulCase_SingleData() {
						//given
						PageRequest paging = PageRequest.of(0, 20);
						//when

						List<Long> ids = querydslPostRepository.findPostIdsByContent(content, paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(
							ids);
						PostEntity post = posts.get(0);

						//then
						assertThat(posts).size().isEqualTo(1);
						assertThat(post).extracting("title").isEqualTo(title);
						assertThat(post).extracting("content").isEqualTo(content);
					}

					@DisplayName("정상 조회 - paging 갯수 이상")
					@Test
					void successfulCase_MultiData() {
						//given
						UserEntity savedUser = userRepository.findById(user.getId())
							.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

						for (int i = 0; i < 30; i++) {
							PostEntity savedPost = PostEntity.builder()
								.title(title + i)
								.content(content + i)
								.user(user)
								.isPublic(true)
								.build();
							postRepository.save(savedPost);
						}
						PageRequest paging1 = PageRequest.of(0, 20);
						PageRequest paging2 = PageRequest.of(1, 20);
						//when
						List<Long> ids1 = querydslPostRepository.findPostIdsByContent(content, paging1);
						List<PostEntity> posts1 = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(
							ids1);

						List<Long> ids2 = querydslPostRepository.findPostIdsByContent(content, paging2);
						List<PostEntity> posts2 = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(
							ids2);

						//then
						assertThat(posts1).size().isEqualTo(20);
						assertThat(posts2).size().isEqualTo(11);

					}
				}
			}
		}

		@DisplayName("포스트 정렬 조회")
		@Nested
		class SortByList {
			@BeforeEach
			void init() {
				for (int i = 0; i < 10; i++) {
					PostEntity newPost = PostEntity.builder()
						.title(title + " " + i)
						.content(content)
						.user(user)
						.isPublic(true)
						.createdAt(LocalDateTime.of(2023, 1, i + 1, 0, 0))
						.build();
					PostEntity save = postRepository.save(newPost);
					// log.info("save.isEmpty: {}", save.getId());
					series.addPost(save);
				}
				// entityManager.merge(series);
			}

			@DisplayName("최신순 조회")
			@Nested
			class LookupByDate {
				@DisplayName("성공 케이스")
				@Nested
				class SuccessCase {
					@DisplayName("조회 성공")
					@Test
					void successful() {
						//given
						PageRequest paging = PageRequest.of(0, 3);
						//when
						List<Long> ids = querydslPostRepository.findLatestPostIdsByCreatedAtDesc(paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(
							ids);
						//then
						assertThat(posts).size().isEqualTo(paging.getPageSize());
					}
				}
			}

			@DisplayName("인기순 조회")
			@Nested
			class LookupByPopularity {
				@DisplayName("성공 케이스")
				@Nested
				class SuccessCase {
					@DisplayName("조회 성공")
					@Test
					void successful() {
						//given
						PageRequest paging = PageRequest.of(0, 3);
						//when
						List<Long> ids = querydslPostRepository.findPopularityPostIdsByViewCountAtDesc(paging);
						List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(
							ids);
						//then
						assertThat(posts).size().isEqualTo(paging.getPageSize());
					}
				}
			}

			@DisplayName("시리즈 Id로 조회")
			@Nested
			class LookupBySeriesId {
				@DisplayName("오름차순")
				@Nested
				class LookupAsc {
					@DisplayName("성공 케이스")
					@Nested
					class SuccessCase {
						@DisplayName("조회 성공")
						@Test
						void successful() {
							//given
							PageRequest paging = PageRequest.of(0, 3);
							//when
							List<Long> ids = querydslPostRepository.findIdsBySeriesIdAsc(series.getId(), paging);
							List<PostEntity> posts = querydslPostRepository.findByIdsJoinWithSeries(ids);
							//then
							assertThat(posts).size().isEqualTo(paging.getPageSize());
						}
					}
				}

				@DisplayName("내림차순")
				@Nested
				class LookupDesc {
					@DisplayName("성공 케이스")
					@Nested
					class SuccessCase {
						@DisplayName("조회 성공")
						@Test
						void successful() {
							//given
							PageRequest paging = PageRequest.of(0, 3);
							//when
							List<Long> ids = querydslPostRepository.findIdsBySeriesIdDesc(series.getId(), paging);
							List<PostEntity> posts = querydslPostRepository.findByIdsJoinWithSeries(ids);
							//then
							assertThat(posts).size().isEqualTo(paging.getPageSize());
						}
					}

				}
			}

		}

	}

	@DisplayName("포스트 편집")
	@Nested
	class EditPost {
		@DisplayName("성공 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("미 변경")
			@Test
			void partialChange() {
				//given
				//when
				PostEntity editPost = postRepository.findById(post.getId())
					.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));

				editPost.editPost("", null);

				PostEntity editedPost = postRepository.findById(post.getId())
					.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
				//then
				assertThat(editedPost).extracting("title").isEqualTo(title);
				assertThat(editedPost).extracting("content").isEqualTo(content);

			}

			@DisplayName("전체 변경")
			@Test
			void fullChange() {
				//given
				String changedTitle = "changedTitle";
				String changedContent = "changedContent";
				//when
				PostEntity editPost = postRepository.findById(post.getId())
					.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));

				editPost.editPost(changedTitle, changedContent);

				PostEntity editedPost = postRepository.findById(post.getId())
					.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
				//then
				assertThat(editedPost).extracting("title").isEqualTo(changedTitle);
				assertThat(editedPost).extracting("content").isEqualTo(changedContent);
			}

		}

	}

	@DisplayName("썸네일 조회")
	@Nested
	class LookupThumbnailImage {
		@DisplayName("ID로 PostDto 조회")
		@Nested
		class LookupThumbnailPathByThumbnailName {
			@DisplayName("정상 조회")
			@Test
			void successCase() {
				//given

				//when
				String thumbnailPathWithName = postRepository.findThumbnailPathWithName(thumbnail);
				//then
				assertThat(thumbnailPathWithName).isEqualTo(path);

			}
		}
	}

}