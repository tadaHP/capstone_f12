package io.f12.notionlinkedblog.service.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.PostExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostCreateDto;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	PostService postService;

	@Mock
	PostDataRepository postDataRepository;

	@Mock
	UserDataRepository userDataRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("포스트 생성")
	@Nested
	class createPost {

		@DisplayName("성공케이스")
		@Nested
		class successCase {
			@DisplayName("모든 데이터 존재")
			@Test
			void haveEveryData() {
				//given
				Long fakeId = 1L;
				User user = User.builder()
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";

				PostCreateDto postDto = PostCreateDto.builder()
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.build();

				Post returnPost = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.build();

				ReflectionTestUtils.setField(user, "id", fakeId);
				//Mock
				given(userDataRepository.findById(fakeId))
					.willReturn(Optional.of(user));
				given(postDataRepository.save(any(Post.class)))
					.willReturn(returnPost);

				//when
				PostSearchDto createdPost = postService.createPost(fakeId, postDto);
				//then
				assertThat(createdPost).extracting("title").isEqualTo(title);
				assertThat(createdPost).extracting("content").isEqualTo(content);
				assertThat(createdPost).extracting("thumbnail").isEqualTo(thumbnail);
			}

			@DisplayName("섬네일 제외")
			@Test
			void withoutThumbnail() {
				//given
				Long fakeId = 1L;
				User user = User.builder()
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				String title = "testTitle";
				String content = "testContent";
				PostCreateDto postDto = PostCreateDto.builder()
					.title(title)
					.content(content)
					.build();

				Post returnPost = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.build();

				ReflectionTestUtils.setField(user, "id", fakeId);
				//Mock
				given(userDataRepository.findById(fakeId))
					.willReturn(Optional.of(user));
				given(postDataRepository.save(any(Post.class)))
					.willReturn(returnPost);

				//when
				PostSearchDto createdPost = postService.createPost(fakeId, postDto);
				//then
				assertThat(createdPost).extracting("title").isEqualTo(title);
				assertThat(createdPost).extracting("content").isEqualTo(content);
				assertThat(createdPost).extracting("thumbnail").isNull();
			}

		}

		@DisplayName("실패 케이스")
		@Nested
		class failureCase {
			@DisplayName("USER 미존재")
			@Test
			void undefinedUser() {
				//given
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				PostCreateDto postDto = PostCreateDto.builder()
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.build();
				Long fakeId = 1L;

				//Mock
				given(userDataRepository.findById(fakeId))
					.willReturn(null);

				//when
				//then
				assertThatThrownBy(() -> {
					postService.createPost(fakeId, postDto);
				}).isInstanceOf(NullPointerException.class);

			}

		}

	}

	@DisplayName("포스트 조회")
	@Nested
	class findPost {

		@DisplayName("title 로 조회")
		@Nested
		class findPostByTitle {
			@DisplayName("성공케이스")
			@Test
			void successCase() {
				//given
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String username = "tester";

				User user = User.builder()
					.username(username)
					.email("test@gamil.com")
					.password(passwordEncoder.encode("1234"))
					.build();

				Post post = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.viewCount(10L)
					.build();

				List<Post> list = new ArrayList<>();
				list.add(post);

				//Mock
				given(postDataRepository.findByTitle(title))
					.willReturn(list);
				//when
				List<PostSearchDto> posts = postService.getPostsByTitle(title);
				PostSearchDto postSearchDto = posts.get(0);
				//then
				assertThat(posts).size().isEqualTo(1);
				assertThat(postSearchDto).extracting("title").isEqualTo(title);
				assertThat(postSearchDto).extracting("username").isEqualTo(username);
			}
		}

		@DisplayName("content 로 조회")
		@Nested
		class findPostByContent {
			@DisplayName("성공케이스")
			@Test
			void successCase() {

				//given
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String username = "tester";

				User user = User.builder()
					.username(username)
					.email("test@gamil.com")
					.password(passwordEncoder.encode("1234"))
					.build();

				Post post = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.viewCount(10L)
					.build();

				List<Post> list = new ArrayList<>();
				list.add(post);

				//Mock
				given(postDataRepository.findByContent(content))
					.willReturn(list);
				//when
				List<PostSearchDto> posts = postService.getPostByContent(content);
				PostSearchDto postSearchDto = posts.get(0);
				//then
				assertThat(posts).size().isEqualTo(1);
				assertThat(postSearchDto).extracting("title").isEqualTo(title);
				assertThat(postSearchDto).extracting("username").isEqualTo(username);

			}
		}

		@DisplayName("postId 로 조회")
		@Nested
		class findPostByPostId {
			@DisplayName("성공케이스")
			@Test
			void successCase() {
				//given
				Long fakeId = 1L;
				String title = "testTitle";
				String content = "testContent";
				String thumbnail = "testThumbnail";
				String username = "tester";

				User user = User.builder()
					.username(username)
					.email("test@gamil.com")
					.password(passwordEncoder.encode("1234"))
					.build();

				Post testPost = Post.builder()
					.user(user)
					.title(title)
					.content(content)
					.thumbnail(thumbnail)
					.viewCount(10L)
					.build();
				//Mock
				given(postDataRepository.findById(fakeId))
					.willReturn(Optional.ofNullable(testPost));
				//when
				PostSearchDto postDto = postService.getPostDtoById(fakeId);

				//then
				assertThat(postDto).extracting("title").isEqualTo(title);
				assertThat(postDto).extracting("username").isEqualTo(username);

			}

			@DisplayName("실패케이스 - 해당 포스트 미존재")
			@Test
			void failureCase() {
				//given
				Long fakeId = 1L;

				//Mock
				given(postDataRepository.findById(fakeId))
					.willReturn(Optional.empty());
				//when
				//then
				assertThatThrownBy(() -> {
					postService.getPostDtoById(fakeId);
				}).isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(POST_NOT_EXIST);
			}
		}
	}

	@DisplayName("포스트 삭제")
	@Nested
	class removePost {
		@DisplayName("성공 케이스")
		@Test
		void successfulCase() {
			//given
			Long fakeUserId = 1L;
			Long fakePostId = 1L;

			Post returnPost = Post.builder()
				.user(User.builder().username("tester").email("test@test.com").password("password").build())
				.title("testTitle")
				.content("testContent")
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
	class editPost {

		@DisplayName("성공케이스")
		@Nested
		class successfulCase {
			@DisplayName("데이터 수정")
			@Test
			void editEveryData() {
				//given
				Long fakePostId = 1L;
				Long fakeUserId = 1L;
				String editTitle = "editedTitle";
				String editContent = "editedContent";
				String editThumbnail = "editedThumbnail";

				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.thumbnail(editThumbnail)
					.build();

				User user = User.builder()
					.username("tester")
					.email("test@test.com")
					.password("password")
					.build();
				ReflectionTestUtils.setField(user, "id", fakeUserId);
				Post returnPost = Post.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.thumbnail("tentThumbnail")
					.build();

				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.ofNullable(returnPost));
				//when
				postService.editPost(fakePostId, fakeUserId, editDto);

			}
		}

		@DisplayName("실패케이스")
		@Nested
		class failureCase {

			@DisplayName("포스트 미존재")
			@Test
			void undefinedPost() {
				//given
				Long fakePostId = 1L;
				Long fakeUserId = 1L;
				String editTitle = "editedTitle";
				String editContent = "editedContent";
				String editThumbnail = "editedThumbnail";
				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.thumbnail(editThumbnail)
					.build();
				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.empty());
				//when
				//then
				assertThatThrownBy(() -> {
					postService.editPost(fakePostId, fakeUserId, editDto);
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
				String editThumbnail = "editedThumbnail";

				PostEditDto editDto = PostEditDto.builder()
					.title(editTitle)
					.content(editContent)
					.thumbnail(editThumbnail)
					.build();

				User writer = User.builder()
					.username("tester")
					.email("test@test.com")
					.password("password")
					.build();

				ReflectionTestUtils.setField(writer, "id", fakeUserId);

				Post returnPost = Post.builder()
					.user(writer)
					.title("testTitle")
					.content("testContent")
					.thumbnail("tentThumbnail")
					.build();

				//Mock
				given(postDataRepository.findById(fakePostId))
					.willReturn(Optional.ofNullable(returnPost));
				//when
				//then
				assertThatThrownBy(() -> {
					postService.editPost(fakePostId, illegalEditorId, editDto);
				}).isInstanceOf(IllegalStateException.class)
					.hasMessageContaining(WRITER_USER_NOT_MATCH);

			}

		}
	}

}