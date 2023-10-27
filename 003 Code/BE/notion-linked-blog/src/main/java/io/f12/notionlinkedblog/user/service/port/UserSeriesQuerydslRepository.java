package io.f12.notionlinkedblog.user.service.port;

import java.util.List;

import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;

public interface UserSeriesQuerydslRepository {
	List<Long> findIdByUserId(Long userId);

	List<SeriesEntity> findByUserIds(List<Long> ids);

}
