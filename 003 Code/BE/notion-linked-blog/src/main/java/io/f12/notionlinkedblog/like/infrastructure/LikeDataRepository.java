package io.f12.notionlinkedblog.like.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.like.domain.dto.LikeSearchDto;
import io.f12.notionlinkedblog.like.service.port.LikeRepository;

@Repository
public interface LikeDataRepository extends JpaRepository<LikeEntity, Long>, LikeRepository {
	@Override
	@Query("SELECT NEW io.f12.notionlinkedblog.like.domain.dto.LikeSearchDto(u.id,p.id,l.id) "
		+ "FROM LikeEntity l join l.user u join l.post p "
		+ "WHERE u.id = :userId AND p.id = :postId")
	Optional<LikeSearchDto> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

	@Override
	@Modifying
	@Query("DELETE FROM LikeEntity l WHERE l.id = :id")
	void removeById(@Param("id") Long likeId);
}
