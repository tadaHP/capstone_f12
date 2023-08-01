package io.f12.notionlinkedblog.repository.notion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.f12.notionlinkedblog.domain.notion.Notion;

public interface NotionDataRepository extends JpaRepository<Notion, Long> {

	@Query("SELECT n FROM Notion n where n.notionId = :pathValue")
	Optional<Notion> findByPathValue(@Param("pathValue") String key);

}
