package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserEditDto;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.service.user.UserService;

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
	private UserDataRepository userDataRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private User testUser;

	@BeforeEach
	void setup() {
		testUser = userDataRepository.save(User.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234"))
			.build()
		);
	}

	@AfterEach
	void teardown() {
		userDataRepository.deleteAll();
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

		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("유저 정보 수정")
			@WithUserDetails(value = "test@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
			@Test
			void editUserInfoTest() throws Exception {
				//given
				final String url = Endpoint.Api.USER + "/" + testUser.getId();

				UserEditDto editDto = UserEditDto.builder().username("test1").build();

				//stub
				String requestBody = om.writeValueAsString(editDto);
				given(userService.editUserInfo(testUser.getId(), editDto)).willReturn(testUser.getId());

				//when
				ResultActions resultActions = mockMvc.perform(
					put(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody));

				//then
				resultActions.andExpect(status().isOk());
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
				final String url = Endpoint.Api.USER + "/" + anotherUserId;
				UserEditDto editDto = UserEditDto.builder().username("test1").build();
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
}
