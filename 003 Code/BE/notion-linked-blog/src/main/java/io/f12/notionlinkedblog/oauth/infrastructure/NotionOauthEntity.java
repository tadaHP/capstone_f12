package io.f12.notionlinkedblog.oauth.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.oauth.domain.notion.NotionOauth;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "notion_oauth")
public class NotionOauthEntity {
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	@NotNull
	private UserEntity user;

	@Column(unique = true)
	private String accessToken;
	@Column(unique = true)
	private String botId;

	public NotionOauth toModel() {
		return NotionOauth.builder()
			.id(this.id)
			.user(this.user.toModel())
			.accessToken(this.accessToken)
			.botId(this.botId)
			.build();
	}

	public void renewAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
