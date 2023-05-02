package io.f12.notionlinkedblog.security.login.ajax.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class AjaxLoginFailureDto {
	private final String errorMessage;

	private AjaxLoginFailureDto(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static AjaxLoginFailureDto from(String errorMessage) {
		return new AjaxLoginFailureDto(errorMessage);
	}
}
