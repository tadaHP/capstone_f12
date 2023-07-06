package io.f12.notionlinkedblog.security.login.check.filter;

import static io.f12.notionlinkedblog.api.common.Endpoint.Api.*;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.security.login.ajax.dto.UserWithoutPassword;
import io.f12.notionlinkedblog.security.login.check.dto.LoginStatusCheckingFailureResponseDto;
import io.f12.notionlinkedblog.security.login.check.dto.LoginStatusCheckingSuccessResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LoginStatusCheckingFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final RequestMatcher loginStatusCheckingRequestMatcher = new AntPathRequestMatcher(LOGIN_STATUS, "GET");
	private final UserDataRepository userDataRepository;

	public LoginStatusCheckingFilter(UserDataRepository userDataRepository) {
		this.userDataRepository = userDataRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		RequestMatcher.MatchResult matcher = loginStatusCheckingRequestMatcher.matcher(request);
		if (matcher.isMatch()) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				log.info("Session is exists.");
				SecurityContext securityContext = (SecurityContext)session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
				Authentication authentication;
				if (securityContext != null
					&& (authentication = securityContext.getAuthentication()) != null) {
					log.info("SecurityContext is exists.");
					LoginUser principal = (LoginUser)authentication.getPrincipal();
					User user = userDataRepository.findById(principal.getUser().getId()).get();
					UserWithoutPassword userWithoutPassword = UserWithoutPassword.of(user);
					LoginStatusCheckingSuccessResponseDto responseDto =
						LoginStatusCheckingSuccessResponseDto.of(userWithoutPassword);
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					objectMapper.writeValue(response.getWriter(), responseDto);
					return;
				}

				log.info("AnonymousUser is accessed.");
				LoginStatusCheckingFailureResponseDto responseDto =
					LoginStatusCheckingFailureResponseDto.of("AnonymousUser is accessed.");
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				objectMapper.writeValue(response.getWriter(), responseDto);
				return;
			}

			log.info("No session is exists.");
			LoginStatusCheckingFailureResponseDto responseDto =
				LoginStatusCheckingFailureResponseDto.of("No session is exists.");
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			objectMapper.writeValue(response.getWriter(), responseDto);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
