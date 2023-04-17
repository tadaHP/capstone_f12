package io.f12.notionlinkedblog.repository.user;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;

@DataJpaTest
class UserDataRepositoryTest {

	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private EntityManager entityManager;

	@DisplayName("유저 조회 테스트")
	@Nested
	class UserCheckTest {
		@BeforeEach
		void clear() {
			userDataRepository.deleteAll();
			entityManager.createNativeQuery("ALTER SEQUENCE user_seq RESTART WITH 1").executeUpdate();
		}

		@DisplayName("정상 조회")
		@Test
		void checkSpecificUserDto() {
			//given
			User user1 = User.builder()
				.username("username1")
				.email("email1")
				.password("password1")
				.build();
			User user2 = User.builder()
				.username("username2")
				.email("email2")
				.password("password2")
				.build();
			userDataRepository.save(user1);
			userDataRepository.save(user2);
			//when
			long id1 = 1L;
			Optional<UserSearchDto> userA = userDataRepository.findUserById(id1);
			UserSearchDto findUserA = userA.orElseThrow(
				() -> new IllegalArgumentException("Wrong MemberId: " + id1));
			long id2 = 2L;
			Optional<UserSearchDto> userB = userDataRepository.findUserById(id2);
			UserSearchDto findUserB = userB.orElseThrow(
				() -> new IllegalArgumentException("Wrong MemberId: " + id2));
			//then
			assertThat(findUserA).extracting("id").isEqualTo(user1.getId());
			assertThat(findUserA).extracting("username").isEqualTo(user1.getUsername());
			assertThat(findUserA).extracting("email").isEqualTo(user1.getEmail());

			assertThat(findUserB).extracting("id").isEqualTo(user2.getId());
			assertThat(findUserB).extracting("username").isEqualTo(user2.getUsername());
			assertThat(findUserB).extracting("email").isEqualTo(user2.getEmail());
		}

		@DisplayName("비정상 조회 - 없는 회원 조회시")
		@Test
		void checkUnUnifiedSpecificUserDto() {
			//given
			User user1 = User.builder()
				.username("username1")
				.email("email1")
				.password("password1")
				.profile("profile1")
				.introduction("intro1")
				.blogTitle("title1")
				.githubLink("git1")
				.instagramLink("insta1")
				.build();
			userDataRepository.save(user1);
			long id = 3L;
			String errorMessage = "Wrong MemberId: ";
			//when, then
			Optional<UserSearchDto> userA = userDataRepository.findUserById(id);
			assertThatThrownBy(() -> {
				userA.orElseThrow(() -> new IllegalArgumentException(errorMessage + id));
			}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(errorMessage)
				.hasMessageContaining(String.valueOf(id));

		}

	}
}
