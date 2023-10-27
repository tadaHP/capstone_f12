package io.f12.notionlinkedblog.medium.email.infrastructure.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.test.context.ActiveProfiles;

import io.f12.notionlinkedblog.common.config.EmbeddedRedisConfig;
import io.f12.notionlinkedblog.common.config.RedisConfig;
import io.f12.notionlinkedblog.email.service.port.RedisEmailVerificationTokenRepository;
import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.security.service.SecureRandomService;

@ActiveProfiles("test")
@DataRedisTest
@Import({
	SecureRandomService.class,
	SecureRandomFactoryBean.class,
	EmbeddedRedisConfig.class,
	RedisConfig.class
})
class EmailVerificationTokenRepositoryTests {
	@Autowired
	private SecureRandomService secureRandomService;
	@Autowired
	private RedisEmailVerificationTokenRepository tokenRepository;

	@DisplayName("이메일 인증을 위한 토큰 생성")
	@Test
	void save() {
		String email = "test@gmail.com";
		String code = secureRandomService.generateRandomCodeString();

		EmailVerificationToken token = EmailVerificationToken.builder().email(email).code(code).build();
		EmailVerificationToken savedToken = tokenRepository.save(token);

		assertThat(tokenRepository.count()).isGreaterThan(0L);
		assertThat(savedToken.getEmail()).isEqualTo(email);
		assertThat(savedToken.getCode()).isEqualTo(code);
	}
}
