package io.f12.notionlinkedblog.oauth.api.port;

import io.f12.notionlinkedblog.common.exceptions.exception.TokenAvailabilityFailureException;
import io.f12.notionlinkedblog.oauth.api.response.NotionOAuthLinkDto;

public interface NotionOauthService {

	public NotionOAuthLinkDto getNotionAuthSite();

	public String saveAccessToken(String code, Long userId) throws TokenAvailabilityFailureException;

	public void removeAccessToken(Long userId);
}
