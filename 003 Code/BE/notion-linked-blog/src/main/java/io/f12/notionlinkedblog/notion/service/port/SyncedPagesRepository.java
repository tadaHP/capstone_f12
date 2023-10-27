package io.f12.notionlinkedblog.notion.service.port;

import java.util.List;
import java.util.Optional;

import io.f12.notionlinkedblog.notion.infrastructure.single.SyncedPagesEntity;

public interface SyncedPagesRepository {
	List<SyncedPagesEntity> findAll();

	SyncedPagesEntity save(SyncedPagesEntity syncedPages);

	Optional<SyncedPagesEntity> findByPageId(String pageId);
}
