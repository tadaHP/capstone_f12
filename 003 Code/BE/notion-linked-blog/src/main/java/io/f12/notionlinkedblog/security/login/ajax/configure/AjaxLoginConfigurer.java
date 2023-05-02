package io.f12.notionlinkedblog.security.login.ajax.configure;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.f12.notionlinkedblog.security.login.ajax.filter.AjaxEmailPasswordAuthenticationFilter;
import io.f12.notionlinkedblog.security.login.ajax.handler.AjaxAuthenticationFailureHandler;
import io.f12.notionlinkedblog.security.login.ajax.handler.AjaxAuthenticationSuccessHandler;
import io.f12.notionlinkedblog.security.login.ajax.provider.AjaxEmailPasswordAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>>
	extends AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxEmailPasswordAuthenticationFilter> {

	private PasswordEncoder passwordEncoder;
	private UserDetailsService userDetailsService;

	private AjaxLoginConfigurer() {
		super(AjaxEmailPasswordAuthenticationFilter.create(), AjaxEmailPasswordAuthenticationFilter.DEFAULT_URL);
	}

	public static AjaxLoginConfigurer<HttpSecurity> create() {
		return new AjaxLoginConfigurer<>();
	}

	@Override
	protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
		return new AntPathRequestMatcher(loginProcessingUrl, "POST");
	}

	@Override
	public void configure(H http) {
		log.info("AjaxLoginConfigurer.configure() 시작");
		ProviderManager providerManager = (ProviderManager)http.getSharedObject(AuthenticationManager.class);
		providerManager.getProviders().add(ajaxEmailPasswordAuthenticationProvider());
		http.addFilterAfter(ajaxEmailPasswordAuthenticationFilter(providerManager), LogoutFilter.class);
	}

	public AjaxEmailPasswordAuthenticationFilter ajaxEmailPasswordAuthenticationFilter(
		AuthenticationManager authenticationManager) {
		AjaxEmailPasswordAuthenticationFilter authenticationFilter = AjaxEmailPasswordAuthenticationFilter.create();
		authenticationFilter.setAuthenticationManager(authenticationManager);
		authenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
		authenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
		return authenticationFilter;
	}

	public AjaxEmailPasswordAuthenticationProvider ajaxEmailPasswordAuthenticationProvider() {
		AjaxEmailPasswordAuthenticationProvider authenticationProvider
			= AjaxEmailPasswordAuthenticationProvider.create();
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		authenticationProvider.setUserDetailsService(userDetailsService);
		return authenticationProvider;
	}

	public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
		return AjaxAuthenticationSuccessHandler.create();
	}

	public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
		return AjaxAuthenticationFailureHandler.create();
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
