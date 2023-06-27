package io.f12.notionlinkedblog.service.redis;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.repository.redis.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmailVerificationTokenService {

	private final EmailVerificationTokenRepository emailVerificationTokenRepository;

	public EmailVerificationToken findById(String id) {
		return emailVerificationTokenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ID입니다."));
	}

	public void save(EmailVerificationToken token) {
		emailVerificationTokenRepository.save(token);
	}
}
