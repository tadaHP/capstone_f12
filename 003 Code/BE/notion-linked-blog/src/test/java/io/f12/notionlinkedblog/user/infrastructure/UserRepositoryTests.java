package io.f12.notionlinkedblog.user.infrastructure;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.f12.notionlinkedblog.common.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@ActiveProfiles("test")
@Import(TestQuerydslConfiguration.class)
@DataJpaTest
class UserRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@DisplayName("유저 조회 API")
	@Nested
	class UserSelectingTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("이메일로 조회 성공")
			@Test
			void findByEmail() {
				//given
				UserEntity testUser = UserEntity.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();

				//when
				Optional<UserEntity> foundUser = userRepository.findByEmail(testUser.getEmail());

				//then
				assertThat(foundUser.isEmpty()).isTrue();
			}
		}

		@DisplayName("비정상 케이스")
		@Nested
		class FailureCase {
			@DisplayName("이메일 중복으로 인한 조회 실패")
			@Test
			void findByEmail() {
				//given
				UserEntity existUser = UserEntity.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();
				userRepository.save(existUser);

				UserEntity newUser = UserEntity.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();

				//when
				Optional<UserEntity> foundUser = userRepository.findByEmail(newUser.getEmail());

				//then
				assertThat(foundUser.isPresent()).isTrue();
			}
		}
	}
}
