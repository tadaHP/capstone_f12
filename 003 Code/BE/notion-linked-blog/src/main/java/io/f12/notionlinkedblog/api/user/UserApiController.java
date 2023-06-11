package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;
import static org.springframework.http.MediaType.*;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.user.dto.info.UserEditDto;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
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

@Tag(name = "User", description = "사용자 API")
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.USER)
@RestController
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

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원 정보 변경", description = "id에 해당하는 사용자의 정보를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보변경 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = UserSearchDto.class)))
	})
	public void editUserInfo(@PathVariable Long id,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated UserEditDto editDto) {
		checkSameUser(id, loginUser);
		userService.editUserInfo(id, editDto);
	} //TODO: 추후 정보 수정 성공과 관련된 url 넘기기

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

	private void checkSameUser(Long id, LoginUser loginUser) {
		if (!id.equals(loginUser.getUser().getId())) {
			throw new AccessDeniedException("데이터를 찾지 못했습니다");
		}
	}
}
