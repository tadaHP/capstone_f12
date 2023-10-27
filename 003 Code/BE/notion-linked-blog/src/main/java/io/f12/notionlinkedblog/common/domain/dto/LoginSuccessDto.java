package io.f12.notionlinkedblog.common.domain.dto;

import org.springframework.security.core.Authentication;

import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.security.login.ajax.dto.UserWithoutPassword;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class LoginSuccessDto {
	private final UserWithoutPassword user;
	private final String redirectUrl;

	private LoginSuccessDto(UserWithoutPassword userWithoutPassword, String redirectUrl) {
		this.user = userWithoutPassword;
		this.redirectUrl = redirectUrl;
	}

	public static LoginSuccessDto getLoginSuccessDto(Authentication authentication, String redirectUrl) {
		LoginUser principal = (LoginUser)authentication.getPrincipal();
		UserWithoutPassword userWithoutPassword = UserWithoutPassword.of(principal.getUser());
		return LoginSuccessDto.of(userWithoutPassword, redirectUrl);
	}

	private static LoginSuccessDto of(UserWithoutPassword userWithoutPassword, String redirectUrl) {
		return new LoginSuccessDto(userWithoutPassword, redirectUrl);
	}
}
