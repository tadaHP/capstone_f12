package io.f12.notionlinkedblog.security.login.check.filter;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.security.login.ajax.token.AjaxEmailPasswordAuthenticationToken;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class LoginStatusCheckingFilterTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		userRepository.deleteAll();
		userRepository.save(UserEntity.builder()
			.email("test@gmail.com")
			.username("test")
			.password(passwordEncoder.encode("1234")).build());
	}

	@DisplayName("로그인 후 로그인 상태 조회 테스트")
	@Nested
	class LoginStatusTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("로그인 후 새로고침해서 세션에 정보가 있는 경우")
			@Test
			void alreadyLoginTest() throws Exception {
				//given
				final String url = Endpoint.Api.LOGIN_STATUS;

				// 로그인한 상태를 만들기 위한 세션 생성
				MockHttpSession mockHttpSession = new MockHttpSession();

				UserEntity user = userRepository.findByEmail("test@gmail.com").get();
				LoginUser loginUser = LoginUser.of(user, Set.of(new SimpleGrantedAuthority("ROLE_USER")));
				Authentication authentication =
					AjaxEmailPasswordAuthenticationToken.authenticated(loginUser, null, loginUser.getAuthorities());
				SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

				mockHttpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);

				//when
				ResultActions resultActions = mockMvc.perform(get(url).session(mockHttpSession));

				//then
				resultActions
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.user").exists());
			}

			@DisplayName("세션이 만료되어 없는 경우")
			@Test
			void expiredSessionTest() throws Exception {
				//given
				final String url = Endpoint.Api.LOGIN_STATUS;

				//when
				ResultActions resultActions = mockMvc.perform(get(url));

				//then
				resultActions
					.andExpect(status().isNoContent())
					.andExpect(jsonPath("$.msg").value("No session is exists."));
			}

			@DisplayName("세션은 있지만 로그인되지 않은 경우")
			@Test
			void anonymousTest() throws Exception {
				//given
				final String url = Endpoint.Api.LOGIN_STATUS;

				// 세션은 있지만 로그인되지 않은 상태를 만들기 위한 세션 생성
				MockHttpSession mockHttpSession = new MockHttpSession();

				//when
				ResultActions resultActions = mockMvc.perform(get(url).session(mockHttpSession));

				//then
				resultActions
					.andExpect(status().isNoContent())
					.andExpect(jsonPath("$.msg").value("AnonymousUser is accessed."));
			}
		}
	}
}
