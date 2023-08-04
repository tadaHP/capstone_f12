package io.f12.notionlinkedblog.repository.syncedpages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.f12.notionlinkedblog.domain.notion.SyncedPages;

public interface SyncedPagesDataRepository extends JpaRepository<SyncedPages, Long> {

	@Query("SELECT s from SyncedPages s left join fetch s.post left join fetch s.user")
	List<SyncedPages> findAll();
}
