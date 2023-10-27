package io.f12.notionlinkedblog.hashtag.serivce.port;

import java.util.Optional;

import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;

public interface HashtagRepository {
	HashtagEntity save(HashtagEntity entity);

	void delete(HashtagEntity hashtag);

	void deleteAll();

	Optional<HashtagEntity> findByName(String name);
}
