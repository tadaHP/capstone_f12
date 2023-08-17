package io.f12.notionlinkedblog.oauth.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionOAuthLinkDto {
	String authUrl;
}
