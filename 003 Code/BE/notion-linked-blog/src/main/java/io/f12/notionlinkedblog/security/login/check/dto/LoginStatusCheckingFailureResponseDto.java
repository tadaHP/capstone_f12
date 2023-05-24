package io.f12.notionlinkedblog.security.login.check.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class LoginStatusCheckingFailureResponseDto {
	private final String msg;

	private LoginStatusCheckingFailureResponseDto(String msg) {
		this.msg = msg;
	}

	public static LoginStatusCheckingFailureResponseDto of(String msg) {
		return new LoginStatusCheckingFailureResponseDto(msg);
	}
}
