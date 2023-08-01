package io.f12.notionlinkedblog.repository.post;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.f12.notionlinkedblog.domain.post.Post;

public interface PostRepositoryCustom {
	List<Long> findPostIdsByTitle(String title, Pageable pageable);

	List<Long> findPostIdsByContent(String content, Pageable pageable);

	List<Long> findLatestPostIdsByCreatedAtDesc(Pageable pageable);

	List<Long> findPopularityPostIdsByViewCountAtDesc(Pageable pageable);

	List<Post> findByIds(List<Long> ids);

	Post findWithNotion(Long id);

}
