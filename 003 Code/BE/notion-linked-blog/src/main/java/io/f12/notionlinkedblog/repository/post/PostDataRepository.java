package io.f12.notionlinkedblog.repository.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.post.Post;

@Repository
public interface PostDataRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

	@Query("SELECT p "
		+ "FROM Post p join fetch p.user left join fetch p.likes "
		+ "WHERE p.id = :id")
	Optional<Post> findById(@Param("id") Long id);

	@Query("SELECT DISTINCT p FROM Post p left join fetch p.likes")
	List<Post> findByPostIdForTrend();
}
