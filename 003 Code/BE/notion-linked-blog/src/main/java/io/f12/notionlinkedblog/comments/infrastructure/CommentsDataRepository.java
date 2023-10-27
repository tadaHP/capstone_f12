package io.f12.notionlinkedblog.comments.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.comments.service.port.CommentsRepository;

@Repository
public interface CommentsDataRepository extends JpaRepository<CommentsEntity, Long>, CommentsRepository {

	@Override
	@Query("SELECT c "
		+ "FROM CommentsEntity c join fetch c.post join fetch c.user "
		+ "WHERE c.post.id = :postId")
	List<CommentsEntity> findByPostId(@Param("postId") Long postId);

	@Override
	@Query("SELECT c "
		+ "FROM CommentsEntity c left join fetch c.user "
		+ "WHERE c.id = :commentId")
	Optional<CommentsEntity> findById(@Param("commentId") Long commentId);

}
