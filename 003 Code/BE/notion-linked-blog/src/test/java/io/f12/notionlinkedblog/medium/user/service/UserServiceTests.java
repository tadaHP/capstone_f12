package io.f12.notionlinkedblog.medium.user.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.amazonaws.services.s3.AmazonS3Client;

import io.f12.notionlinkedblog.common.domain.AwsBucket;
import io.f12.notionlinkedblog.medium.dummy.DummyObject;
import io.f12.notionlinkedblog.user.api.response.ProfileImageLinkDto;
import io.f12.notionlinkedblog.user.api.response.UserSearchDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.UserServiceImpl;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests extends DummyObject {

	@InjectMocks
	private UserServiceImpl userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private AwsBucket awsBucket;
	@Mock
	private AmazonS3Client amazonS3Client;

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

				UserEntity mockUser = newMockUser(1L, "test", "test@gmail.com");

				// stub 1
				given(userRepository.findByEmail(any())).willReturn(Optional.empty());

				// stub 2
				given(userRepository.save(any())).willReturn(mockUser);

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
				UserEntity existUser = existUserDto.toEntity();
				userRepository.save(existUser);

				UserSignupRequestDto newUserDto = UserSignupRequestDto.builder()
					.email("test@gmail.com")
					.username("test")
					.password("password")
					.build();
				UserEntity newUser = newUserDto.toEntity();
				given(userRepository.findByEmail(newUser.getEmail())).willReturn(Optional.of(existUser));

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
					UserEntity userA = UserEntity.builder()
						.id(fakeIdForA)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();
					UserEntity userB = UserEntity.builder()
						.id(fakeIdForB)
						.email("test2@gmail.com")
						.username("username2")
						.password("password2")
						.build();

					//Mock
					UserEntity mockUserSearchDtoA = UserEntity.builder()
						.id(fakeIdForA)
						.email("test1@gmail.com")
						.username("username1")
						.build();
					UserEntity mockUserSearchDtoB = UserEntity.builder()
						.id(fakeIdForB)
						.email("test2@gmail.com")
						.username("username2")
						.build();
					given(userRepository.findUserById(fakeIdForA))
						.willReturn(Optional.ofNullable(mockUserSearchDtoA));
					given(userRepository.findUserById(fakeIdForB))
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
					given(userRepository.findUserById(fakeId))
						.willReturn(Optional.empty());
					//when, then
					assertThatThrownBy(() -> userService.getUserInfo(fakeId))
						.isInstanceOf(IllegalArgumentException.class)
						.hasMessageContaining(USER_NOT_EXIST);
				}
			}
		}

		@DisplayName("유저 수정 테스트")
		@Nested
		class UserEditTest {
			@DisplayName("기본 정보 수정 테스트")
			@Nested
			class BasicEditTest {
				@DisplayName("성공 케이스")
				@Test
				void successCase() {
					//given
					UserEntity userA = UserEntity.builder()
						.id(1L)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();

					UserBasicInfoEditDto editDto = UserBasicInfoEditDto.builder()
						.username("changed")
						.introduction("changed")
						.build();

					//stub
					given(userRepository.findById(1L))
						.willReturn(Optional.of(userA));
					//when
					userService.editBasicUserInfo(1L, editDto);
					//then
					assertThat(userA.getUsername()).isEqualTo(editDto.getUsername());
					assertThat(userA.getIntroduction()).isEqualTo(editDto.getIntroduction());
				}

			}

			@DisplayName("블로그 제목 수정 테스트")
			@Nested
			class BlogTitleEditTest {
				@DisplayName("성공 케이스")
				@Test
				void successCase() {
					//given
					UserEntity userA = UserEntity.builder()
						.id(1L)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();

					UserBlogTitleEditDto editDto = UserBlogTitleEditDto.builder()
						.blogTitle("changedTitle")
						.build();

					//stub
					given(userRepository.findById(1L))
						.willReturn(Optional.of(userA));
					//when
					userService.editUserBlogTitleInfo(1L, editDto);
					//then
					assertThat(userA.getBlogTitle()).isEqualTo(editDto.getBlogTitle());
				}
			}

			@DisplayName("SNS 정보 수정 테스트")
			@Nested
			class SocialEditTest {
				@DisplayName("성공 케이스")
				@Test
				void successCase() {
					//given
					UserEntity userA = UserEntity.builder()
						.id(1L)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.build();
					UserSocialInfoEditDto editDto = UserSocialInfoEditDto.builder()
						.githubLink("changedGitLink")
						.instagramLink("changedInstaLink")
						.build();

					//stub
					given(userRepository.findById(1L))
						.willReturn(Optional.of(userA));
					//when
					userService.editUserSocialInfo(1L, editDto);
					//then
					assertThat(userA.getGithubLink()).isEqualTo(editDto.getGithubLink());
					assertThat(userA.getInstagramLink()).isEqualTo(editDto.getInstagramLink());
				}
			}

			@DisplayName("프로파일 이미지 수정 테스트")
			@Nested
			class ProfileEditTest {

				@DisplayName("프로파일 삭제")
				@Test
				void removeProfileImage() throws IOException {
					//given
					String originalPath = Paths
						.get("src", "test", "resources", "static", "images", "test.jpg")
						.toFile()
						.getAbsolutePath();
					String newPath = Paths
						.get("src", "test", "resources", "static", "images", "new.jpg")
						.toFile()
						.getAbsolutePath();

					File originFile = new File(originalPath);
					File newFile = new File(newPath);

					Files.copy(originFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

					UserEntity userA = UserEntity.builder()
						.id(1L)
						.email("test1@gmail.com")
						.username("username1")
						.password("password1")
						.profile(newPath)
						.build();

					//stub
					given(userRepository.findById(1L))
						.willReturn(Optional.of(userA));
					//when
					//then
					userService.removeUserProfileImage(1L);
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
					UserEntity removedUser = UserEntity.builder()
						.id(removedUserId)
						.email("changed@gmail.com")
						.username("changedUsername")
						.password("changedPassword")
						.build();
					//mock
					given(userRepository.findById(removedUserId))
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

		@DisplayName("유저 프로필 조회 테스트")
		@Nested
		class UserProfileLookUp {
			@DisplayName("프로파일 조회")
			@Test
			void getProfile() {
				//given
				String profileImage = "test";
				UserEntity userA = UserEntity.builder()
					.id(1L)
					.email("test1@gmail.com")
					.username("username1")
					.password("password1")
					.profile("aws")
					.profile(profileImage)
					.build();
				String testSuccess = "testSuccess";
				//stub
				given(userRepository.findById(1L))
					.willReturn(Optional.of(userA));
				given(awsBucket.makeFileUrl(profileImage))
					.willReturn(testSuccess);
				//when
				ProfileImageLinkDto profileImageUrl = userService.getProfileImageUrl(1L);
				//then
				assertThat(profileImageUrl.getImageUrl()).isEqualTo(testSuccess);
			}

			@DisplayName("기본 프로파일 조회")
			@Test
			void getDefaultProfile() {
				//given
				UserEntity userA = UserEntity.builder()
					.id(1L)
					.email("test1@gmail.com")
					.username("username1")
					.password("password1")
					.build();
				String testSuccess = "testSuccess";
				//stub
				given(userRepository.findById(1L))
					.willReturn(Optional.of(userA));
				given(awsBucket.makeFileUrl("profile/DefaultProfile.png"))
					.willReturn(testSuccess);
				//when
				ProfileImageLinkDto profileImageUrl = userService.getProfileImageUrl(1L);
				//then
				assertThat(profileImageUrl.getImageUrl()).isEqualTo(testSuccess);
			}

		}
	}
}
