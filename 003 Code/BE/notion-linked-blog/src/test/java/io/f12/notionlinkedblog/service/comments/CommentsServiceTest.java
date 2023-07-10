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

import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.comments.dto.CreateCommentDto;
import io.f12.notionlinkedblog.domain.comments.dto.response.CommentEditDto;
import io.f12.notionlinkedblog.domain.comments.dto.response.ParentsCommentDto;
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

				Comments parent = Comments.builder()
					.content(content1)
					.user(user)
					.post(post)
					.depth(0)
					.build();

				Comments child = Comments.builder()
					.content(content2)
					.user(user)
					.parent(parent)
					.depth(1)
					.post(post)
					.build();

				returnComments.add(parent);
				returnComments.add(child);
				//Mock
				given(commentsDataRepository.findByPostId(fakePostId))
					.willReturn(returnComments);
				//when
				List<ParentsCommentDto> commentsDto = commentsService.getCommentsByPostId(fakePostId);
				ParentsCommentDto parentsCommentDto = commentsDto.get(0);

				//then
				assertThat(parentsCommentDto.getChildren()).size().isEqualTo(1);
				assertThat(parentsCommentDto.getChildren().get(0).getComment()).isEqualTo(content2);
				assertThat(parentsCommentDto.getComment()).isEqualTo(content1);

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
				List<ParentsCommentDto> commentsDto = commentsService.getCommentsByPostId(fakePostId);
				//then
				assertThat(commentsDto).isEmpty();
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
			CommentEditDto commentDto = commentsService.createComments(fakePostId, fakeUserId, createCommentDto);
			//then
			assertThat(commentDto.getComment()).isEqualTo(content);
			assertThat(commentDto.getAuthor()).isEqualTo(user.getUsername());
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
				.id(fakeUserId)
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
			given(commentsDataRepository.findById(fakeCommentId))
				.willReturn(Optional.ofNullable(comments));
			//when
			CommentEditDto editedComment = commentsService.editComment(fakeCommentId, fakeUserId, editContent);
			//then
			assertThat(editedComment.getComment()).isEqualTo(editContent);
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
				.id(fakeUserId)
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			Post post = Post.builder()
				.id(fakePostId)
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			Comments comments = Comments.builder()
				.id(fakeCommentId)
				.content(content)
				.user(user)
				.depth(0)
				.post(post)
				.build();
			//Mock
			given(commentsDataRepository.findById(fakeCommentId))
				.willReturn(Optional.ofNullable(comments));
			//when
			commentsService.removeComment(fakeCommentId, fakeUserId);
			//then
		}
	}

}