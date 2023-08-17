package io.f12.notionlinkedblog.user.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.user.api.response.ProfileSuccessEditDto;
import io.f12.notionlinkedblog.user.api.response.UserSearchDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class UserServiceImpl implements io.f12.notionlinkedblog.user.api.port.UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Long signupByEmail(UserSignupRequestDto requestDto) {
		checkEmailIsDuplicated(requestDto.getEmail());

		requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		UserEntity newUser = requestDto.toEntity();
		UserEntity savedUser = userRepository.save(newUser);

		return savedUser.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public UserSearchDto getUserInfo(Long id) {
		UserEntity user = userRepository.findUserById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		return UserSearchDto.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.introduction(user.getIntroduction())
			.blogTitle(user.getBlogTitle())
			.githubLink(user.getGithubLink())
			.instagramLink(user.getInstagramLink())
			.notionCertificate(user.getNotionOauth() != null)
			.build();
	}

	@Override
	public void editBasicUserInfo(Long id, UserBasicInfoEditDto editDto) {
		UserEntity findUser = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	@Override
	public void editUserBlogTitleInfo(Long id, UserBlogTitleEditDto editDto) {
		UserEntity findUser = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	@Override
	public void editUserSocialInfo(Long id, UserSocialInfoEditDto editDto) {
		UserEntity findUser = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	@Override
	public ProfileSuccessEditDto editUserProfileImage(Long id, MultipartFile imageFile) throws IOException {
		UserEntity findUser = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		String systemPath = System.getProperty("user.dir");
		String imageName = findUser.getUsername();
		String profileFileName = makeProfileFileName(findUser.getUsername());

		//기존 프로파일 제거
		if (findUser.getProfile() != null) {
			try {
				Files.delete(Path.of(findUser.getProfile()));
			} catch (Exception e) {
				log.warn("파일이 존재하지 않습니다: {}", e.getMessage());
			}
			findUser.setProfile(null);
		}
		//새로운 프로파일 등록
		String fullPath = getSavedDirectory(imageFile, systemPath, profileFileName);
		imageFile.transferTo(new File(fullPath));
		String newName = profileFileName + "." + StringUtils.getFilenameExtension(imageFile.getOriginalFilename());

		findUser.setProfile(systemPath + "/" + newName);
		return ProfileSuccessEditDto.builder()
			.requestLink(Endpoint.Api.REQUEST_PROFILE_IMAGE + id)
			.build();
	}

	@Override
	public void removeUserProfileImage(Long id) {
		UserEntity findUser = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		try {
			Files.delete(Path.of(findUser.getProfile()));
		} catch (Exception e) {
			log.warn("파일이 존재하지 않습니다: {}", e.getMessage());
		}
		findUser.setProfile(null);
	}

	@Override
	public void removeUser(Long id) {
		userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		userRepository.deleteById(id);
	}

	@Override
	public File readImageFile(Long userId) {
		UserEntity editedUSer =
			userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		if (editedUSer.getProfile() == null) {
			return null;
		}

		return new File(editedUSer.getProfile());
	}

	//내부 사용 매서드
	private void checkEmailIsDuplicated(final String email) {
		boolean isPresent = userRepository.findByEmail(email).isPresent();
		if (isPresent) {
			throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
		}
	}

	private String makeProfileFileName(String username) {
		StringBuilder sb = new StringBuilder();
		Date now = new Date();
		SimpleDateFormat savedDataFormat = new SimpleDateFormat("yyyy_MM");
		sb.append(username);
		sb.append(savedDataFormat.format(now));
		return sb.toString();
	}

	private String getSavedDirectory(MultipartFile multipartFile, String systemPath, String fileName) {
		return
			systemPath + "\\" + fileName + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
	}
}