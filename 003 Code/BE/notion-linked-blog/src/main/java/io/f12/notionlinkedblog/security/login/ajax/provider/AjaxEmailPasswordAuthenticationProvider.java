package io.f12.notionlinkedblog.security.login.ajax.provider;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import io.f12.notionlinkedblog.security.login.ajax.token.AjaxEmailPasswordAuthenticationToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AjaxEmailPasswordAuthenticationProvider implements AuthenticationProvider {

	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
	private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private final GrantedAuthoritiesMapper authoritiesMapper = new SimpleAuthorityMapper();
	private PasswordEncoder passwordEncoder;
	private UserDetailsService userDetailsService;
	private volatile String userNotFoundEncodedPassword;

	private AjaxEmailPasswordAuthenticationProvider() {
	}

	public static AjaxEmailPasswordAuthenticationProvider create() {
		return new AjaxEmailPasswordAuthenticationProvider();
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = (String)authentication.getPrincipal();
		String password = (String)authentication.getCredentials();
		UserDetails user;

		try {
			user = retrieveUser(email, (AjaxEmailPasswordAuthenticationToken)authentication);
		} catch (UsernameNotFoundException ex) {
			log.debug("Failed to find user '" + email + "'");

			throw new BadCredentialsException(this.messages
				.getMessage("AjaxEmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
		}
		Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		boolean matches = passwordEncoder.matches(password, user.getPassword());
		if (!matches) {
			throw new BadCredentialsException(this.messages
				.getMessage("AjaxEmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		return createSuccessAuthentication(user, authentication, user);
	}

	private Authentication createSuccessAuthentication(Object principal, Authentication authentication,
		UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		AjaxEmailPasswordAuthenticationToken result = AjaxEmailPasswordAuthenticationToken.authenticated(principal,
			authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());
		log.debug("Authenticated user");
		return result;
	}

	protected final UserDetails retrieveUser(String email, AjaxEmailPasswordAuthenticationToken authentication)
		throws AuthenticationException {
		prepareTimingAttackProtection();
		try {
			UserDetails loadedUser = this.userDetailsService.loadUserByUsername(email);
			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		} catch (UsernameNotFoundException ex) {
			mitigateAgainstTimingAttack(authentication);
			throw ex;
		} catch (InternalAuthenticationServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AjaxEmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private void prepareTimingAttackProtection() {
		if (this.userNotFoundEncodedPassword == null) {
			this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
		}
	}

	private void mitigateAgainstTimingAttack(AjaxEmailPasswordAuthenticationToken authentication) {
		if (authentication.getCredentials() != null) {
			String presentedPassword = authentication.getCredentials().toString();
			this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
		}
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
