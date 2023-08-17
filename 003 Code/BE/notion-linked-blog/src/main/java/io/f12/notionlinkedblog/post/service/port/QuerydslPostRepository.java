package io.f12.notionlinkedblog.post.service.port;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface QuerydslPostRepository {
	List<Long> findPostIdsByTitle(String title, Pageable pageable);

	List<Long> findPostIdsByContent(String content, Pageable pageable);

	List<Long> findLatestPostIdsByCreatedAtDesc(Pageable pageable);

	List<Long> findPopularityPostIdsByViewCountAtDesc(Pageable pageable);

	List<PostEntity> findByPostIdsJoinWithUserAndLikeOrderByLatest(List<Long> ids);

	List<PostEntity> findByPostIdsJoinWithUserAndLikeOrderByTrend(List<Long> ids);

	List<Long> findIdsBySeriesIdDesc(Long seriesId, Pageable pageable);

	List<Long> findIdsBySeriesIdAsc(Long seriesId, Pageable pageable);

	List<PostEntity> findByIdsJoinWithSeries(List<Long> ids);

}
