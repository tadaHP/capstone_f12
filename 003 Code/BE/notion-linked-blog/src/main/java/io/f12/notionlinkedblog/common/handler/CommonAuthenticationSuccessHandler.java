package io.f12.notionlinkedblog.common.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.common.domain.dto.LoginSuccessDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RequestCache requestCache = new HttpSessionRequestCache();

	private static CommonAuthenticationSuccessHandler self = null;

	public static CommonAuthenticationSuccessHandler create() {
		if (self == null) {
			self = new CommonAuthenticationSuccessHandler();
		}
		return self;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		log.info("CommonAuthenticationSuccessHandler.onAuthenticationSuccess() 시작");
		SavedRequest savedRequest = this.requestCache.getRequest(request, response);
		String redirectUrl = savedRequest == null ? getDefaultTargetUrl() : savedRequest.getRedirectUrl();
		LoginSuccessDto loginSuccessDto = LoginSuccessDto.getLoginSuccessDto(authentication, redirectUrl);
		response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), loginSuccessDto);
		clearAuthenticationAttributes(request);
	}
}
