package io.f12.notionlinkedblog.oauth.common.domain;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateOauthAttribute {
	private final Map<String, Object> attributes;
	private final String newEmail;
}
