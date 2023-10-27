package io.f12.notionlinkedblog.series.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.series.service.port.SeriesRepository;

@Repository
public interface SeriesDataRepository extends JpaRepository<SeriesEntity, Long>, SeriesRepository {

	@Query("SELECT s FROM SeriesEntity s LEFT JOIN FETCH s.post WHERE s.id = :seriesId")
	Optional<SeriesEntity> findSeriesById(@Param("seriesId") Long seriesId);
}
