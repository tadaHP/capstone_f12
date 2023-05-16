package io.f12.notionlinkedblog.api.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostCreateDto;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PostApiControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private PostDataRepository postDataRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private User testUser;
	private Post testPost;

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
			.content("testContent").build());
	}

	@AfterEach
	void clear() {
		postDataRepository.deleteAll();
		userDataRepository.deleteAll();
	}

	@DisplayName("포스트 생성")
	@Nested
	class createPost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			final String url = Endpoint.Api.POST;
			UserSearchDto user = userDataRepository.findUserById(testUser.getId())
				.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

			PostCreateDto body = PostCreateDto.builder()
				.title("testTitle")
				.content("testContent")
				.thumbnail("testThumbnail")
				.build();
			String requestBody = objectMapper.writeValueAsString(body);
			//mock
			MockHttpSession mockHttpSession = new MockHttpSession();
			mockHttpSession.setAttribute(mockHttpSession.getId(), user);

			//when
			ResultActions resultActions = mockMvc.perform(
				post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.session(mockHttpSession)
					.content(requestBody)
			);
			//then
			resultActions.andExpect(status().isCreated());
		}

	}

	@DisplayName("포스트 조회")
	@Nested
	class getPost {

		@DisplayName("단건 조회")
		@Nested
		class singleLookup {
			@DisplayName("포스트 ID로 조회")
			@Nested
			class getPostById {
				@DisplayName("성공 케이스")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/" + testPost.getId();
					//when
					ResultActions resultActions = mockMvc.perform(
						get(url)
					);
					//then
					resultActions.andExpect(status().isOk());

				}
			}
		}

		@DisplayName("다건 조회")
		@Nested
		class multiLookup {
			@DisplayName("포스트 title 로 조회")
			@Nested
			class getPostsByTitle {
				@DisplayName("성공 케이스")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/title";
					SearchRequestDto requestDto = SearchRequestDto.builder()
						.param("content")
						.pageNumber(0)
						.build();
					//when
					ResultActions resultActions = mockMvc.perform(
						get(url)
							.content(objectMapper.writeValueAsString(requestDto))
							.contentType(MediaType.APPLICATION_JSON)
					);
					//then
					resultActions.andExpect(status().isOk());
				}

				@DisplayName("실패 케이스")
				@Nested
				class failureCase {
					@DisplayName("파라미터 미존재")
					@Test
					void noParam() throws Exception {
						//given
						String url = Endpoint.Api.POST + "/title";
						//when
						ResultActions resultActions = mockMvc.perform(
							get(url)
						);
						//then
						resultActions.andExpect(status().isBadRequest());
					}
				}
			}

			@DisplayName("포스트 content 로 조회")
			@Nested
			class getPostsByContent {
				@DisplayName("성공 케이스")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/content";
					SearchRequestDto requestDto = SearchRequestDto.builder()
						.param("content")
						.pageNumber(0)
						.build();
					//when
					ResultActions resultActions = mockMvc.perform(
						get(url)
							.content(objectMapper.writeValueAsString(requestDto))
							.contentType(MediaType.APPLICATION_JSON)
					);
					//then
					resultActions.andExpect(status().isOk());
				}

				@DisplayName("실패 케이스")
				@Nested
				class failureCase {
					@DisplayName("파라미터 미존재")
					@Test
					void noParam() throws Exception {
						//given
						String url = Endpoint.Api.POST + "/content";
						//when
						ResultActions resultActions = mockMvc.perform(
							get(url)
						);
						//then
						resultActions.andExpect(status().isBadRequest());
					}

				}
			}
		}

	}

	@DisplayName("포스트 수정")
	@Nested
	class editPost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			String url = Endpoint.Api.POST + "/" + testPost.getId();

			userDataRepository.findUserById(testUser.getId())
				.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

			PostEditDto body = PostEditDto.builder()
				.title("testTitle")
				.content("testContent")
				.thumbnail("testThumbnail")
				.build();

			String requestBody = objectMapper.writeValueAsString(body);
			//when
			ResultActions resultActions = mockMvc.perform(
				put(url)
					.content(requestBody)
					.contentType(MediaType.APPLICATION_JSON)
			);
			//then
			resultActions.andExpect(status().isFound());
		}

	}

	@DisplayName("포스트 삭제")
	@Nested
	class removePost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			String url = Endpoint.Api.POST + "/" + testPost.getId();
			UserSearchDto user = userDataRepository.findUserById(testUser.getId())
				.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
			//mock
			MockHttpSession mockHttpSession = new MockHttpSession();
			mockHttpSession.setAttribute(mockHttpSession.getId(), user);
			//when
			ResultActions resultActions = mockMvc.perform(
				delete(url)
					.session(mockHttpSession)
			);
			//then
			resultActions.andExpect(status().isNoContent());
		}
	}

}