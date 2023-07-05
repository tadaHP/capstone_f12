package io.f12.notionlinkedblog.security.login.ajax.dto;

import io.f12.notionlinkedblog.domain.user.User;
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

	private UserWithoutPassword(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.introduction = user.getIntroduction();
		this.blogTitle = user.getBlogTitle();
		this.githubLink = user.getGithubLink();
		this.instagramLink = user.getInstagramLink();
	}

	public static UserWithoutPassword of(User user) {
		return new UserWithoutPassword(user);
	}
}
