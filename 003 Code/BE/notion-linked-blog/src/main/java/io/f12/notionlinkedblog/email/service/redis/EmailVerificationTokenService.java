package io.f12.notionlinkedblog.email.service.redis;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.email.service.port.RedisEmailVerificationTokenRepository;
import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmailVerificationTokenService {

	private final RedisEmailVerificationTokenRepository emailVerificationTokenRepository;

	public EmailVerificationToken findById(String id) {
		return emailVerificationTokenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID입니다."));
	}

	public void save(EmailVerificationToken token) {
		emailVerificationTokenRepository.save(token);
	}
}
