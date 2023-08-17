package io.f12.notionlinkedblog.email.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.f12.notionlinkedblog.dummy.DummyObject;
import io.f12.notionlinkedblog.email.service.redis.EmailVerificationTokenService;
import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;

@ExtendWith(MockitoExtension.class)
class EmailSignupServiceTests {
	@InjectMocks
	EmailSignupService emailSignupService;
	@Mock
	EmailVerificationTokenService emailVerificationTokenService;

	@DisplayName("인증 코드")
	@Nested
	class VerificationCodeTests extends DummyObject {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("검증 성공")
			@Test
			void verifyingCode() {
				//given
				EmailVerificationToken mockEmailVerificationToken = newMockEmailVerificationToken("1", "123456");

				// stub 1
				given(emailVerificationTokenService.findById(any())).willReturn(mockEmailVerificationToken);

				//when
				boolean isVerified = emailSignupService.verifyingCode("1", "123456");

				//then
				assertThat(isVerified).isTrue();
			}
		}

		@DisplayName("비정상 케이스")
		@Nested
		class FailureCase {
			@DisplayName("검증 실패")
			@Test
			void verifyingCode() {
				//given
				EmailVerificationToken mockEmailVerificationToken = newMockEmailVerificationToken("1", "123456");

				// stub 1
				given(emailVerificationTokenService.findById(any())).willReturn(mockEmailVerificationToken);

				//when
				boolean isVerified = emailSignupService.verifyingCode("1", "987654");

				//then
				assertThat(isVerified).isFalse();
			}
		}
	}
}
