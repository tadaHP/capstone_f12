package io.f12.notionlinkedblog.security.login.ajax.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.user.dto.login.email.EmailLoginUserRequestDto;
import io.f12.notionlinkedblog.security.login.ajax.token.AjaxEmailPasswordAuthenticationToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AjaxEmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String DEFAULT_URL = Endpoint.Api.LOGIN_WITH_EMAIL;
	private static final String AJAX_EMAIL_KEY = "email";
	private static final String AJAX_PASSWORD_KEY = "password";
	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher(DEFAULT_URL, "POST");

	private String emailParameter = AJAX_EMAIL_KEY;

	private String passwordParameter = AJAX_PASSWORD_KEY;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final boolean postOnly = true;

	private AjaxEmailPasswordAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	private AjaxEmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

	public static AjaxEmailPasswordAuthenticationFilter create() {
		return new AjaxEmailPasswordAuthenticationFilter();
	}

	public static AjaxEmailPasswordAuthenticationFilter create(AuthenticationManager authenticationManager) {
		return new AjaxEmailPasswordAuthenticationFilter(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException, IOException {
		log.info("AjaxEmailPasswordAuthenticationFilter.attemptAuthentication() 시작");
		if (this.postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		if (!request.getHeader(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.APPLICATION_JSON_VALUE)) {
			throw new AuthenticationServiceException(
				"Authentication content type not supported: " + request.getHeader(HttpHeaders.CONTENT_TYPE));
		}

		EmailLoginUserRequestDto requestDto = getAjaxLoginUserDetails(request);

		String email = requestDto.getEmail();
		email = (email != null) ? email.trim() : "";
		String password = requestDto.getPassword();
		password = (password != null) ? password : "";
		AjaxEmailPasswordAuthenticationToken authRequest = AjaxEmailPasswordAuthenticationToken.unauthenticated(email,
			password);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, AjaxEmailPasswordAuthenticationToken authRequest) {
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
	}

	private EmailLoginUserRequestDto getAjaxLoginUserDetails(HttpServletRequest request) throws IOException {
		return objectMapper.readValue(request.getReader(), EmailLoginUserRequestDto.class);
	}

	public void setEmailParameter(String emailParameter) {
		Assert.hasText(emailParameter, "Email parameter must not be empty or null");
		this.emailParameter = emailParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the password from the login request..
	 * @param passwordParameter the parameter name. Defaults to "password".
	 */
	public void setPasswordParameter(String passwordParameter) {
		Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
		this.passwordParameter = passwordParameter;
	}

	public final String getEmailParameter() {
		return this.emailParameter;
	}

	public final String getPasswordParameter() {
		return this.passwordParameter;
	}
}
