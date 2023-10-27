package io.f12.notionlinkedblog.security.login.ajax.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import io.f12.notionlinkedblog.oauth.common.domain.OAuth2UserProfile;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginUser implements OAuth2User, UserDetails, Serializable {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private final UserEntity user;
	private final boolean isAccountNonExpired = true;
	private final boolean isAccountNonLocked = true;
	private final boolean isCredentialsNonExpired = true;
	private final boolean isEnabled = true;
	private final Collection<? extends GrantedAuthority> authorities;
	private final OAuth2UserProfile attributes;

	private LoginUser(UserEntity user, Collection<? extends GrantedAuthority> authorities,
		OAuth2UserProfile attributes) {
		this.user = user;
		this.authorities = authorities;
		this.attributes = attributes;
	}

	public static LoginUser of(UserEntity user, Collection<? extends GrantedAuthority> authorities) {
		return new LoginUser(user, authorities, null);
	}

	public static LoginUser of(UserEntity user, Collection<? extends GrantedAuthority> authorities,
		OAuth2UserProfile attributes) {
		return new LoginUser(user, authorities, attributes);
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
	public Map<String, Object> getAttributes() {
		return this.attributes.toAttributes();
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

	@Override
	public String getName() {
		return this.attributes.getName();
	}
}
