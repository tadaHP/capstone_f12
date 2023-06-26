package io.f12.notionlinkedblog.security.login.check.dto;

import io.f12.notionlinkedblog.security.login.ajax.dto.UserWithoutPassword;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class LoginStatusCheckingSuccessResponseDto {
	private final UserWithoutPassword user;

	private LoginStatusCheckingSuccessResponseDto(UserWithoutPassword userWithoutPassword) {
		this.user = userWithoutPassword;
	}

	public static LoginStatusCheckingSuccessResponseDto of(UserWithoutPassword userWithoutPassword) {
		return new LoginStatusCheckingSuccessResponseDto(userWithoutPassword);
	}
}
