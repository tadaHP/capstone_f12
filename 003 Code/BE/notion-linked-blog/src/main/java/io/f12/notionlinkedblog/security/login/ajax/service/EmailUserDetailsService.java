package io.f12.notionlinkedblog.security.login.ajax.service;

import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.security.login.ajax.exception.EmailNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailUserDetailsService implements UserDetailsService {

	private final UserDataRepository userDataRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userDataRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
		return LoginUser.of(user, Set.of(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
