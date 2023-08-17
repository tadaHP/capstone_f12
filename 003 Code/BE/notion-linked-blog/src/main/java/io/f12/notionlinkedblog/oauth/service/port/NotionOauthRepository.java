package io.f12.notionlinkedblog.oauth.service.port;

import java.util.Optional;

import io.f12.notionlinkedblog.oauth.infrastructure.NotionOauthEntity;

public interface NotionOauthRepository {
	Optional<NotionOauthEntity> findNotionOauthByUserId(Long userId);

	void deleteNotionOauthByUserId(Long userId);

	NotionOauthEntity save(NotionOauthEntity notionOauth);
}
