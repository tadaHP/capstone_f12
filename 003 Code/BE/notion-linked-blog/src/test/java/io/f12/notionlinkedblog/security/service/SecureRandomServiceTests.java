package io.f12.notionlinkedblog.security.service;

import static io.f12.notionlinkedblog.security.service.SecureRandomService.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecureRandomServiceTests {

	@Mock
	SecureRandomService secureRandomService;

	@DisplayName("인증 코드 생성")
	@Test
	void generateVerifyingCode() {
		int randomCode = secureRandomService.generateRandomCode();
		Assertions.assertThat(randomCode).isLessThan(DEFAULT_SECURE_BOUND);
	}
}
