package io.f12.notionlinkedblog.domain.verification;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RedisHash(timeToLive = 300L)
public class EmailVerificationToken {
	private final String email;
	private final String code;
	@Id
	private String id;
}
