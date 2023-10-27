package io.f12.notionlinkedblog.notion.service.port;

import java.util.List;
import java.util.Optional;

import io.f12.notionlinkedblog.notion.infrastructure.multi.SyncedSeriesEntity;

public interface SyncedSeriesRepository {
	List<SyncedSeriesEntity> findAll();

	SyncedSeriesEntity save(SyncedSeriesEntity entity);

	Optional<SyncedSeriesEntity> findByPageId(String id);
}
