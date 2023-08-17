package io.f12.notionlinkedblog.comments.service.port;

import java.util.List;
import java.util.Optional;

import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;

public interface CommentsRepository {
	List<CommentsEntity> findByPostId(Long postId);

	Optional<CommentsEntity> findById(Long commentsId);

	CommentsEntity save(CommentsEntity comments);

	void deleteById(Long commentId);

	void deleteAll();
}
