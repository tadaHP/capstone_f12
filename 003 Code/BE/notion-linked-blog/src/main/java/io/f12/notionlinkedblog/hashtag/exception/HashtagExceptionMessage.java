package io.f12.notionlinkedblog.hashtag.exception;

import lombok.Getter;

@Getter
public enum HashtagExceptionMessage {
	INVALID_HASHTAG("잘못된 해시태그입니다");

	private final String messages;

	HashtagExceptionMessage(String messages) {
		this.messages = messages;
	}

}
