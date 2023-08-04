package io.f12.notionlinkedblog.exceptions.exception;

import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.NotionValidateMessages.*;

public class NotionAuthenticationException extends Exception {
	public NotionAuthenticationException() {
		super(ACCESS_TOKEN_INVALID);
	}

	public NotionAuthenticationException(String message) {
		super(message);
	}
}
