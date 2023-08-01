package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;
import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static org.springframework.http.MediaType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.common.CommonErrorResponse;
import io.f12.notionlinkedblog.domain.user.dto.request.ProfileSuccessEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.domain.user.dto.response.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupResponseDto;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User", description = "사용자 API")
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.USER)
@RestController
@Slf4j
public class UserApiController {

	private final UserService userService;

	@PostMapping("/email/signup")
	@Operation(summary = "email 을 이용한 회원가입")
	public ResponseEntity<UserSignupResponseDto> signupByEmail(
		@RequestBody @Validated UserSignupRequestDto requestDto, BindingResult bindingResult, HttpSession httpSession) {
		verifyEmailIsVerifiedElseThrowIllegalStateException(httpSession);

		Long savedId = userService.signupByEmail(requestDto);
		UserSignupResponseDto responseDto = UserSignupResponseDto.builder().id(savedId).build();
		httpSession.removeAttribute(emailVerifiedAttr);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	private void verifyEmailIsVerifiedElseThrowIllegalStateException(HttpSession session) {
		String isVerified = (String)session.getAttribute(emailVerifiedAttr);
		if (isVerified == null) {
			throw new IllegalStateException("이메일 검증이 되지 않았습니다.");
		}
	}

	@GetMapping(value = "/{id}")
	@Operation(summary = "회원 정보 조회", description = "id에 해당하는 사용자의 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "유저 정보 조회 성공",
		content = @Content(mediaType = APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = UserSearchDto.class)))
	public UserSearchDto getUserInfo(@PathVariable Long id) {
		return userService.getUserInfo(id);
	}

	@PutMapping(value = "/basic/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원 기본 정보 변경", description = "id에 해당하는 사용자의 이름(username), 본인 설명(introduction)을 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보변경 성공")
	})
	public void editBasicUserInfo(@PathVariable Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated UserBasicInfoEditDto editDto) {
		checkSameUser(id, loginUser);
		userService.editBasicUserInfo(id, editDto);
	}

	@PutMapping(value = "/blogTitle/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원 BlogTitle 정보 변경", description = "id에 해당하는 사용자의 BlogTitle 을 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보 변경 성공")
	})
	public void editUserBlogTitleInfo(@PathVariable Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated UserBlogTitleEditDto editDto) {
		checkSameUser(id, loginUser);
		userService.editUserBlogTitleInfo(id, editDto);
	}

	@PutMapping(value = "/social/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원 Social 정보 변경", description = "id에 해당하는 사용자의 Social 정보(github, instagram) 을 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보 변경 성공"),
		@ApiResponse()
	})
	public void editUserSocialInfo(@PathVariable Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated UserSocialInfoEditDto editDto) {
		checkSameUser(id, loginUser);
		userService.editUserSocialInfo(id, editDto);
	}

	@PutMapping(value = "/profileImage/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "profileImage 변경", description = "profileImage 를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "이미지 변경 성공"),
		@ApiResponse(responseCode = "401", description = "미존재"),
		@ApiResponse(responseCode = "500", description = "기존 썸네일 삭제 실패")
	})
	public ProfileSuccessEditDto editUserProfile(
		@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestPart(value = "profile", required = false) MultipartFile file) throws IOException {
		checkSameUser(id, loginUser);
		checkIsValidImageFile(file);

		return userService.editUserProfileImage(id, file);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "회원 정보 삭제", description = "id에 해당하는 사용자의 정보를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "회원 삭제 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE))
	})
	public void deleteUser(@PathVariable Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		checkSameUser(id, loginUser);
		userService.removeUser(id);
	}

	@DeleteMapping(value = "/profileImage/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "profileImage 삭제", description = "profileImage 를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "이미지 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "미존재"),
		@ApiResponse(responseCode = "500", description = "기존 썸네일 삭제 실패")
	})
	public void removeUserProfile(
		@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		checkSameUser(id, loginUser);
		userService.removeUserProfileImage(id);
	}

	@GetMapping("/profile/{userId}")
	@Operation(summary = "userId 에 해당하는 회원의 프로파일 이미지 가져오기", description = "userId에 해당하는 사용자의 프로파일 이미지를 가져옵니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 조회 성공", content = @Content(mediaType = "image/*")),
		@ApiResponse(responseCode = "404", description = "이미지 미 존재",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public ResponseEntity<byte[]> getProfile(@PathVariable Long userId) throws IOException {
		File imageFile = userService.readImageFile(userId);
		ResponseEntity<byte[]> result = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-type", Files.probeContentType(imageFile.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(imageFile), headers, HttpStatus.OK);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return result;
	}

	private void checkSameUser(Long id, LoginUser loginUser) {

		if (!id.equals(loginUser.getUser().getId())) {
			throw new AccessDeniedException("데이터를 찾지 못했습니다");
		}
	}

	private static void checkIsValidImageFile(MultipartFile file) throws IOException {
		if (file.getBytes().length == 0) {
			throw new MultipartException(FILE_IS_EMPTY);
		}
		if (!Objects.requireNonNull(file.getContentType()).contains("image/")) {
			throw new MultipartException(FILE_IS_INVALID);
		}
	}
}
