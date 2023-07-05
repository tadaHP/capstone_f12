package io.f12.notionlinkedblog.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.response.UserSearchDto;

public interface UserDataRepository extends JpaRepository<User, Long> {

	@Query(
		"SELECT new io.f12.notionlinkedblog.domain.user.dto.response.UserSearchDto(u.id,"
			+ "u.username,u.email,u.introduction,u.blogTitle,u.githubLink,u.instagramLink) "
			+ "FROM User u "
			+ "WHERE u.id = :id")
	Optional<UserSearchDto> findUserById(@Param("id") Long id);

	Optional<User> findByEmail(final String email);
}
