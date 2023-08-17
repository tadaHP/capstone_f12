package io.f12.notionlinkedblog.user.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserSearchDto {
	private Long id;
	private String username;
	private String email;
	private String introduction;
	private String blogTitle;
	private String githubLink;
	private String instagramLink;
	private Boolean notionCertificate;
}
