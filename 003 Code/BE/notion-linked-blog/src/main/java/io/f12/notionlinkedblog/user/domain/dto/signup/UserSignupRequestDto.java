package io.f12.notionlinkedblog.user.domain.dto.signup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserSignupRequestDto {
	@NotNull
	private String username;
	@NotNull
	@Email
	private String email;
	@NotNull
	private String password;

	private String profile;
	private String introduction;
	private String blogTitle;
	private String githubLink;
	private String instagramLink;

	@Builder
	public UserSignupRequestDto(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public UserEntity toEntity() {
		return UserEntity.builder()
			.username(username)
			.email(email)
			.password(password)
			.profile(profile)
			.introduction(introduction)
			.blogTitle(blogTitle)
			.githubLink(githubLink)
			.instagramLink(instagramLink)
			.build();
	}
}
