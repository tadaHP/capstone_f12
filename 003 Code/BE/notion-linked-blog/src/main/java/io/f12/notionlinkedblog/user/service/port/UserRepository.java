package io.f12.notionlinkedblog.user.service.port;

import java.util.Optional;

import io.f12.notionlinkedblog.user.infrastructure.UserEntity;

public interface UserRepository {
	Optional<UserEntity> findUserById(Long id);

	Optional<UserEntity> findUserByIdForNotionAuthToken(Long id);

	Optional<UserEntity> findByEmail(final String email);

	Optional<UserEntity> findSeriesByUserId(Long userId);

	UserEntity save(UserEntity user);

	Optional<UserEntity> findById(Long id);

	Optional<UserEntity> findByOauthId(String id);

	void deleteById(Long id);

	void deleteAll();

}
