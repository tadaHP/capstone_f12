package io.f12.notionlinkedblog.api.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.service.post.PostService;
import lombok.extern.slf4j.Slf4j;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Slf4j
class PostApiControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private PostDataRepository postRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Mock
	private PostDataRepository postDataRepository;
	@MockBean
	private PostService postService;

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
		testPost = postRepository.save(Post.builder()
			.user(testUser)
			.title("testTitle")
			.content("testContent").build());
	}

	@AfterEach
	void clear() {
		postRepository.deleteAll();
		userDataRepository.deleteAll();
	}

	@DisplayName("포스트 생성")
	@Nested
	class createPost {
		@DisplayName("성공케이스")
		@Nested
		class successCase {
			@DisplayName("썸네일 미존재 케이스")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void createPostWithoutThumbnail() throws Exception {
				//given
				final String url = Endpoint.Api.POST;
				UserSearchDto user = userDataRepository.findUserById(testUser.getId())
					.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
				//mock
				MockMultipartFile titleData = new MockMultipartFile("title",
					"testTitle".getBytes());
				MockMultipartFile contentData = new MockMultipartFile("content",
					"testContent".getBytes());
				//when
				ResultActions resultActions = mockMvc.perform(
					multipart(url)
						.file(titleData)
						.file(contentData)
				);
				//then
				resultActions.andExpect(status().isCreated());
			}

			@DisplayName("썸네일 존재 케이스")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void createPostWithThumbnail() throws Exception {
				//given
				final String url = Endpoint.Api.POST;
				UserSearchDto user = userDataRepository.findUserById(testUser.getId())
					.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
				File file = new ClassPathResource("static/images/test.jpg").getFile();
				//mock
				MockMultipartFile fileInfo = new MockMultipartFile("file", "", IMAGE_JPEG_VALUE,
					Files.readAllBytes(file.toPath()));
				MockMultipartFile titleData = new MockMultipartFile("title",
					"testTitle".getBytes());
				MockMultipartFile contentData = new MockMultipartFile("content",
					"testContent".getBytes());

				//when
				ResultActions resultActions = mockMvc.perform(
					multipart(url)
						.file(fileInfo)
						.file(titleData)
						.file(contentData));
				//then
				resultActions.andExpect(status().isCreated());
			}

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
					//Mock
					BDDMockito.given(postDataRepository.findById(testPost.getId()))
						.willReturn(Optional.ofNullable(testPost));
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

		@DisplayName("최신순으로 테스트 조회")
		@Nested
		class searchLatestPosts {
			@DisplayName("성공 케이스")
			@Test
			void successCase() throws Exception {
				//given
				Integer pageNumber = 1;
				String url = Endpoint.Api.POST + "/newest/" + pageNumber;

				postRepository.save(Post.builder()
					.user(testUser)
					.title("testTitle 2")
					.content("testContent").build());
				//when
				ResultActions resultActions = mockMvc.perform(
					get(url)
				);
				//then
				resultActions.andExpect(status().isOk());
			}

			@DisplayName("실패 케이스")
			@Nested
			class failureCase {
				@DisplayName("pageNumber 미존재")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/newest/";

					postRepository.save(Post.builder()
						.user(testUser)
						.title("testTitle 2")
						.content("testContent").build());
					//when
					ResultActions resultActions = mockMvc.perform(
						get(url)
					);
					//then
					resultActions.andExpect(status().isBadRequest());
				}
			}
		}

		@DisplayName("인기순으로 테스트 조회")
		@Nested
		class searchPopularPosts {
			@DisplayName("성공 케이스")
			@Test
			void successCase() throws Exception {
				//given
				Integer pageNumber = 1;
				String url = Endpoint.Api.POST + "/trend/" + pageNumber;

				postRepository.save(Post.builder()
					.user(testUser)
					.title("testTitle 2")
					.content("testContent").build());
				//when
				ResultActions resultActions = mockMvc.perform(
					get(url)
				);
				//then
				resultActions.andExpect(status().isOk());
			}

			@DisplayName("실패 케이스")
			@Nested
			class failureCase {
				@DisplayName("pageNumber 미존재")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/trend/";

					postRepository.save(Post.builder()
						.user(testUser)
						.title("testTitle 2")
						.content("testContent").build());
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

	@DisplayName("포스트 좋아요")
	@Nested
	class likePost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			final String url = Endpoint.Api.POST + "/like/" + testPost.getId();
			//when
			ResultActions resultActions = mockMvc.perform(
				post(url)
			);
			//then
			resultActions.andExpect(status().isCreated());

		}
	}

	@DisplayName("썸네일 실제 조회")
	@Nested
	class ThumbnailLookup {
		@DisplayName("성공 케이스")
		@Test
		void successCase() throws Exception {
			//given
			final String url = Endpoint.Api.REQUEST_IMAGE + "testImage";
			//when
			ResultActions resultActions = mockMvc.perform(
				get(url)
			);
			//then
			resultActions.andExpect(status().isOk());
		}
	}

}