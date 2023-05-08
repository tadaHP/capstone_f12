package io.f12.notionlinkedblog.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.security.service.SecureRandomService;
import io.f12.notionlinkedblog.service.EmailSignupService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class EmailApiControllerTests {
	private static final String redisCookieName = "x-redis-id";
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private EmailVerificationTokenRepository emailVerificationTokenRepository;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private SecureRandomService secureRandomService;
	@MockBean
	private EmailSignupService mockEmailSignupService;

	@Nested
	@DisplayName("인증 코드 검증")
	class VerificationCodeVerifyingTests {
		@Nested
		@DisplayName("정상 케이스")
		class SuccessCase {
			@DisplayName("검증 성공")
			@Test
			void success() throws Exception {
				//given
				final String url = "/api/email/code";
				final String email = "test@gmail.com";

				String code = secureRandomService.generateRandomCodeString();
				EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();

				EmailVerificationToken verificationToken = emailVerificationTokenRepository.save(token);
				String redisId = verificationToken.getId();
				Cookie cookie = new Cookie(redisCookieName, redisId);

				given(mockEmailSignupService.verifyingCode(redisId, code)).willReturn(true);

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.content(code)
						.cookie(cookie)
				);

				//then
				resultActions.andExpect(status().isNoContent());
			}
		}

		@Nested
		@DisplayName("비정상 케이스")
		class FailureCase {
			@DisplayName("잘못된 ID로 인한 인증 실패")
			@Test
			void notProvideValidID() throws Exception {
				//given
				final String url = "/api/email/code";
				final String redisId = "invalidID";

				Cookie cookie = new Cookie(redisCookieName, redisId);

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.content("123456")
						.cookie(cookie)
				);

				//then
				resultActions.andExpect(status().isBadRequest());
			}

			@DisplayName("잘못된 인증 코드로 인한 인증 실패")
			@Test
			void notProvideValidCode() throws Exception {
				//given
				final String url = "/api/email/code";
				final String email = "test@gmail.com";

				String code = secureRandomService.generateRandomCodeString();
				EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();
				emailVerificationTokenRepository.save(token);
				final String redisId = token.getId();
				Cookie cookie = new Cookie(redisCookieName, redisId);

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.content(code)
						.cookie(cookie)
				);

				//then
				resultActions.andExpect(status().isBadRequest());
			}
		}
	}

	@Nested
	@DisplayName("인증 코드 전송")
	class VerificationCodeSendingTests {
		@Nested
		@DisplayName("정상 케이스")
		class SuccessCase {
			@DisplayName("전송 성공")
			@Test
			void success() throws Exception {
				//given
				final String email = "test@gmail.com";
				final String tmpRedisId = "redisId";
				given(mockEmailSignupService.sendMail(email)).willReturn(tmpRedisId);

				//when
				ResultActions resultActions = mockMvc.perform(post("/api/email").content(email));

				//then
				resultActions
					.andExpect(status().isNoContent())
					.andExpect(cookie().exists(redisCookieName))
					.andExpect(cookie().value(redisCookieName, tmpRedisId));
			}
		}

		@Nested
		@DisplayName("비정상 케이스")
		class FailureCase {
			@DisplayName("이메일이 중복되어 실패")
			@Test
			void duplicateEmail() throws Exception {
				//given
				final String alreadyExistingEmail = "hello@gmail.com";
				userDataRepository.save(
					User.builder().email(alreadyExistingEmail).password("1234").username("hello").build());

				//when
				ResultActions resultActions = mockMvc.perform(post("/api/email").content(alreadyExistingEmail));

				//then
				resultActions.andExpect(status().isBadRequest());
			}

			@DisplayName("이메일이 입력되지 않아 실패")
			@Test
			void isEmpty() throws Exception {
				//given
				final String email = "";

				//when
				ResultActions resultActions = mockMvc.perform(post("/api/email").content(email));

				//then
				resultActions.andExpect(status().isBadRequest());
			}

			@DisplayName("이메일 형식에 맞지 않아 실패")
			@Test
			void isInvalidFormat() throws Exception {
				//given
				final String email = "invali\"d@domain.com";

				//when
				ResultActions resultActions = mockMvc.perform(post("/api/email").content(email));

				//then
				resultActions.andExpect(status().isBadRequest());
			}
		}
	}
}
