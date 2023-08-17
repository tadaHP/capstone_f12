package io.f12.notionlinkedblog.post.service.port;

import java.util.List;
import java.util.Optional;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface PostRepository {
	Optional<PostEntity> findById(Long id);

	List<PostEntity> findByPostIdForTrend();

	String findThumbnailPathWithName(String name);

	PostEntity save(PostEntity post);

	void deleteById(Long id);

	void deleteAll();
}
