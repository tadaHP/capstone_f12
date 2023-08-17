package io.f12.notionlinkedblog.email.service.port;

import java.util.Optional;

import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;

public interface RedisEmailVerificationTokenRepository {
	Optional<EmailVerificationToken> findById(String id);

	EmailVerificationToken save(EmailVerificationToken token);

	long count();
}
