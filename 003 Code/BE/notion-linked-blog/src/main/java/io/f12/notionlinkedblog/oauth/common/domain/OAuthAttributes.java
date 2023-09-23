package io.f12.notionlinkedblog.oauth.common.domain;

import java.util.Arrays;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuthAttributes {
	GITHUB("GitHub", (attributes) -> {
		return new OAuth2UserProfile(
			String.valueOf(attributes.getAttributes().get("id")),
			(String)attributes.getAttributes().get("login"),
			"github " + attributes.getNewEmail()
		);
	}),
	GOOGLE("Google", (attributes) -> {
		return new OAuth2UserProfile(
			String.valueOf(attributes.getAttributes().get("sub")),
			(String)attributes.getAttributes().get("name"),
			"google " + (String)attributes.getAttributes().get("email")
		);
	});

	private final String registrationId;
	private final Function<CreateOauthAttribute, OAuth2UserProfile> of;

	public static OAuth2UserProfile extract(String registrationId, CreateOauthAttribute attributes) {
		return Arrays.stream(values())
			.filter(p -> registrationId.equals(p.registrationId))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new)
			.of.apply(attributes);
	}
}
