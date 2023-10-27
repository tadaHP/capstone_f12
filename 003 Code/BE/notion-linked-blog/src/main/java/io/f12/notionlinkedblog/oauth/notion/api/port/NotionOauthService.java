package io.f12.notionlinkedblog.oauth.notion.api.port;

import io.f12.notionlinkedblog.oauth.notion.domain.OAuthLinkDto;
import io.f12.notionlinkedblog.oauth.notion.exception.TokenAvailabilityFailureException;

public interface NotionOauthService {

	public OAuthLinkDto getNotionAuthSite();

	public String saveAccessToken(String code, Long userId) throws TokenAvailabilityFailureException;

	public void removeAccessToken(Long userId);
}
