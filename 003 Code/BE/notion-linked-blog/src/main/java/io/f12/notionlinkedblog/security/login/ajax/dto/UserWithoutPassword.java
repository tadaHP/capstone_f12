package io.f12.notionlinkedblog.security.login.ajax.dto;

import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class UserWithoutPassword {
	private final Long id;
	private final String username;
	private final String email;
	private final String introduction;
	private final String blogTitle;
	private final String githubLink;
	private final String instagramLink;
	private final Boolean notionCertificate;

	private UserWithoutPassword(UserEntity user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.introduction = user.getIntroduction();
		this.blogTitle = user.getBlogTitle();
		this.githubLink = user.getGithubLink();
		this.instagramLink = user.getInstagramLink();
		this.notionCertificate = user.getNotionOauth() != null;
	}

	public static UserWithoutPassword of(UserEntity user) {
		return new UserWithoutPassword(user);
	}
}
