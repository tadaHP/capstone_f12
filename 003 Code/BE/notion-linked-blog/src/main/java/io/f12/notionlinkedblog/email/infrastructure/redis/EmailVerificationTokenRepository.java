package io.f12.notionlinkedblog.email.infrastructure.redis;

import org.springframework.data.repository.CrudRepository;

import io.f12.notionlinkedblog.email.service.port.RedisEmailVerificationTokenRepository;
import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;

public interface EmailVerificationTokenRepository
	extends CrudRepository<EmailVerificationToken, String>, RedisEmailVerificationTokenRepository {
}
