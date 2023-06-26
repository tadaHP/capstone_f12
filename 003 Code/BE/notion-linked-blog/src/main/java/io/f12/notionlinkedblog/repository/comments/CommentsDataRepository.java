package io.f12.notionlinkedblog.repository.comments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.comments.Comments;

@Repository
public interface CommentsDataRepository extends JpaRepository<Comments, Long> {
	@Query("SELECT c "
		+ "FROM Comments c join fetch c.post join fetch c.user "
		+ "WHERE c.post.id = :postId")
	List<Comments> findByPostId(@Param("postId") Long postId);

}
