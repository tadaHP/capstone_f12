package io.f12.notionlinkedblog.post.api;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.post.api.port.PostService;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.domain.dto.PostEditDto;
import io.f12.notionlinkedblog.post.domain.dto.SearchRequestDto;
import io.f12.notionlinkedblog.post.domain.dto.ThumbnailReturnDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
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
	private UserRepository userRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@MockBean
	private PostService postService;

	private UserEntity testUser;
	private PostEntity testPost;

	@BeforeEach
	void init() {
		testUser = userRepository.save(UserEntity.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234"))
			.build()
		);
		testPost = postRepository.save(PostEntity.builder()
			.user(testUser)
			.title("testTitle")
			.isPublic(true)
			.content("testContent").build());
	}

	@AfterEach
	void clear() {
		postRepository.deleteAll();
		userRepository.deleteAll();
	}

	@DisplayName("포스트 생성")
	@Nested
	class CreatePost {
		@DisplayName("성공케이스")
		@Nested
		class SuccessCase {
			@DisplayName("썸네일 미존재 케이스")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void createPostWithoutThumbnail() throws Exception {
				//given
				final String url = Endpoint.Api.POST;
				//mock
				MockMultipartFile titleData = new MockMultipartFile("title",
					"testTitle".getBytes());
				log.info("titleData.getContentType(): {}", titleData.getContentType());
				MockMultipartFile contentData = new MockMultipartFile("content",
					"testContent".getBytes());
				MockMultipartFile descriptionData = new MockMultipartFile("description",
					"description".getBytes());
				MockMultipartFile isPublicData = new MockMultipartFile("isPublic",
					"0".getBytes());
				//when
				ResultActions resultActions = mockMvc.perform(
					multipart(url)
						.file(titleData)
						.file(contentData)
						.file(descriptionData)
						.file(isPublicData)
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
				File file = new ClassPathResource("static/images/test.jpg").getFile();
				//mock
				MockMultipartFile fileInfo = new MockMultipartFile("file", "", IMAGE_JPEG_VALUE,
					Files.readAllBytes(file.toPath()));
				MockMultipartFile titleData = new MockMultipartFile("title",
					"testTitle".getBytes());
				MockMultipartFile contentData = new MockMultipartFile("content",
					"testContent".getBytes());
				MockMultipartFile descriptionData = new MockMultipartFile("description",
					"description".getBytes());
				MockMultipartFile isPublicData = new MockMultipartFile("isPublic",
					"0".getBytes());

				//when
				ResultActions resultActions = mockMvc.perform(
					multipart(url)
						.file(fileInfo)
						.file(titleData)
						.file(contentData)
						.file(descriptionData)
						.file(isPublicData)
				);
				//then
				resultActions.andExpect(status().isCreated());
			}

		}

	}

	@DisplayName("포스트 조회")
	@Nested
	class PostLookup {

		@DisplayName("단건 조회")
		@Nested
		class SingleLookup {
			@DisplayName("포스트 ID로 조회")
			@Nested
			class getPostById {
				@DisplayName("성공 케이스")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/" + testPost.getId();
					//Mock
					given(postService.getPostDtoById(testPost.getId(), null))
						.willReturn(PostSearchDto.builder().build());
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
		class MultiLookup {
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
		class SearchLatestPosts {
			@DisplayName("성공 케이스")
			@Test
			void successCase() throws Exception {
				//given
				Integer pageNumber = 1;
				String url = Endpoint.Api.POST + "/newest/" + pageNumber;

				postRepository.save(PostEntity.builder()
					.user(testUser)
					.title("testTitle 2")
					.isPublic(true)
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
			class FailCase {
				@DisplayName("pageNumber 미존재")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/newest/";

					postRepository.save(PostEntity.builder()
						.user(testUser)
						.title("testTitle 2")
						.isPublic(true)
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
		class SearchPopularPosts {
			@DisplayName("성공 케이스")
			@Test
			void successCase() throws Exception {
				//given
				Integer pageNumber = 1;
				String url = Endpoint.Api.POST + "/trend/" + pageNumber;

				postRepository.save(PostEntity.builder()
					.user(testUser)
					.title("testTitle 2")
					.isPublic(true)
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
			class FailCase {
				@DisplayName("pageNumber 미존재")
				@Test
				void successCase() throws Exception {
					//given
					String url = Endpoint.Api.POST + "/trend/";

					postRepository.save(PostEntity.builder()
						.user(testUser)
						.title("testTitle 2")
						.isPublic(true)
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
	class EditPost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			String url = Endpoint.Api.POST + "/" + testPost.getId();

			userRepository.findUserById(testUser.getId())
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
			resultActions.andExpect(status().isOk());
		}

	}

	@DisplayName("포스트 삭제")
	@Nested
	class RemovePost {
		@DisplayName("성공 케이스")
		@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@Test
		void successCase() throws Exception {
			//given
			String url = Endpoint.Api.POST + "/" + testPost.getId();
			UserEntity user = userRepository.findUserById(testUser.getId())
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
	class LikePost {
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
		//TODO: 변경 필요
		@DisplayName("성공 케이스")
		@Test
		void successCase() throws Exception {
			//given
			File file = new ClassPathResource("static/images/test.jpg").getFile();
			UrlResource urlResource = new UrlResource("file:" + file.getPath());
			final String url = Endpoint.Api.REQUEST_THUMBNAIL_IMAGE + "testImage";
			ThumbnailReturnDto dto = ThumbnailReturnDto.builder()
				.thumbnailPath("path.jpg")
				.image(urlResource)
				.build();
			//mock
			given(postService.readImageFile("testImage"))
				.willReturn(new File(""));
			//when
			ResultActions resultActions = mockMvc.perform(
				get(url)
			);
			//then
			resultActions.andExpect(status().isOk());
		}
	}

}