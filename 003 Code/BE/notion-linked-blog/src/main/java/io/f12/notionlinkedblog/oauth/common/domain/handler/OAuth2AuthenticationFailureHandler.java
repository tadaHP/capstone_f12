package io.f12.notionlinkedblog.oauth.common.domain.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.security.login.ajax.dto.AjaxLoginFailureDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		log.info("Oauth2AuthenticationFailureHandler.onAuthenticationFailure() 시작");
		String errorMessage = "로그인에 실패하였습니다. " + exception.getLocalizedMessage();
		AjaxLoginFailureDto loginFailureDto = AjaxLoginFailureDto.from(errorMessage);
		response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		objectMapper.writeValue(response.getWriter(), loginFailureDto);
	}
}
