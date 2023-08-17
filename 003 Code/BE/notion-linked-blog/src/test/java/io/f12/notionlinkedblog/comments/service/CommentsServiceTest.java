package io.f12.notionlinkedblog.comments.service;

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

import io.f12.notionlinkedblog.comments.api.response.CommentEditDto;
import io.f12.notionlinkedblog.comments.api.response.ParentsCommentDto;
import io.f12.notionlinkedblog.comments.domain.dto.CreateCommentDto;
import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import io.f12.notionlinkedblog.comments.service.port.CommentsRepository;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommentsServiceTest {
	@InjectMocks
	CommentsServiceImpl commentsService;
	@Mock
	CommentsRepository commentsRepository;
	@Mock
	PostRepository postRepository;
	@Mock
	UserRepository userRepository;
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
				List<CommentsEntity> returnComments = new ArrayList<>();
				UserEntity user = UserEntity.builder()
					.username("tester")
					.email("test@test.com")
					.password(passwordEncoder.encode("1234"))
					.build();
				PostEntity post = PostEntity.builder()
					.user(user)
					.title("testTitle")
					.content("testContent")
					.build();
				String content1 = "testComment1";
				String content2 = "testComment2";

				CommentsEntity parent = CommentsEntity.builder()
					.content(content1)
					.user(user)
					.post(post)
					.depth(0)
					.build();

				CommentsEntity child = CommentsEntity.builder()
					.content(content2)
					.user(user)
					.parent(parent)
					.depth(1)
					.post(post)
					.build();

				returnComments.add(parent);
				returnComments.add(child);
				//Mock
				given(commentsRepository.findByPostId(fakePostId))
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
				List<CommentsEntity> returnComments = new ArrayList<>();
				UserEntity user = UserEntity.builder()
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();
				//Mock
				given(commentsRepository.findByPostId(fakePostId))
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
			UserEntity user = UserEntity.builder()
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			PostEntity post = PostEntity.builder()
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			CommentsEntity comments = CommentsEntity.builder()
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
			given(postRepository.findById(fakePostId))
				.willReturn(Optional.ofNullable(post));
			given(userRepository.findById(fakeUserId))
				.willReturn(Optional.ofNullable(user));
			given(commentsRepository.save(any(CommentsEntity.class)))
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
			UserEntity user = UserEntity.builder()
				.id(fakeUserId)
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			PostEntity post = PostEntity.builder()
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			String editContent = "editComment";
			CommentsEntity comments = CommentsEntity.builder()
				.content(content)
				.user(user)
				.depth(0)
				.post(post)
				.build();
			given(commentsRepository.findById(fakeCommentId))
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
			UserEntity user = UserEntity.builder()
				.id(fakeUserId)
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();
			PostEntity post = PostEntity.builder()
				.id(fakePostId)
				.user(user)
				.title("testTitle")
				.content("testContent")
				.build();
			String content = "testComment1";
			CommentsEntity comments = CommentsEntity.builder()
				.id(fakeCommentId)
				.content(content)
				.user(user)
				.depth(0)
				.post(post)
				.build();
			//Mock
			given(commentsRepository.findById(fakeCommentId))
				.willReturn(Optional.ofNullable(comments));
			//when
			commentsService.removeComment(fakeCommentId, fakeUserId);
			//then
		}
	}

}