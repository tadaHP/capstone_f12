package io.f12.notionlinkedblog.domain.user.dto.info;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class UserSearchDto {
	private Long id;
	private String username;
	private String email;
	private String profile;
	private String introduction;
	private String blogTitle;
	private String githubLink;
	private String instagramLink;

	@Builder
	public UserSearchDto(Long id, String username, String email, String profile, String introduction,
		String blogTitle, String githubLink, String instagramLink) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.profile = profile;
		this.introduction = introduction;
		this.blogTitle = blogTitle;
		this.githubLink = githubLink;
		this.instagramLink = instagramLink;
	}

}
