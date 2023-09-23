package io.f12.notionlinkedblog.oauth.github;

import java.net.URI;
import java.util.Collections;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.util.UriComponentsBuilder;

public class GithubOAuth2RequestEntityConverter implements Converter<OAuth2UserRequest, RequestEntity<?>> {
	@Override
	public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		HttpMethod getHttpMethod = HttpMethod.GET;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		URI uri = UriComponentsBuilder
			.fromUriString("https://api.github.com/user/emails").build().toUri();
		headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
		return new RequestEntity<>(headers, getHttpMethod, uri);

	}
}
