package io.f12.notionlinkedblog.api.comments;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.comments.dto.CreateCommentDto;
import io.f12.notionlinkedblog.domain.comments.dto.EditCommentDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.comments.CommentsDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CommentsApiControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private PostDataRepository postDataRepository;
	@Autowired
	private CommentsDataRepository commentsDataRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private User testUser;
	private Post testPost;
	private Comments testComment;

	@BeforeEach
	void init() {
		testUser = userDataRepository.save(User.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234"))
			.build()
		);
		testPost = postDataRepository.save(Post.builder()
			.user(testUser)
			.title("testTitle")
			.isPublic(true)
			.content("testContent").build());
		testComment = commentsDataRepository.save(Comments.builder()
			.user(testUser)
			.post(testPost)
			.depth(0)
			.content("testComments").build());
		userDataRepository.save(User.builder()
			.email("test2@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234"))
			.build()
		);
	}

	@AfterEach
	void clear() {
		commentsDataRepository.deleteAll();
		postDataRepository.deleteAll();
		userDataRepository.deleteAll();
	}

	@DisplayName("댓글 조회")
	@Nested
	class CommentsLookup {
		@DisplayName("성공케이스")
		@Test
		void successfulCase() throws Exception {
			//given
			final String url = Endpoint.Api.POST + "/" + testPost.getId() + "/comments";
			//mock
			//when
			ResultActions resultActions = mockMvc.perform(
				get(url)
			);
			//then
			resultActions.andExpect(status().isOk());
		}
	}

	@DisplayName("댓글 생성")
	@Nested
	class CommentCreate {
		@DisplayName("성공케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			CreateCommentDto createDto = CreateCommentDto.builder()
				.comment("testComment")
				.depth(0)
				.build();
			final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
			//mock

			//when
			ResultActions resultActions = mockMvc.perform(
				post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(createDto))
			);
			//then
			resultActions.andExpect(status().isCreated());
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailCase {
			@DisplayName("생성 데이터 없음")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void noCreateData() throws Exception {
				//given
				final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
				//mock
				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)

				);
				//then
				resultActions.andExpect(status().isBadRequest());
			}
		}
	}

	@DisplayName("댓글 수정")
	@Nested
	class CommentEdit {
		@DisplayName("성공케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successfulCase() throws Exception {
			//given
			EditCommentDto editComment = EditCommentDto.builder()
				.comment("editComment")
				.build();
			final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
			//mock

			//when
			ResultActions resultActions = mockMvc.perform(
				put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(editComment))
			);
			//then
			resultActions.andExpect(status().isOk());
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailCase {
			@DisplayName("생성 데이터 없음")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void noCreateData() throws Exception {
				//given
				final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
				//mock
				//when
				ResultActions resultActions = mockMvc.perform(
					put(url)
				);
				//then
				resultActions.andExpect(status().isBadRequest());
			}
		}

		@DisplayName("작성자 변경자 불일치")
		@WithUserDetails(value = "test2@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void writerEditorNotMatch() throws Exception {
			//given
			final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
			//mock
			//when
			ResultActions resultActions = mockMvc.perform(
				put(url)
			);
			//then
			resultActions.andExpect(status().isBadRequest());
		}
	}

	@DisplayName("댓글 삭제")
	@Nested
	class RemoveComment {
		@DisplayName("성공케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successfulCase() throws Exception {
			//given
			final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
			//mock
			//when
			ResultActions resultActions = mockMvc.perform(
				delete(url)
			);
			//then
			resultActions.andExpect(status().isNoContent());
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailCase {
			@DisplayName("작성자 변경자 불일치")
			@WithUserDetails(value = "test2@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void writerEditorNotMatch() throws Exception {
				//given
				final String url = Endpoint.Api.COMMENTS + "/" + testComment.getId();
				//mock
				//when
				ResultActions resultActions = mockMvc.perform(
					delete(url)
				);
				//then
				resultActions.andExpect(status().isUnauthorized());
			}
		}

	}

}