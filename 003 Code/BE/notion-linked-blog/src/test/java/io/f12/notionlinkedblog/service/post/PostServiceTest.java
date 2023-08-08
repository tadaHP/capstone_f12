package io.f12.notionlinkedblog.service.post;

import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.domain.likes.dto.LikeSearchDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchResponseDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.like.LikeDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostServiceTest {

	@InjectMocks
	PostService postService;

	@Mock
	PostDataRepository postDataRepository;

	@Mock
	UserDataRepository userDataRepository;
	@Mock
	LikeDataRepository likeDataRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("포스트 생성")
	@Nested
	class CreatePost {

		@DisplayName("성공케이스")
		@Nested
		class SuccessCase {
			@DisplayName("모든 데이터 존재")
			@Test
			void haveEveryData() throws IOException {
				//given
				Long fakeId = 1L;
				User user = User.builder()
					.id(fakeId)
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String path = "path";
				String description = "description";
				Boolean isPublic = true;

				Post returnPost = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.thumbnailName(thumbnail)
					.storedThumbnailPath(path)
					.build();

				File file = new ClassPathResource("static/images/test.jpg").getFile();

				//Mock
				MultipartFile mockMultipartFile = new MockMultipartFile(file.getName(), new FileInputStream(file));
				given(userDataRepository.findById(fakeId))
					.willReturn(Optional.of(user));
				given(postDataRepository.save(any(Post.class)))
					.willReturn(returnPost);

				//when
				PostSearchDto createdPost = postService.createPost(fakeId, title, content, description, isPublic,
					mockMultipartFile);

				//then
				assertThat(createdPost).extracting("title").isEqualTo(title);
				assertThat(createdPost).extracting("content").isEqualTo(content);
			}

			@DisplayName("섬네일 제외")
			@Test
			void withoutThumbnail() throws IOException {
				//given
				Long fakeId = 1L;
				User user = User.builder()
					.id(fakeId)
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				String title = "testTitle";
				String content = "testContent";
				String description = "description";
				Boolean isPublic = true;

				Post returnPost = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.build();
				//Mock
				given(userDataRepository.findById(fakeId))
					.willReturn(Optional.of(user));
				given(postDataRepository.save(any(Post.class)))
					.willReturn(returnPost);

				//when
				PostSearchDto createdPost = postService.createPost(fakeId, title, content, description, isPublic,
					null);
				//then
				assertThat(createdPost).extracting("title").isEqualTo(title);
				assertThat(createdPost).extracting("content").isEqualTo(content);
				assertThat(createdPost).extracting("requestThumbnailLink").isNull();
			}

		}

		@DisplayName("실패 케이스")
		@Nested
		class FailCase {
			@DisplayName("USER 미존재")
			@Test
			void undefinedUser() {
				//given
				String title = "testTitle";
				String content = "testContent";
				String description = "description";
				Boolean isPublic = true;
				Long fakeId = 1L;

				//Mock
				given(userDataRepository.findById(fakeId))
					.willReturn(null);

				//when
				//then
				assertThatThrownBy(() -> {
					postService.createPost(fakeId, title, content, description, isPublic, null);
				}).isInstanceOf(NullPointerException.class);

			}

		}

	}

	@DisplayName("포스트 조회")
	@Nested
	class LookupPost {

		@DisplayName("title 로 조회")
		@Nested
		class LookupPostByTitle {
			@DisplayName("성공케이스")
			@Test
			void successCase() {
				//given
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String username = "tester";
				String path = "path";

				User user = User.builder()
					.username(username)
					.email("test@gamil.com")
					.password(passwordEncoder.encode("1234"))
					.build();

				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Post post1 = Post.builder()
					.id(fakePostAId)
					.user(user)
					.title(title)
					.content(content)
					.thumbnailName(thumbnail)
					.storedThumbnailPath(path)
					.build();
				Post post2 = Post.builder()
					.id(fakePostBId)
					.user(user)
					.title(title)
					.content(content)
					.build();
				List<Long> ids = new ArrayList<>();
				ids.add(fakePostAId);
				ids.add(fakePostBId);
				List<Post> postList = new ArrayList<>();
				postList.add(post1);
				postList.add(post2);

				SearchRequestDto requestDto = SearchRequestDto.builder()
					.param("test")
					.pageNumber(0)
					.build();
				PageRequest paging = PageRequest.of(requestDto.getPageNumber(), 20);
				//Mock
				given(postDataRepository.findPostIdsByTitle(requestDto.getParam(), paging))
					.willReturn(ids);
				given(postDataRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids))
					.willReturn(postList);
				//when
				PostSearchResponseDto posts = postService.getPostsByTitle(requestDto);
				PostSearchDto postSearchDto = posts.getPosts().get(0);
				//then
				assertThat(posts).extracting(PostSearchResponseDto::getPageSize).isEqualTo(20);
				assertThat(posts).extracting(PostSearchResponseDto::getPageNow).isEqualTo(requestDto.getPageNumber());
				assertThat(posts).extracting(PostSearchResponseDto::getElementsSize).isEqualTo(2);
				assertThat(posts.getPosts()).size().isEqualTo(2);
				assertThat(postSearchDto).extracting("title").isEqualTo(title);
				assertThat(postSearchDto).extracting("author").isEqualTo(username);
			}
		}

		@DisplayName("content 로 조회")
		@Nested
		class LookupPostByContent {
			@DisplayName("성공케이스")
			@Test
			void successCase() {

				//given
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String username = "tester";
				String path = "path";

				User user = User.builder()
					.username(username)
					.email("test@gamil.com")
					.password(passwordEncoder.encode("1234"))
					.build();

				SearchRequestDto requestDto = SearchRequestDto.builder()
					.param("test")
					.pageNumber(0)
					.build();

				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Post post1 = Post.builder()
					.id(fakePostAId)
					.user(user)
					.title(title)
					.content(content)
					.thumbnailName(thumbnail)
					.storedThumbnailPath(path)
					.build();
				Post post2 = Post.builder()
					.id(fakePostBId)
					.user(user)
					.title(title)
					.content(content)
					.build();

				List<Long> ids = new ArrayList<>();
				ids.add(fakePostAId);
				ids.add(fakePostBId);
				List<Post> postList = new ArrayList<>();
				postList.add(post1);
				postList.add(post2);

				PageRequest paging = PageRequest.of(requestDto.getPageNumber(), 20);

				//Mock
				given(postDataRepository.findPostIdsByContent(requestDto.getParam(), paging))
					.willReturn(ids);
				given(postDataRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids))
					.willReturn(postList);
				//when
				PostSearchResponseDto posts = postService.getPostByContent(requestDto);
				PostSearchDto postSearchDto = posts.getPosts().get(0);
				//then
				assertThat(posts).extracting(PostSearchResponseDto::getPageSize).isEqualTo(20);
				assertThat(posts).extracting(PostSearchResponseDto::getPageNow).isEqualTo(requestDto.getPageNumber());
				assertThat(posts).extracting(PostSearchResponseDto::getElementsSize).isEqualTo(2);
				assertThat(posts.getPosts()).size().isEqualTo(2);
				assertThat(postSearchDto).extracting("title").isEqualTo(title);
				assertThat(postSearchDto).extracting("author").isEqualTo(username);

			}
		}

		@DisplayName("postId 로 조회")
		@Nested
		class LookupPostByPostId {
			@DisplayName("성공케이스")
			@Nested
			class SuccessfulCase {
				@DisplayName("유저가 좋아요를 눌렀을 때")
				@Test
				void userLikePost() {
					//given
					Long fakeId = 1L;
					String title = "testTitle";
					String content = "testContent";
					String thumbnail = "testThumbnail";
					String username = "tester";
					String path = "path";

					User user = User.builder()
						.username(username)
						.email("test@gamil.com")
						.password(passwordEncoder.encode("1234"))
						.build();

					Post testPost = Post.builder()
						.user(user)
						.title(title)
						.content(content)
						.thumbnailName(thumbnail)
						.storedThumbnailPath(path)
						.viewCount(10L)
						.build();
					LikeSearchDto likeSearchDto = LikeSearchDto.builder()
						.postId(1L)
						.likeId(1L)
						.userID(1L)
						.build();
					//Mock
					given(postDataRepository.findById(fakeId))
						.willReturn(Optional.ofNullable(testPost));
					given(likeDataRepository.findByUserIdAndPostId(fakeId, fakeId))
						.willReturn(Optional.ofNullable(likeSearchDto));
					//when
					PostSearchDto postDto = postService.getPostDtoById(fakeId, fakeId);

					//then
					assertThat(postDto).extracting("title").isEqualTo(title);
					assertThat(postDto).extracting("author").isEqualTo(username);
					assertThat(postDto).extracting("isLiked").isEqualTo(true);

				}

				@DisplayName("유저가 좋아요를 눌렀을 때")
				@Test
				void userNotLikePost() {
					//given
					Long fakeId = 1L;
					String title = "testTitle";
					String content = "testContent";
					String thumbnail = "testThumbnail";
					String username = "tester";
					String path = "path";

					User user = User.builder()
						.username(username)
						.email("test@gamil.com")
						.password(passwordEncoder.encode("1234"))
						.build();

					Post testPost = Post.builder()
						.user(user)
						.title(title)
						.content(content)
						.thumbnailName(thumbnail)
						.storedThumbnailPath(path)
						.viewCount(10L)
						.build();
					LikeSearchDto likeSearchDto = LikeSearchDto.builder()
						.postId(1L)
						.likeId(1L)
						.userID(1L)
						.build();
					//Mock
					given(postDataRepository.findById(fakeId))
						.willReturn(Optional.ofNullable(testPost));
					given(likeDataRepository.findByUserIdAndPostId(fakeId, fakeId))
						.willReturn(Optional.empty());
					//when
					PostSearchDto postDto = postService.getPostDtoById(fakeId, fakeId);

					//then
					assertThat(postDto).extracting("title").isEqualTo(title);
					assertThat(postDto).extracting("author").isEqualTo(username);
					assertThat(postDto).extracting("isLiked").isEqualTo(false);

				}
			}

			@DisplayName("실패케이스")
			@Nested
			class FailureCase {
				@DisplayName("해당 포스트 미존재")
				@Test
				void postNotExist() {
					//given
					Long fakeId = 1L;

					//Mock
					given(postDataRepository.findById(fakeId))
						.willReturn(Optional.empty());
					//when
					//then
					assertThatThrownBy(() -> {
						postService.getPostDtoById(fakeId, fakeId);
					}).isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining(POST_NOT_EXIST);
				}

				@DisplayName("UserID 미존재")
				@Test
				void userNotExist() {
					//given
					Long fakeId = 1L;
					String title = "testTitle";
					String content = "testContent";
					String thumbnail = "testThumbnail";
					String username = "tester";
					String path = "path";

					User user = User.builder()
						.username(username)
						.email("test@gamil.com")
						.password(passwordEncoder.encode("1234"))
						.build();

					Post testPost = Post.builder()
						.user(user)
						.title(title)
						.content(content)
						.thumbnailName(thumbnail)
						.storedThumbnailPath(path)
						.viewCount(10L)
						.build();
					//Mock
					given(postDataRepository.findById(fakeId))
						.willReturn(Optional.ofNullable(testPost));
					//when
					PostSearchDto postDto = postService.getPostDtoById(fakeId, null);

					//then
					assertThat(postDto).extracting("title").isEqualTo(title);
					assertThat(postDto).extracting("author").isEqualTo(username);
					assertThat(postDto).extracting("isLiked").isEqualTo(false);
				}
			}

		}

		@DisplayName("최신 포스트 조회")
		@Nested
		class LookupLatestPosts {
			@DisplayName("성공케이스")
			@Test
			void successCase() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.id(fakeUserId)
					.email("test@gmail.com")
					.username("tester")
					.password(passwordEncoder.encode("1234"))
					.build();

				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Post postA = Post.builder()
					.id(fakePostAId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.thumbnailName("thumbnail")
					.storedThumbnailPath("path")
					.user(user)
					.build();
				Post postB = Post.builder()
					.id(fakePostBId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.user(user)
					.build();

				Integer requestPageNumber = 0;
				PageRequest paging = PageRequest.of(requestPageNumber, 20);

				List<Long> postIds = new ArrayList<>();
				postIds.add(fakePostAId);
				postIds.add(fakePostBId);
				List<Post> postList = new ArrayList<>();
				postList.add(postA);
				postList.add(postB);

				//Mock
				given(postDataRepository.findLatestPostIdsByCreatedAtDesc(paging))
					.willReturn(postIds);
				given(postDataRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(postIds))
					.willReturn(postList);

				//when
				PostSearchResponseDto latestPosts = postService.getLatestPosts(requestPageNumber);
				//then
				assertThat(latestPosts).extracting(PostSearchResponseDto::getPageSize).isEqualTo(20);
				assertThat(latestPosts).extracting(PostSearchResponseDto::getPageNow).isEqualTo(requestPageNumber);
				assertThat(latestPosts).extracting(PostSearchResponseDto::getElementsSize).isEqualTo(2);
				assertThat(latestPosts.getPosts()).size().isEqualTo(2);
			}
		}

		@DisplayName("인기 포스트 조회")
		@Nested
		class LookupTrendPosts {
			@DisplayName("성공케이스")
			@Test
			void successCase() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.id(fakeUserId)
					.email("test@gmail.com")
					.username("tester")
					.password(passwordEncoder.encode("1234"))
					.build();

				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Post postA = Post.builder()
					.id(fakePostAId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.user(user)
					.build();
				Post postB = Post.builder()
					.id(fakePostBId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.user(user)
					.build();

				Integer requestPageNumber = 0;
				PageRequest paging = PageRequest.of(requestPageNumber, 20);

				List<Long> postIds = new ArrayList<>();
				postIds.add(fakePostAId);
				postIds.add(fakePostBId);
				List<Post> postList = new ArrayList<>();
				postList.add(postA);
				postList.add(postB);

				//Mock
				given(postDataRepository.findPopularityPostIdsByViewCountAtDesc(paging))
					.willReturn(postIds);
				given(postDataRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(postIds))
					.willReturn(postList);

				//when
				PostSearchResponseDto latestPosts = postService.getPopularityPosts(requestPageNumber);
				//then
				assertThat(latestPosts).extracting(PostSearchResponseDto::getPageSize).isEqualTo(20);
				assertThat(latestPosts).extracting(PostSearchResponseDto::getPageNow).isEqualTo(requestPageNumber);
				assertThat(latestPosts).extracting(PostSearchResponseDto::getElementsSize).isEqualTo(2);
				assertThat(latestPosts.getPosts()).size().isEqualTo(2);
			}
		}
	}

	@DisplayName("포스트 삭제")
	@Nested
	class RemovePost {
		@DisplayName("성공 케이스")
		@Test
		void successfulCase() {
			//given
			Long fakeUserId = 1L;
			Long fakePostId = 1L;
			User user = User.builder()
				.id(fakeUserId)
				.email("test@gmail.com")
				.username("tester")
				.password(passwordEncoder.encode("1234"))
				.build();

			Post returnPost = Post.builder()
				.id(fakePostId)
				.user(User.builder().username("tester").email("test@test.com").password("password").build())
				.title("testTitle")
				.content("testContent")
				.user(user)
				.build();
			//Mock
			given(postDataRepository.findById(fakePostId))
				.willReturn(Optional.ofNullable(returnPost));
			//when
			postService.removePost(fakePostId, fakeUserId);
		}

		@DisplayName("실패 케이스 - post 미존재")
		@Test
		void failureCase() {
			//given
			Long fakeUserId = 1L;
			Long fakePostId = 1L;

			//Mock
			given(postDataRepository.findById(fakePostId))
				.willReturn(Optional.empty());
			//when
			//then
			assertThatThrownBy(() -> {
				postService.removePost(fakePostId, fakeUserId);
			}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(POST_NOT_EXIST);

		}

	}

	@DisplayName("포스트 수정")
	@Nested
	class EditPost {

		@DisplayName("성공케이스")
		@Nested
		class SuccessCase {
			@DisplayName("데이터 수정")
			@Test
			void editEveryData() {
				//given
				Long fakePostId = 1L;
				Long fakeUserId = 1L;
				String editTitle = "editedTitle";
				String editContent = "editedContent";

				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.build();

				User user = User.builder()
					.id(fakeUserId)
					.username("tester")
					.email("test@test.com")
					.password("password")
					.build();
				Post returnPost = Post.builder()
					.id(fakePostId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();

				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.ofNullable(returnPost));
				//when
				postService.editPostContent(fakePostId, fakeUserId, editDto);

			}
		}

		@DisplayName("실패케이스")
		@Nested
		class FailCase {

			@DisplayName("포스트 미존재")
			@Test
			void undefinedPost() {
				//given
				Long fakePostId = 1L;
				Long fakeUserId = 1L;
				String editTitle = "editedTitle";
				String editContent = "editedContent";
				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.build();
				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.empty());
				//when
				//then
				assertThatThrownBy(() -> {
					postService.editPostContent(fakePostId, fakeUserId, editDto);
				}).isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(POST_NOT_EXIST);

			}

			@DisplayName("작성자와 편집자 미일치")
			@Test
			void unMatchingWriterAndEditor() {
				//given
				Long fakePostId = 1L;
				Long fakeUserId = 1L;
				Long illegalEditorId = fakeUserId + 1L;
				String editTitle = "editedTitle";
				String editContent = "editedContent";

				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.build();

				User writer = User.builder()
					.id(fakeUserId)
					.username("tester")
					.email("test@test.com")
					.password("password")
					.build();

				Post returnPost = Post.builder()
					.user(writer)
					.title("testTitle")
					.content("testContent")
					.build();

				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.ofNullable(returnPost));
				//when
				//then
				assertThatThrownBy(() -> {
					postService.editPostContent(fakePostId, illegalEditorId, editDto);
				}).isInstanceOf(IllegalStateException.class)
					.hasMessageContaining(WRITER_USER_NOT_MATCH);

			}

		}
	}

	@DisplayName("포스트 좋아요")
	@Nested
	class LikePost {
		@DisplayName("성공 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("좋아요")
			@Test
			void likeTest() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.id(fakeUserId)
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();

				Long fakePostId = 1L;
				Post post = Post.builder()
					.id(fakePostId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				//mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.of(post));
				given(userDataRepository.findById(fakeUserId))
					.willReturn(Optional.of(user));
				given(likeDataRepository.findByUserIdAndPostId(fakeUserId, fakePostId))
					.willReturn(Optional.empty());
				//when
				postService.likeStatusChange(fakePostId, fakeUserId);
			}

			@DisplayName("좋아요 취소")
			@Test
			void cancelLikeTest() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.id(fakeUserId)
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();

				Long fakePostId = 1L;
				Post post = Post.builder()
					.id(fakePostId)
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();

				LikeSearchDto dto = LikeSearchDto.builder()
					.postId(post.getId())
					.userID(user.getId())
					.likeId(1L)
					.build();
				//mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.of(post));
				given(userDataRepository.findById(fakeUserId))
					.willReturn(Optional.of(user));
				given(likeDataRepository.findByUserIdAndPostId(fakeUserId, fakePostId))
					.willReturn(Optional.of(dto));
				//when
				postService.likeStatusChange(fakePostId, fakeUserId);
			}
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailCase {
			@DisplayName("회원 미존재")
			@Test
			void noExistUser() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();

				Long fakePostId = 1L;
				Post post = Post.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				//mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.of(post));
				given(userDataRepository.findById(fakeUserId))
					.willReturn(Optional.empty());
				//when
				assertThatThrownBy(() -> {
					postService.likeStatusChange(fakePostId, fakeUserId);
				})
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(USER_NOT_EXIST);

			}

			@DisplayName("포스트 미존재")
			@Test
			void noExistPost() {
				//given
				Long fakeUserId = 1L;
				User user = User.builder()
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();

				Long fakePostId = 1L;
				Post post = Post.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				//mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.empty());
				//when
				assertThatThrownBy(() -> {
					postService.likeStatusChange(fakePostId, fakeUserId);
				})
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(POST_NOT_EXIST);

			}
		}

	}

	@DisplayName("썸네일 조회")
	@Nested
	class ThumbnailLookup {

		@DisplayName("성공 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("썸네일 존재 케이스")
			@Test
			void thumbnailExist() throws MalformedURLException {
				//given
				String imageName = "thumbName";
				User user = User.builder()
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();
				//mock
				given(postDataRepository.findThumbnailPathWithName(imageName))
					.willReturn("testImage.png");
				//when
				File file = postService.readImageFile(imageName);

				//then
				assertThat(file).exists();
			}
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailureCase {
			@DisplayName("썸네일 존재 케이스")
			@Test
			void thumbnailExist() {
				//given
				String imageName = "thumbName";
				User user = User.builder()
					.username("tester")
					.email("test@gmail.com")
					.password("1234")
					.build();
				Post post = Post.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				//when
				//then
				assertThatThrownBy(() -> {
					postService.readImageFile(imageName);
				}).isInstanceOf(IllegalArgumentException.class);
			}
		}

	}
}