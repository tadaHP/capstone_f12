package io.f12.notionlinkedblog.oauth.common.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.f12.notionlinkedblog.oauth.common.domain.CreateOauthAttribute;
import io.f12.notionlinkedblog.oauth.common.domain.OAuth2UserProfile;
import io.f12.notionlinkedblog.oauth.common.domain.OAuthAttributes;
import io.f12.notionlinkedblog.oauth.github.GithubOAuth2UserService;
import io.f12.notionlinkedblog.oauth.github.dto.GithubOauthEmailEntity;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserDetailsService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final GithubOAuth2UserService githubOAuth2UserService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String clientName = userRequest.getClientRegistration().getClientName();
		log.info("clientName: {}", clientName);

		OAuth2User oAuth2User = super.loadUser(userRequest);
		String newGithubEmail = null;
		oAuth2User.getAttributes().forEach((key, value) -> {
			log.info("key: {}, info: {}", key, value);
		});

		if (clientName.equals("GitHub")) {
			GithubOauthEmailEntity githubOauthEmailEntity = githubOAuth2UserService.loadUser(userRequest);
			Assert.notNull(githubOauthEmailEntity, "이메일이 설정되어있지 않습니다.");
			newGithubEmail = githubOauthEmailEntity.getEmail();
		}

		OAuth2UserProfile userProfile = OAuthAttributes
			.extract(clientName, new CreateOauthAttribute(oAuth2User.getAttributes(), newGithubEmail));

		UserEntity user = getOrSaveUser(userProfile);
		return LoginUser.builder()
			.attributes(userProfile)
			.user(user)
			.authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
			.build();
	}

	private UserEntity getOrSaveUser(OAuth2UserProfile userProfile) {

		Optional<UserEntity> user = userRepository.findByOauthId(userProfile.getOauthId());
		if (user.isPresent()) {
			return user.orElseThrow(IllegalArgumentException::new);
		}

		return userRepository.save(UserEntity.builder()
			.email(userProfile.getEmail())
			.username(userProfile.getName())
			.password(passwordEncoder.encode(KeyGenerators.string().generateKey()))
			.oauthId(userProfile.getOauthId())
			.build());
	}

}
