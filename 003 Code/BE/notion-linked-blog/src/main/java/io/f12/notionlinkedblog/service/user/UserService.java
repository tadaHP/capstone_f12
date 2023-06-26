package io.f12.notionlinkedblog.service.user;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;

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

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.request.ProfileSuccessEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.domain.user.dto.response.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class UserService {

	private final UserDataRepository userDataRepository;
	private final PasswordEncoder passwordEncoder;

	public Long signupByEmail(UserSignupRequestDto requestDto) {
		checkEmailIsDuplicated(requestDto.getEmail());

		requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		User newUser = requestDto.toEntity();
		User savedUser = userDataRepository.save(newUser);

		return savedUser.getId();
	}

	@Transactional(readOnly = true)
	public UserSearchDto getUserInfo(Long id) {
		return userDataRepository.findUserById(id).orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
	}

	public void editBasicUserInfo(Long id, UserBasicInfoEditDto editDto) {
		User findUser = userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	public void editUserBlogTitleInfo(Long id, UserBlogTitleEditDto editDto) {
		User findUser = userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	public void editUserSocialInfo(Long id, UserSocialInfoEditDto editDto) {
		User findUser = userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
	}

	public ProfileSuccessEditDto editUserProfileImage(Long id, MultipartFile imageFile) throws IOException {
		User findUser = userDataRepository.findById(id)
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

	public void removeUser(Long id) {
		userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		userDataRepository.deleteById(id);
	}

	public File readImageFile(Long userId) {
		User editedUSer =
			userDataRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		if (editedUSer.getProfile() == null) {
			throw new IllegalArgumentException(IMAGE_NOT_EXIST);
		}

		return new File(editedUSer.getProfile());
	}

	//내부 사용 매서드
	private void checkEmailIsDuplicated(final String email) {
		boolean isPresent = userDataRepository.findByEmail(email).isPresent();
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