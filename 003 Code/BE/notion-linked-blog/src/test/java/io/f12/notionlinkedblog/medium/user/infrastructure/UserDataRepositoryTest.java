package io.f12.notionlinkedblog.medium.user.infrastructure;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import io.f12.notionlinkedblog.common.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
class UserDataRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EntityManager entityManager;

	private UserEntity userA;

	@DisplayName("유저 조회 테스트")
	@Nested
	class UserCheckTest {
		@BeforeEach
		void init() {
			UserEntity user1 = UserEntity.builder()
				.username("username1")
				.email("email1")
				.password("password1")
				.build();
			userA = userRepository.save(user1);
		}

		@AfterEach
		void clear() {
			userRepository.deleteAll();
			entityManager.createNativeQuery("ALTER SEQUENCE user_seq RESTART WITH 1").executeUpdate();
		}

		@DisplayName("정상 조회")
		@Test
		void checkSpecificUserDto() {
			//given

			UserEntity user2 = UserEntity.builder()
				.username("username2")
				.email("email2")
				.password("password2")
				.build();
			UserEntity userB = userRepository.save(user2);
			//when
			UserEntity findUserA = userRepository.findUserById(userA.getId()).orElseThrow(
				() -> new IllegalArgumentException(USER_NOT_EXIST));

			UserEntity findUserB = userRepository.findUserById(userB.getId()).orElseThrow(
				() -> new IllegalArgumentException(USER_NOT_EXIST));
			//then
			assertThat(findUserA).extracting("id").isEqualTo(userA.getId());
			assertThat(findUserA).extracting("username").isEqualTo(userA.getUsername());
			assertThat(findUserA).extracting("email").isEqualTo(userA.getEmail());

			assertThat(findUserB).extracting("id").isEqualTo(user2.getId());
			assertThat(findUserB).extracting("username").isEqualTo(user2.getUsername());
			assertThat(findUserB).extracting("email").isEqualTo(user2.getEmail());
		}

		@DisplayName("비정상 조회 - 없는 회원 조회시")
		@Test
		void checkUnUnifiedSpecificUserDto() {
			//given
			Long wrongId = userA.getId() + 100;
			//when, then
			Optional<UserEntity> user = userRepository.findUserById(wrongId);
			assertThatThrownBy(() -> {
				user.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
			}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(USER_NOT_EXIST);

		}

	}
}
