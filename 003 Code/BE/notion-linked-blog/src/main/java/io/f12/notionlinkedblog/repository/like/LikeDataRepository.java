package io.f12.notionlinkedblog.repository.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.likes.dto.LikeSearchDto;

@Repository
public interface LikeDataRepository extends JpaRepository<Like, Long> {
	@Query("SELECT NEW io.f12.notionlinkedblog.domain.likes.dto.LikeSearchDto(u.id,p.id,l.id) "
		+ "FROM Like l join l.user u join l.post p "
		+ "WHERE u.id = :userId AND p.id = :postId")
	Optional<LikeSearchDto> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

	@Modifying
	@Query("DELETE FROM Like l WHERE l.id = :id")
	void removeById(@Param("id") Long likeId);
}
