package io.f12.notionlinkedblog.security.common.dto;

import io.f12.notionlinkedblog.common.Endpoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class AuthenticationFailureDto {
	private static final AuthenticationFailureDto instance = new AuthenticationFailureDto();
	private final String loginUrl = Endpoint.Api.LOGIN_WITH_EMAIL;
	private final String message = "로그인 후 이용해 주세요";

	private AuthenticationFailureDto() {
	}

	public static AuthenticationFailureDto getInstance() {
		return instance;
	}
}
