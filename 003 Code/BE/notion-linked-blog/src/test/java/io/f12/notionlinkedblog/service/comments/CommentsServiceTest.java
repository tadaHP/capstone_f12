package io.f12.notionlinkedblog.service.comments;

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

import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.comments.dto.CommentSearchDto;
import io.f12.notionlinkedblog.domain.comments.dto.CreateCommentDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.comments.CommentsDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@ExtendWith(MockitoExtension.class)
class CommentsServiceTest {
	@InjectMocks
	CommentsService commentsService;
	@Mock
	CommentsDataRepository commentsDataRepository;
	@Mock
	PostDataRepository postDataRepository;
	@Mock
	UserDataRepository userDataRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("댓글 조회")
	@Nested
	class LookupComments {
		@DisplayName("성공 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("데이터 존재")
			@Test
			void dataExist() {
				//given
				Long fakePostId = 1L;
				List<Comments> returnComments = new ArrayList<>();
				User user = User.builder()
					.username("tester")
					.email("test@test.com")
					.password(passwordEncoder.encode("1234"))
					.build();
				Post post = Post.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				String content1 = "testComment1";
				String content2 = "testComment2";
				returnComments.add(Comments.builder()
					.content(content1)
					.user(user)
					.post(post)
					.depth(0)
					.build());
				returnComments.add(Comments.builder()
					.content(content2)
					.user(user)
					.depth(0)
					.post(post)
					.build());
				//Mock
				given(commentsDataRepository.findByPostId(fakePostId))
					.willReturn(returnComments);
				//when
				List<CommentSearchDto> comments = commentsService.getCommentsByPostId(fakePostId);
				CommentSearchDto comment1 = comments.get(0);
				CommentSearchDto comment2 = comments.get(1);
				//then
				assertThat(comments).size().isEqualTo(2);
				assertThat(comment1).extracting("comments").isEqualTo(content1);
				assertThat(comment2).extracting("comments").isEqualTo(content2);

			}

			@DisplayName("데이터 미존재")
			@Test
			void noData() {
				//given
				Long fakePostId = 1L;
				List<Comments> returnComments = new ArrayList<>();
				User user = User.builder()
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();
				//Mock
				given(commentsDataRepository.findByPostId(fakePostId))
					.willReturn(returnComments);
				//when
				List<CommentSearchDto> comments = commentsService.getCommentsByPostId(fakePostId);
				//then
				assertThat(comments).isEmpty();
			}
		}

	}

	@DisplayName("댓글 생성")
	@Nested
	class CreateComments {
		@DisplayName("성공 케이스")
		@Test
		void successCase() {
			//given
			Long fakePostId = 1L;
			Long fakeUserId = 1L;
			User user = User.builder()
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			Post post = Post.builder()
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			Comments comments = Comments.builder()
				.content(content)
				.user(user)
				.post(post)
				.depth(0)
				.build();
			CreateCommentDto createCommentDto = CreateCommentDto.builder()
				.comment("testComment")
				.depth(0)
				.build();
			//Mock
			given(postDataRepository.findById(fakePostId))
				.willReturn(Optional.ofNullable(post));
			given(userDataRepository.findById(fakeUserId))
				.willReturn(Optional.ofNullable(user));
			given(commentsDataRepository.save(any(Comments.class)))
				.willReturn(comments);
			//when
			CommentSearchDto commentDto = commentsService.createComments(fakePostId, fakeUserId, createCommentDto);
			//then
			assertThat(commentDto).extracting("comments").isEqualTo(content);
			assertThat(commentDto).extracting("username").isEqualTo(user.getUsername());
		}

	}

	@DisplayName("댓글 수정")
	@Nested
	class EditComments {
		@DisplayName("성공 케이스")
		@Test
		void successCase() {
			//given
			Long fakeCommentId = 1L;
			Long fakeUserId = 1L;
			User user = User.builder()
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			Post post = Post.builder()
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			String editContent = "editComment";
			Comments comments = Comments.builder()
				.content(content)
				.user(user)
				.depth(0)
				.post(post)
				.build();
			//Mock
			ReflectionTestUtils.setField(user, "id", fakeUserId);
			given(commentsDataRepository.findById(fakeCommentId))
				.willReturn(Optional.ofNullable(comments));
			//when
			CommentSearchDto editedComment = commentsService.editComment(fakeCommentId, fakeUserId, editContent);
			//then
			assertThat(editedComment).extracting("comments").isEqualTo(editContent);
		}
	}

	@DisplayName("댓글 삭제")
	@Nested
	class RemoveComments {
		@DisplayName("성공 케이스")
		@Test
		void successCase() {
			//given
			Long fakeCommentId = 1L;
			Long fakeUserId = 1L;
			Long fakePostId = 1L;
			User user = User.builder()
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			Post post = Post.builder()
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			Comments comments = Comments.builder()
				.content(content)
				.user(user)
				.depth(0)
				.post(post)
				.build();
			ReflectionTestUtils.setField(user, "id", fakeUserId);
			ReflectionTestUtils.setField(post, "id", fakePostId);
			ReflectionTestUtils.setField(comments, "id", fakeCommentId);
			//Mock
			given(commentsDataRepository.findById(fakeCommentId))
				.willReturn(Optional.ofNullable(comments));
			//when
			commentsService.removeComment(fakeCommentId, fakeUserId);
			//then
		}
	}

}