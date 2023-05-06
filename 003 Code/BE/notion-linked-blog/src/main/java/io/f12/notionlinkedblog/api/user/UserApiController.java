package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;

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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.USER)
@RestController
public class UserApiController {

	private final UserService userService;

	@PostMapping("/email/signup")
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
	public UserSearchDto getUserInfo(@PathVariable Long id) {
		return userService.getUserInfo(id);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> editUserInfo(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated UserEditDto editDto) {
		checkSameUser(id, loginUser);
		userService.editUserInfo(id, editDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
		checkSameUser(id, loginUser);
		userService.removeUser(id);
	}

	private void checkSameUser(Long id, LoginUser loginUser) {
		if (!id.equals(loginUser.getId())) {
			throw new AccessDeniedException("데이터를 찾지 못했습니다");
		}
	}
}
