package io.f12.notionlinkedblog.hashtag.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.f12.notionlinkedblog.hashtag.serivce.port.HashtagRepository;

public interface HashtagDataRepository extends JpaRepository<HashtagEntity, Long>, HashtagRepository {

	@Override
	@Query("SELECT h FROM HashtagEntity h LEFT JOIN FETCH h.post WHERE h.name = :name")
	Optional<HashtagEntity> findByName(@Param("name") String name);
}
