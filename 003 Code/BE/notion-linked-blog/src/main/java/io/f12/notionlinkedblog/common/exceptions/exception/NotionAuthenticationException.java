package io.f12.notionlinkedblog.common.exceptions.exception;

import io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages;

public class NotionAuthenticationException extends Exception {
	public NotionAuthenticationException() {
		super(ExceptionMessages.NotionValidateMessages.ACCESS_TOKEN_INVALID);
	}

	public NotionAuthenticationException(String message) {
		super(message);
	}
}
