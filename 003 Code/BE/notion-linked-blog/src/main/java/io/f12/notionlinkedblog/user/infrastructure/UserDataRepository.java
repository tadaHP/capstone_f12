package io.f12.notionlinkedblog.user.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.f12.notionlinkedblog.user.service.port.UserRepository;

public interface UserDataRepository extends JpaRepository<UserEntity, Long>, UserRepository {
	@Override
	@Query("SELECT u FROM UserEntity u left join fetch u.notionOauth WHERE u.id = :id")
	Optional<UserEntity> findUserById(@Param("id") Long id);

	@Override
	@Query("SELECT u FROM UserEntity u left join fetch u.notionOauth where u.id= :id")
	Optional<UserEntity> findUserByIdForNotionAuthToken(@Param("id") Long id);

	@Override
	Optional<UserEntity> findByEmail(final String email);

	@Override
	@Query("SELECT u "
		+ "FROM UserEntity u "
		+ "LEFT JOIN FETCH u.series "
		+ "WHERE u.id = :userId")
	Optional<UserEntity> findSeriesByUserId(@Param("userId") Long userId);
}
