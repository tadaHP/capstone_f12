package io.f12.notionlinkedblog.repository.post;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.post.Post;

@Repository
public interface PostDataRepository extends JpaRepository<Post, Long> {
	@Query("SELECT p "
		+ "FROM Post p join fetch p.user u "
		+ "WHERE p.id = :id")
	Optional<Post> findById(@Param("id") Long id);

	@Query("SELECT p "
		+ "FROM Post p join fetch p.user u "
		+ "WHERE p.title LIKE %:name%")
	Slice<Post> findByTitle(@Param("name") String name, Pageable pageable);

	@Query("SELECT p "
		+ "FROM Post p join p.user u "
		+ "WHERE p.content LIKE %:content%")
	Slice<Post> findByContent(@Param("content") String content, Pageable pageable);

}
