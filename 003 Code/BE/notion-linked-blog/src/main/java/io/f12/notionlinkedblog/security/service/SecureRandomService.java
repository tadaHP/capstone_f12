package io.f12.notionlinkedblog.security.service;

import java.security.SecureRandom;

import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecureRandomService {

	public static final int DEFAULT_SECURE_BOUND = 1_000_000;
	private final SecureRandomFactoryBean secureRandomFactoryBean;

	public String generateRandomCodeString() {
		return String.format("%06d", generateRandomCode());
	}

	public int generateRandomCode() {
		return generateRandomCode(DEFAULT_SECURE_BOUND);
	}

	public int generateRandomCode(int bound) {
		SecureRandom secureRandom = null;
		try {
			secureRandom = secureRandomFactoryBean.getObject();
		} catch (Exception e) {
			log.info(String.valueOf(e));
		}
		assert secureRandom != null;
		secureRandom.setSeed(System.currentTimeMillis());
		return secureRandom.nextInt(bound);
	}
}
