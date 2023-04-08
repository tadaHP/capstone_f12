package io.f12.notionlinkedblog.repository.redis;

import org.springframework.data.repository.CrudRepository;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, String> {
}
