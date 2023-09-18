package io.f12.notionlinkedblog.component.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Service
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotionOAuthComponent {
	@Value("${external.notion.real.notionClientId}")
	private String clientId;
	@Value("${external.notion.real.notionClientSecret}")
	private String clientSecret;
	@Value("${external.notion.real.authorizeUrl}")
	private String authUrl;
	@Value("${external.notion.real.redirectUrl}")
	private String redirectUrl;
}
