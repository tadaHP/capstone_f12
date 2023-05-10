package io.f12.notionlinkedblog.security.login.ajax.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class AjaxLoginSuccessDto {
	private final UserWithoutPassword user;
	private final String redirectUrl;

	private AjaxLoginSuccessDto(UserWithoutPassword userWithoutPassword, String redirectUrl) {
		this.user = userWithoutPassword;
		this.redirectUrl = redirectUrl;
	}

	public static AjaxLoginSuccessDto of(UserWithoutPassword userWithoutPassword, String redirectUrl) {
		return new AjaxLoginSuccessDto(userWithoutPassword, redirectUrl);
	}
}
