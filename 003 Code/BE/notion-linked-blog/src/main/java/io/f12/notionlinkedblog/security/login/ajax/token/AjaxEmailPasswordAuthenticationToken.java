package io.f12.notionlinkedblog.security.login.ajax.token;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;

public class AjaxEmailPasswordAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;

	private Object credentials;

	public AjaxEmailPasswordAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public AjaxEmailPasswordAuthenticationToken(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true); // must use super, as we override
	}

	public static AjaxEmailPasswordAuthenticationToken unauthenticated(Object principal, Object credentials) {
		return new AjaxEmailPasswordAuthenticationToken(principal, credentials);
	}

	public static AjaxEmailPasswordAuthenticationToken authenticated(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		return new AjaxEmailPasswordAuthenticationToken(principal, credentials, authorities);
	}

	@Override
	public String getName() {
		return ((LoginUser)principal).getEmail();
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		Assert.isTrue(!isAuthenticated,
			"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		this.credentials = null;
	}
}
