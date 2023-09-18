package io.f12.notionlinkedblog.oauth.notion.service.port;

import java.util.Optional;

import io.f12.notionlinkedblog.oauth.notion.infrastructure.NotionOauthEntity;

public interface NotionOauthRepository {
	Optional<NotionOauthEntity> findNotionOauthByUserId(Long userId);

	void deleteNotionOauthByUserId(Long userId);

	NotionOauthEntity save(NotionOauthEntity notionOauth);
}
