package io.f12.notionlinkedblog.oauth.common.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OAuth2UserProfile {
	private final String oauthId;
	private final String name;
	private final String email;

	public Map<String, Object> toAttributes() {
		Map<String, Object> stringObjectMap = new HashMap<>();
		stringObjectMap.put("oauthId", oauthId);
		stringObjectMap.put("name", name);
		stringObjectMap.put("email", email);
		return stringObjectMap;
	}

}

