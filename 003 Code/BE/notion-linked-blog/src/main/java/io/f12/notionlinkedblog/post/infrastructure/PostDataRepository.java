package io.f12.notionlinkedblog.post.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.post.service.port.PostRepository;

@Repository
public interface PostDataRepository extends JpaRepository<PostEntity, Long>, PostRepository {

	@Override
	@Query("SELECT p "
		+ "FROM PostEntity p join fetch p.user left join fetch p.likes "
		+ "WHERE p.id = :id")
	Optional<PostEntity> findById(@Param("id") Long id);

	@Override
	@Query("SELECT DISTINCT p FROM PostEntity p left join fetch p.likes")
	List<PostEntity> findByPostIdForTrend();

	@Override
	@Query("SELECT DISTINCT p.storedThumbnailPath FROM PostEntity p WHERE p.thumbnailName  = :thumbnailName")
	String findThumbnailPathWithName(@Param("thumbnailName") String name);
}
