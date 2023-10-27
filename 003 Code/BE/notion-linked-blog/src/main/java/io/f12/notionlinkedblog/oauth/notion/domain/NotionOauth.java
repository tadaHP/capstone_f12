package io.f12.notionlinkedblog.oauth.notion.domain;

import io.f12.notionlinkedblog.oauth.notion.infrastructure.NotionOauthEntity;
import io.f12.notionlinkedblog.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class NotionOauth {
	private Long id;
	private User user;
	private String accessToken;
	private String botId;

	public NotionOauthEntity toEntity() {
		return NotionOauthEntity.builder()
			.id(this.id)
			.accessToken(this.accessToken)
			.botId(this.botId)
			.user(this.user.toEntity())
			.build();
	}
}
