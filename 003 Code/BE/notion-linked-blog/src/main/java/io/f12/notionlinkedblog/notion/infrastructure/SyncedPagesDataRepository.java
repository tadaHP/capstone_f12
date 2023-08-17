package io.f12.notionlinkedblog.notion.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;

public interface SyncedPagesDataRepository extends JpaRepository<SyncedPagesEntity, Long>, SyncedPagesRepository {

	@Query("SELECT s from SyncedPagesEntity s left join fetch s.post left join fetch s.user")
	List<SyncedPagesEntity> findAll();
}
