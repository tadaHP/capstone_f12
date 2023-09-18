package io.f12.notionlinkedblog.oauth.common.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuthAttributes {
	GITHUB("github", (attributes) -> {
		return new OAuth2UserProfile(
			String.valueOf(attributes.get("id")),
			(String)attributes.get("name"),
			(String)attributes.get("email")
		);
	}),
	GOOGLE("Google", (attributes) -> {
		return new OAuth2UserProfile(
			String.valueOf(attributes.get("sub")),
			(String)attributes.get("name"),
			(String)attributes.get("email")
		);
	});
	private final String registrationId;
	private final Function<Map<String, Object>, OAuth2UserProfile> of;

	public static OAuth2UserProfile extract(String registrationId, Map<String, Object> attributes) {
		return Arrays.stream(values())
			.filter(p -> registrationId.equals(p.registrationId))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new)
			.of.apply(attributes);
	}
}
