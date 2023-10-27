package io.f12.notionlinkedblog.user.service.port;

import java.util.List;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface UserPostQuerydslRepository {
	List<Long> findPostIdsByUserId(Long userId);

	List<PostEntity> findPostsByIdsForUserPost(List<Long> ids);
}
