package io.f12.notionlinkedblog.series.service.port;

import java.util.Optional;

import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;

public interface SeriesRepository {
	Optional<SeriesEntity> findSeriesById(Long seriesId);

	SeriesEntity save(SeriesEntity series);

	void delete(SeriesEntity series);

	void deleteAll();
}
