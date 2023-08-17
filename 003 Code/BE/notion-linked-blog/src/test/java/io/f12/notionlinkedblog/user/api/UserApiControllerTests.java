package io.f12.notionlinkedblog.user.api;

import static io.f12.notionlinkedblog.email.api.EmailApiController.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;

import javax.servlet.http.Cookie;

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
import org.springframework.http.HttpMethod;
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
import io.f12.notionlinkedblog.user.api.port.UserService;
import io.f12.notionlinkedblog.user.api.response.UserSearchDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserApiControllerTests {

	@MockBean
	UserService userService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private UserEntity testUser;

	@BeforeEach
	void setup() {
		testUser = userRepository.save(UserEntity.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234"))
			.build()
		);
	}

	@AfterEach
	void teardown() {
		userRepository.deleteAll();
	}

	@DisplayName("이메일 기반 회원가입")
	@Nested
	class SignUpByEmailTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("회원가입 성공")
			@Test
			void success() throws Exception {
				//given
				final String url = Endpoint.Api.USER + "/email/signup";
				UserSignupRequestDto userSignupRequestDto = UserSignupRequestDto.builder()
					.username("test")
					.email("test@gmail.com")
					.password("1234")
					.build();
				String requestBody = om.writeValueAsString(userSignupRequestDto);
				MockHttpSession mockHttpSession = new MockHttpSession();
				mockHttpSession.setAttribute(emailVerifiedAttr, "verified");

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.session(mockHttpSession));

				//then
				resultActions.andExpect(status().isCreated());
			}
		}
	}

	@DisplayName("유저정보 조회 api")
	@Nested
	class UserCheckingApiTest {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("유저 정보 가져오기")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void getUserInfoTest() throws Exception {
				//given
				UserSearchDto returnDto = UserSearchDto.builder()
					.id(testUser.getId())
					.username("test")
					.email("test@gmail.com")
					.build();

				//stub
				given(userService.getUserInfo(any())).willReturn(returnDto);

				//when
				ResultActions resultActions = mockMvc.perform(get(Endpoint.Api.USER + "/1"));

				//then
				resultActions
					.andExpectAll(
						status().isOk(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						jsonPath("$.id").exists(),
						jsonPath("$.username").value(returnDto.getUsername()),
						jsonPath("$.email").value(returnDto.getEmail()));
			}
		}
	}

	@DisplayName("유저정보 수정 api")
	@Nested
	class UserInfoEditApiTest {
		@DisplayName("회원 기본정보 변경")
		@Nested
		class UserBasicInfoEdit {

			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 정보 수정")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/basic/" + testUser.getId();

					UserBasicInfoEditDto editDto = UserBasicInfoEditDto.builder().username("test1").build();

					//stub
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isCreated());
				}
			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {
				@DisplayName("로그인 유저와 변경유저 불일치")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUnMatchingUserInfoTest() throws Exception {
					//given
					String anotherUserId = "0";
					final String url = Endpoint.Api.USER + "/basic/" + anotherUserId;
					UserBasicInfoEditDto editDto = UserBasicInfoEditDto.builder().username("test1").build();
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isNotFound());
				}

				@DisplayName("필요한 dto 정보 미제공")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUnFilledDtoUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/basic/" + testUser.getId();

					UserBasicInfoEditDto editDto = UserBasicInfoEditDto.builder().introduction("edited").build();

					//stub
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isBadRequest());
				}
			}
		}

		@DisplayName("회원 BlogTitle 변경")
		@Nested
		class UserBlogTitleEdit {
			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 정보 수정")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/blogTitle/" + testUser.getId();

					UserBlogTitleEditDto editDto = UserBlogTitleEditDto.builder().blogTitle("edited").build();

					//stub
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isCreated());
				}
			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {
				@DisplayName("로그인 유저와 변경유저 불일치")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUnMatchingUserInfoTest() throws Exception {
					//given
					String anotherUserId = "0";
					final String url = Endpoint.Api.USER + "/blogTitle/" + anotherUserId;
					UserBlogTitleEditDto editDto = UserBlogTitleEditDto.builder().blogTitle("edited").build();

					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isNotFound());
				}

				@DisplayName("blogTitle 정보 미 전송")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUnFilledDtoUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/blogTitle/" + testUser.getId();

					UserBlogTitleEditDto editDto = UserBlogTitleEditDto.builder().build();

					//stub
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isBadRequest());
				}
			}

		}

		@DisplayName("회원 SNS 정보 변경")
		@Nested
		class UserSNSInfoEdit {
			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 정보 수정")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/social/" + testUser.getId();

					UserSocialInfoEditDto editDto = UserSocialInfoEditDto.builder()
						.githubLink("editedGit")
						.instagramLink("editedInsta")
						.build();

					//stub
					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isCreated());
				}
			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {
				@DisplayName("로그인 유저와 변경유저 불일치")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUnMatchingUserInfoTest() throws Exception {
					//given
					String anotherUserId = "0";
					final String url = Endpoint.Api.USER + "/social/" + anotherUserId;
					UserSocialInfoEditDto editDto = UserSocialInfoEditDto.builder()
						.githubLink("editedGit")
						.instagramLink("editedInsta")
						.build();

					String requestBody = om.writeValueAsString(editDto);

					//when
					ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));

					//then
					resultActions.andExpect(status().isNotFound());
				}
			}
		}

		@DisplayName("회원 프로파일 이미지 정보 변경")
		@Nested
		class UserProfileImageEdit {
			// @DisplayName("정상 케이스")
			// @Nested
			// class SuccessCase {
			// 	@DisplayName("유저 정보 수정")
			// 	@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			// 	@Test
			// 	void editUserInfoTest() throws Exception {
			// 		//given
			// 		final String url = Endpoint.Api.USER + "/profileImage/" + testUser.getId();
			// 		File file = new ClassPathResource("static/images/test.jpg").getFile();
			// 		FileInputStream fileInputStream = new FileInputStream(file.getPath());
			// 		//stub
			// 		FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false,
			// 			file.getName(), (int)file.length(), file.getParentFile());
			//
			// 		try {
			// 			IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
			// 		} catch (IOException ex) {
			// 			log.error(String.valueOf(ex));
			// 		}
			//
			// 		MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
			//
			// 		//when
			// 		ResultActions resultActions = mockMvc.perform(
			// 			multipart(HttpMethod.PUT, url)
			// 				.file(multipartFile));
			//
			// 		//then
			// 		resultActions.andExpect(status().isCreated());
			// 	}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {

				@DisplayName("유저 정보 수정")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void editUserInfoTest() throws Exception {
					//given
					Long unMatchedUSerId = 0L;
					final String url = Endpoint.Api.USER + "/profileImage/" + unMatchedUSerId;
					File file = new ClassPathResource("static/images/test.jpg").getFile();
					//stub
					MockMultipartFile fileInfo = new MockMultipartFile("file", "", IMAGE_JPEG_VALUE,
						Files.readAllBytes(file.toPath()));

					//when
					ResultActions resultActions = mockMvc.perform(
						multipart(HttpMethod.PUT, url)
							.file(fileInfo));

					//then
					resultActions.andExpect(status().isNotFound());
				}

			}
		}

		@DisplayName("회원 프로파일 이미지 정보 삭제")
		@Nested
		class UserProfileImageRemove {

			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 정보 삭제")
				@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
				@Test
				void removeUserInfoTest() throws Exception {
					//given
					final String url = Endpoint.Api.USER + "/profileImage/" + testUser.getId();
					//when
					ResultActions perform = mockMvc.perform(delete(url));
					//then
					perform.andExpectAll(status().isNoContent());

				}
			}

		}
	}

	@DisplayName("유저정보 삭제 api")
	@Nested
	class UserDeleteApiTest {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("유저 정보 삭제")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void deleteUserTest() throws Exception {
				//given
				final String url = Endpoint.Api.USER + "/" + testUser.getId();
				UserSearchDto beforeUser = UserSearchDto.builder()
					.username("user1")
					.email("before@test.com")
					.build();
				//mock
				MockHttpSession mockHttpSession = new MockHttpSession();
				mockHttpSession.setAttribute(mockHttpSession.getId(), beforeUser);
				//when
				ResultActions resultActions = mockMvc.perform(
						delete(url)
							.cookie(new Cookie("JSESSIONID", mockHttpSession.getId()))
							.session(mockHttpSession))
					.andDo(print());
				//then
				resultActions.andExpect(status().isNoContent());
			}
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailureCase {
			@DisplayName("로그인 유저와 변경유저 불일치")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void deleteUnMatchingUserInfoTest() throws Exception {
				//given
				String anotherUserId = "0";
				final String url = Endpoint.Api.USER + "/" + anotherUserId;

				//when
				ResultActions resultActions = mockMvc.perform(delete(url));

				//then
				resultActions.andExpect(status().isNotFound());
			}
		}
	}

	@DisplayName("유저 프로파일 조회 api")
	@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@Nested
	class UserProfileApiTest {
		@DisplayName("성공 케이스")
		@Test
		void successfulCase() throws Exception {
			//given
			File file = new ClassPathResource("static/images/test.jpg").getFile();
			UrlResource urlResource = new UrlResource("file:" + file.getPath());
			final String url = Endpoint.Api.USER + "/profile/" + testUser.getId();

			//stub
			given(userService.readImageFile(testUser.getId())).willReturn(new File(""));
			//when
			ResultActions resultActions = mockMvc.perform(get(url));

			//then
			resultActions.andExpect(status().isOk());
		}
	}
}
