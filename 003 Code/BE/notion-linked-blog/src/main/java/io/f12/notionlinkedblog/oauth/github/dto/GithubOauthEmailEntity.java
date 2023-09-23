package io.f12.notionlinkedblog.oauth.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GithubOauthEmailEntity {
	@JsonProperty("email")
	private String email;
	@JsonProperty("primary")
	private String primary;
	@JsonProperty("verified")
	private String verified;
	@JsonProperty("visibility")
	private String visibility;

	public boolean isPrimary() {
		return this.primary.equals("true");
	}
}
