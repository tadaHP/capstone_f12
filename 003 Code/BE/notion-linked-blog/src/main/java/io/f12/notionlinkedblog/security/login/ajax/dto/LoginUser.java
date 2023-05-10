package io.f12.notionlinkedblog.security.login.ajax.dto;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import io.f12.notionlinkedblog.domain.user.User;
import lombok.Getter;

@Getter
public class LoginUser implements UserDetails, Serializable {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private final User user;
	private final boolean isAccountNonExpired = true;
	private final boolean isAccountNonLocked = true;
	private final boolean isCredentialsNonExpired = true;
	private final boolean isEnabled = true;
	private final Collection<? extends GrantedAuthority> authorities;

	private LoginUser(User user, Collection<? extends GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	public static LoginUser of(User user, Collection<? extends GrantedAuthority> authorities) {
		return new LoginUser(user, authorities);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
}
