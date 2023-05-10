package io.f12.notionlinkedblog.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.security.common.dto.AuthenticationFailureDto;
import io.f12.notionlinkedblog.security.login.ajax.configure.AjaxLoginConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(Endpoint.Api.USER + "/email/signup").permitAll()
			.antMatchers(Endpoint.Api.USER + "/**").hasRole("USER")
			.anyRequest().permitAll();

		http
			.headers().frameOptions().disable()
			.and()
			.csrf().disable();

		http
			.cors().configurationSource(corsConfigurationSource());

		http
			.httpBasic().disable()
			.formLogin().disable();

		http
			.logout()
			.logoutUrl("/api/logout")
			.clearAuthentication(true)
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.logoutSuccessHandler(
				(request, response, authentication) -> response.setStatus(HttpStatus.NO_CONTENT.value()));

		http
			.exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint())
			.accessDeniedHandler(accessDeniedHandler());

		http
			.apply(ajaxLoginConfigurer());

		return http.build();
	}

	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addExposedHeader("Authorization");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			log.info("BasicAuthenticationEntryPoint.commence() 실행");
			ObjectMapper objectMapper = new ObjectMapper();
			AuthenticationFailureDto authenticationFailureDto = AuthenticationFailureDto.getInstance();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			objectMapper.writeValue(response.getWriter(), authenticationFailureDto);
		};
	}

	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) ->
			response.sendError(HttpStatus.NOT_FOUND.value(), accessDeniedException.getMessage());
	}

	public AjaxLoginConfigurer<HttpSecurity> ajaxLoginConfigurer() {
		AjaxLoginConfigurer<HttpSecurity> ajaxLoginConfigurer = AjaxLoginConfigurer.create();
		ajaxLoginConfigurer.setPasswordEncoder(passwordEncoder());
		ajaxLoginConfigurer.setUserDetailsService(userDetailsService);
		return ajaxLoginConfigurer;
	}

	@Bean
	public SecureRandomFactoryBean secureRandomFactoryBean() {
		return new SecureRandomFactoryBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
