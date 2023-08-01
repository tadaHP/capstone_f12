package io.f12.notionlinkedblog.repository.oauth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.f12.notionlinkedblog.domain.oauth.NotionOauth;

public interface NotionOauthDataRepository extends JpaRepository<NotionOauth, Long> {
	Optional<NotionOauth> findNotionOauthByUserId(Long userId);

	void deleteNotionOauthByUserId(Long userId);
}
