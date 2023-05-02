package io.f12.notionlinkedblog.security.login.ajax.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.dummy.DummyObject;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.login.email.EmailLoginUserRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AjaxEmailPasswordAuthenticationFilterTests extends DummyObject {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		userDataRepository.deleteAll();
		userDataRepository.save(User.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234")).build());
	}

	@DisplayName("이메일 기반 회원가입 유저 로그인")
	@Nested
	class LoginByEmailTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("로그인 성공")
			@Test
			void successTest() throws Exception {
				//given
				String url = Endpoint.Api.LOGIN_WITH_EMAIL;
				EmailLoginUserRequestDto requestDto = EmailLoginUserRequestDto.builder()
					.email("test@gmail.com")
					.password("1234")
					.build();

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				);

				//then
				resultActions
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").exists())
					.andExpect(jsonPath("$.redirectUrl").exists());
			}
		}

		@DisplayName("비정상 케이스")
		@Nested
		class FailureCase {
			@DisplayName("이메일 조회 실패로 인한 로그인 실패")
			@Test
			void invalidEmailTest() throws Exception {
				//given
				String url = Endpoint.Api.LOGIN_WITH_EMAIL;
				EmailLoginUserRequestDto requestDto = EmailLoginUserRequestDto.builder()
					.email("wrong@gmail.com")
					.password("1234")
					.build();

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				);

				//then
				resultActions
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.errorMessage").value("이메일 또는 비밀번호를 잘못 입력하셨습니다."));
			}

			@DisplayName("비밀번호 오류로 인한 로그인 실패")
			@Test
			void invalidPasswordTest() throws Exception {
				//given
				String url = Endpoint.Api.LOGIN_WITH_EMAIL;
				EmailLoginUserRequestDto requestDto = EmailLoginUserRequestDto.builder()
					.email("wrong@gmail.com")
					.password("1234")
					.build();

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				);

				//then
				resultActions
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.errorMessage").value("이메일 또는 비밀번호를 잘못 입력하셨습니다."));
			}
		}
	}
}
