package io.f12.notionlinkedblog.hashtag.serivce.port;

import java.util.List;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface PostHashtagRepository {

	List<PostEntity> findByIdForHashtag(Long id);
}
