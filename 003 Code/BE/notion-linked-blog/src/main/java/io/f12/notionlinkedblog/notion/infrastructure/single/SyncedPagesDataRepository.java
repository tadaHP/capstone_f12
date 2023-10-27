package io.f12.notionlinkedblog.notion.infrastructure.single;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;

public interface SyncedPagesDataRepository extends JpaRepository<SyncedPagesEntity, Long>, SyncedPagesRepository {

	@Query("SELECT s from SyncedPagesEntity s left join fetch s.post left join fetch s.user")
	List<SyncedPagesEntity> findAll();

	@Query("SELECT s FROM SyncedPagesEntity s WHERE s.pageId = :pageId")
	Optional<SyncedPagesEntity> findByPageId(@Param("pageId") String pageId);
}
