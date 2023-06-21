package io.f12.notionlinkedblog.repository.series;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.series.Series;

@Repository
public interface SeriesDataRepository extends JpaRepository<Series, Long> {

	@Query("SELECT NEW io.f12.notionlinkedblog.domain.post.dto.PostSearchDto("
		+ "p.id, p.title, p.content, p.viewCount, p.likes.size,"
		+ "p.storedThumbnailPath, p.description, p.createdAt,"
		+ "p.comments.size, p.user.username) "
		+ "FROM Post p WHERE p.isPublic = true AND p.series.id = :seriesId "
		+ "order by p.createdAt desc")
	List<PostSearchDto> findPostDtosBySeriesIdOrderByCreatedAtDesc(@Param("seriesId") Long seriesId, Pageable pageable);

	@Query("SELECT NEW io.f12.notionlinkedblog.domain.post.dto.PostSearchDto("
		+ "p.id, p.title, p.content, p.viewCount, p.likes.size,"
		+ "p.storedThumbnailPath, p.description, p.createdAt,"
		+ "p.comments.size, p.user.username) "
		+ "FROM Post p WHERE p.isPublic = true AND p.series.id = :seriesId "
		+ "order by p.createdAt asc")
	List<PostSearchDto> findPostDtosBySeriesIdOrderByCreatedAtAsc(@Param("seriesId") Long seriesId, Pageable pageable);

}
