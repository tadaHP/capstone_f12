package io.f12.notionlinkedblog.security.login.ajax.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class AjaxLoginSuccessDto {
	private final String id;
	private final String redirectUrl;

	private AjaxLoginSuccessDto(String id, String redirectUrl) {
		this.id = id;
		this.redirectUrl = redirectUrl;
	}

	public static AjaxLoginSuccessDto of(String id, String redirectUrl) {
		return new AjaxLoginSuccessDto(id, redirectUrl);
	}
}
