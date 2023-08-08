package io.f12.notionlinkedblog.repository.series;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.series.Series;

@Repository
public interface SeriesDataRepository extends JpaRepository<Series, Long> {

	@Query("SELECT s FROM Series s LEFT JOIN FETCH s.post WHERE s.id = :seriesId")
	Optional<Series> findSeriesById(@Param("seriesId") Long seriesId);

}
