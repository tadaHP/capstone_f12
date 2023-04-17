package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.service.user.UserService;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserApiControllerTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper om;
	@MockBean
	UserService userService;

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
				final String url = "/api/users/email/signup";
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
			@Test
			void getUserInfoTest() throws Exception {
				//given
				UserSearchDto returnDto = UserSearchDto.builder()
					.username("tester")
					.email("test@test.com")
					.build();
				Long fakeId = 1L;
				ReflectionTestUtils.setField(returnDto, "id", fakeId);
				//mock
				given(userService.getUserInfo(fakeId))
					.willReturn(returnDto);
				//when
				ResultActions resultActions = mockMvc.perform(get("/api/users/1"))
					.andDo(print());
				//then
				resultActions
					.andExpectAll(
						status().isOk(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						jsonPath("$.id").value(fakeId),
						jsonPath("$.username").value(returnDto.getUsername()),
						jsonPath("$.email").value(returnDto.getEmail())
					);
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
			@Test
			void editUserInfoTest() throws Exception {
				//given
				final Long fakeUserId = 1L;
				final String url = "/api/users/1";
				UserSearchDto beforeUser = UserSearchDto.builder()
					.username("user1")
					.email("before@test.com")
					.build();
				UserSearchDto changedDto = UserSearchDto.builder()
					.username("changed")
					.email("changed@test.com")
					.build();
				ReflectionTestUtils.setField(beforeUser, "id", fakeUserId);
				//mock
				MockHttpSession mockHttpSession = new MockHttpSession();
				mockHttpSession.setAttribute(mockHttpSession.getId(), beforeUser);
				given(userService.editUserInfo(fakeUserId, changedDto.getUsername(), changedDto.getEmail(), null, null,
					null, null, null, null))
					.willReturn(fakeUserId);
				String requestBody = om.writeValueAsString(changedDto);
				//when
				ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.cookie(new Cookie("JSESSIONID", mockHttpSession.getId()))
							.content(requestBody)
							.session(mockHttpSession))
					.andDo(print());
				//then
				resultActions.andExpectAll(
					status().isFound(),
					redirectedUrl(url)
				);
			}
		}

		@DisplayName("실패 케이스")
		@Nested
		class FailureCase {
			@DisplayName("세션 미존재, 로그인 하지 않은 상태")
			@Test
			void editUserInfoWithoutSessionTest() throws Exception {
				//given
				final Long fakeUserId = 1L;
				final String url = "/api/users/1";
				User beforeUser = User.builder()
					.username("user1")
					.email("before@test.com")
					.password("1234")
					.build();
				UserSearchDto changedDto = UserSearchDto.builder()
					.username("changed")
					.email("changed@test.com")
					.build();
				ReflectionTestUtils.setField(beforeUser, "id", fakeUserId);
				//mock
				String requestBody = om.writeValueAsString(changedDto);
				MockHttpSession mockHttpSession = new MockHttpSession();
				//when
				ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.cookie(new Cookie("JSESSIONID", mockHttpSession.getId()))
							.content(requestBody))
					.andDo(print());
				//then
				assertThat(resultActions.andReturn().getResponse().getContentAsString()).isEqualTo("로그인 되어있지 않습니다.");
				resultActions.andExpect(status().isBadRequest());
			}

			@DisplayName("로그인 유저와 변경유저 불일치")
			@Test
			void editUnMatchingUserInfoTest() throws Exception {
				//given
				final Long fakeUserId = 2L;
				final String url = "/api/users/1";
				UserSearchDto beforeUser = UserSearchDto.builder()
					.username("user1")
					.email("before@test.com")
					.build();
				UserSearchDto changedDto = UserSearchDto.builder()
					.username("changed")
					.email("changed@test.com")
					.build();
				ReflectionTestUtils.setField(beforeUser, "id", fakeUserId);
				//mock
				MockHttpSession mockHttpSession = new MockHttpSession();
				mockHttpSession.setAttribute(mockHttpSession.getId(), beforeUser);
				String requestBody = om.writeValueAsString(changedDto);
				//when
				ResultActions resultActions = mockMvc.perform(
						put(url)
							.contentType(MediaType.APPLICATION_FORM_URLENCODED)
							.cookie(new Cookie("JSESSIONID", mockHttpSession.getId()))
							.content(requestBody)
							.session(mockHttpSession))
					.andDo(print());
				//then
				assertThat(resultActions.andReturn().getResponse().getContentAsString()).isEqualTo("동일 회원이 아닙니다.");
				resultActions.andExpect(status().isBadRequest());
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
			@Test
			void deleteUserTest() throws Exception {
				//given
				final Long fakeUserId = 1L;
				final String url = "/api/users/1";
				UserSearchDto beforeUser = UserSearchDto.builder()
					.username("user1")
					.email("before@test.com")
					.build();
				ReflectionTestUtils.setField(beforeUser, "id", fakeUserId);
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
			@DisplayName("세션 미존재, 로그인 하지 않은 상태")
			@Test
			void deleteUserWithoutSessionTest() throws Exception {
				//given
				final String url = "/api/users/1";
				//mock
				MockHttpSession mockHttpSession = new MockHttpSession();
				//when
				ResultActions resultActions = mockMvc.perform(
						delete(url)
							.cookie(new Cookie("JSESSIONID", mockHttpSession.getId()))
							.session(mockHttpSession))
					.andDo(print());
				//then
				resultActions.andExpect(status().isBadRequest());
			}

			@DisplayName("로그인 유저와 변경유저 불일치")
			@Test
			void deleteUnMatchingUserInfoTest() throws Exception {
				//given
				final Long fakeUserId = 2L;
				final String url = "/api/users/1";
				UserSearchDto beforeUser = UserSearchDto.builder()
					.username("user1")
					.email("before@test.com")
					.build();
				ReflectionTestUtils.setField(beforeUser, "id", fakeUserId);
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
				resultActions.andExpect(status().isBadRequest());
			}
		}
	}

}
