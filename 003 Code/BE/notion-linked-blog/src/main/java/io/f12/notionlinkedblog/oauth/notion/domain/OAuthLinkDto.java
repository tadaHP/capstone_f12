package io.f12.notionlinkedblog.oauth.notion.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OAuthLinkDto {
	String authUrl;
}
