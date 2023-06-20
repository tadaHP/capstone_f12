package io.f12.notionlinkedblog.service.user;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.f12.notionlinkedblog.domain.dummy.DummyObject;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserEditDto;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests extends DummyObject {

	@InjectMocks
	private UserService userService;
	@Mock
	private UserDataRepository userDataRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("유저 이메일 기반 회원가입")
	@Nested
	class UserSignupByEmailTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("이메일로 회원가입 성공")
			@Test
			void findByEmail() {
				//given
				UserSignupRequestDto newUserDto = UserSignupRequestDto.builder()
					.email("test@gmail.com")
					.username("test")
					.password("1234")
					.build();

				User mockUser = newMockUser(1L, "test", "test@gmail.com");

				// stub 1
				given(userDataRepository.findByEmail(any())).willReturn(Optional.empty());

				// stub 2
				given(userDataRepository.save(any())).willReturn(mockUser);

				//when
				Long id = userService.signupByEmail(newUserDto);

				//then
				assertThat(id).isEqualTo(mockUser.getId());
			}
		}

		@DisplayName("비정상 케이스")
		@Nested
		class FailureCase {
			@DisplayName("이메일 중복으로 인한 회원가입 실패")
			@Test
			void findByEmail() {
				//given
				UserSignupRequestDto existUserDto = UserSignupRequestDto.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();
				User existUser = existUserDto.toEntity();
				userDataRepository.save(existUser);

				UserSignupRequestDto newUserDto = UserSignupRequestDto.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();
				User newUser = newUserDto.toEntity();
				given(userDataRepository.findByEmail(newUser.getEmail())).willReturn(Optional.of(existUser));

				//then
				assertThatThrownBy(() -> userService.signupByEmail(newUserDto)).isInstanceOf(
					IllegalArgumentException.class);
			}
		}
	}

	@DisplayName("유저 변경, 조회 및 삭제")
	@Nested
	class UserInfoTest {

		@DisplayName("유저 조회 테스트")
		@Nested
		class UserCheckTest {
			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("id로 조회 케이스")
				@Test
				void getUserInfoTest() {
					//given
					Long fakeIdForA = 1L;
					Long fakeIdForB = 2L;
					User userA = User.builder()
						.id(fakeIdForA)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();
					User userB = User.builder()
						.id(fakeIdForB)
						.email("test2@gmail.com")
						.username("username2")
						.password("password2")
						.build();

					//Mock
					UserSearchDto mockUserSearchDtoA = UserSearchDto.builder()
						.id(fakeIdForA)
						.email("test1@gmail.com")
						.username("username1")
						.build();
					UserSearchDto mockUserSearchDtoB = UserSearchDto.builder()
						.id(fakeIdForB)
						.email("test2@gmail.com")
						.username("username2")
						.build();
					given(userDataRepository.findUserById(fakeIdForA))
						.willReturn(Optional.ofNullable(mockUserSearchDtoA));
					given(userDataRepository.findUserById(fakeIdForB))
						.willReturn(Optional.ofNullable(mockUserSearchDtoB));
					//when
					UserSearchDto userInfoA = userService.getUserInfo(fakeIdForA);
					UserSearchDto userInfoB = userService.getUserInfo(fakeIdForB);
					//then
					assertThat(userInfoA).extracting("id").isEqualTo(userA.getId());
					assertThat(userInfoA).extracting("username").isEqualTo(userA.getUsername());
					assertThat(userInfoA).extracting("email").isEqualTo(userA.getEmail());

					assertThat(userInfoB).extracting("id").isEqualTo(userB.getId());
					assertThat(userInfoB).extracting("username").isEqualTo(userB.getUsername());
					assertThat(userInfoB).extracting("email").isEqualTo(userB.getEmail());
				}
			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {
				@DisplayName("존재하지 않는 회원")
				@Test
				void getUnUnifiedUserInfoTest() {
					//given
					Long fakeId = 1L;
					//mock
					given(userDataRepository.findUserById(fakeId))
						.willReturn(Optional.empty());
					//when, then
					assertThatThrownBy(() -> userService.getUserInfo(fakeId))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining(USER_NOT_EXIST);
				}
			}
		}

		@DisplayName("유저 변경 테스트")
		@Nested
		class UserEditTest {
			@DisplayName("정상 케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 정보 변경")
				@Test
				void editUserInfoTest() {
					//given
					User userA = User.builder()
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();
					UserEditDto editDto = UserEditDto.builder().username("changed").build();

					//stub
					given(userDataRepository.findById(any()))
						.willReturn(Optional.of(userA));
					//when
					userService.editUserInfo(1L, editDto);
					//then
					assertThat(userA.getUsername()).isEqualTo(editDto.getUsername());
				}
			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {
				@DisplayName("해당 유저가 존재하지 않을때")
				@Test
				void editUnUnifiedUserInfoTest() {
					//given
					UserEditDto editedUser = UserEditDto.builder().username("changedUsername").build();

					//mock
					given(userDataRepository.findById(any()))
						.willReturn(Optional.empty());
					//when, then
					assertThatThrownBy(() -> userService
						.editUserInfo(-1L, editedUser))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining(USER_NOT_EXIST);
				}
			}
		}

		@DisplayName("유저 삭제 테스트")
		@Nested
		class UserDeleteTest {
			@DisplayName("성공케이스")
			@Nested
			class SuccessCase {
				@DisplayName("유저 삭제")
				@Test
				void deleteUserTest() {
					//given
					Long removedUserId = 1L;
					User removedUser = User.builder()
						.id(removedUserId)
						.email("changed@gmail.com")
						.username("changedUsername")
						.password("changedPassword")
						.build();
					//mock
					given(userDataRepository.findById(removedUserId))
						.willReturn(Optional.of(removedUser));
					//when
					userService.removeUser(removedUserId);
				}

			}

			@DisplayName("실패 케이스")
			@Nested
			class FailureCase {

				@DisplayName("해당유저가 존재하지 않을때")
				@Test
				void deleteUnUnifiedUserTest() {
					//given
					Long removedUserId = 1L;
					//when, then
					assertThatThrownBy(() -> userService.removeUser(removedUserId)).isInstanceOf(
							IllegalArgumentException.class)
						.hasMessageContaining(USER_NOT_EXIST);

				}

			}

		}

	}
}
