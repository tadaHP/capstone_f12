package io.f12.notionlinkedblog.oauth.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.f12.notionlinkedblog.oauth.service.port.NotionOauthRepository;

public interface NotionOauthDataRepository extends JpaRepository<NotionOauthEntity, Long>, NotionOauthRepository {
	Optional<NotionOauthEntity> findNotionOauthByUserId(Long userId);

	void deleteNotionOauthByUserId(Long userId);
}
