package io.f12.notionlinkedblog.api;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.service.EmailSignupService;
import io.f12.notionlinkedblog.web.argumentresolver.email.Email;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/email")
@RestController
public class EmailApiController {
	public static final String redisCookieName = "x-redis-id";
	public static final String emailVerifiedAttr = "emailVerified";
	private final EmailSignupService emailSignupService;

	@PostMapping("/code")
	public ResponseEntity<String> verifyCode(
		HttpSession session, @CookieValue(redisCookieName) String redisId, @RequestBody String code) {
		verifyCodeElseThrowIllegalArgumentException(redisId, code);

		ResponseCookie redisCookie = ResponseCookie.from(redisCookieName, redisId).maxAge(0L).build();
		session.setAttribute(emailVerifiedAttr, "verified");

		return ResponseEntity.noContent()
			.header(HttpHeaders.SET_COOKIE, redisCookie.toString()).build();
	}

	private void verifyCodeElseThrowIllegalArgumentException(String redisId, String code) {
		boolean isVerified = emailSignupService.verifyingCode(redisId, code);
		if (!isVerified) {
			throw new IllegalArgumentException("잘못된 인증 코드입니다.");
		}
	}

	@PostMapping
	public ResponseEntity<String> sendRandomCode(@Email String email) {
		String redisId = emailSignupService.sendMail(email);
		ResponseCookie cookie = ResponseCookie.from(redisCookieName, redisId)
			.httpOnly(true)
			.maxAge(300L)
			.build();
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}
}
