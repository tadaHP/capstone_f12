package io.f12.notionlinkedblog.domain.user.dto.login.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class EmailLoginUserRequestDto {
	private String email;
	private String password;

	@Builder
	public EmailLoginUserRequestDto(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
