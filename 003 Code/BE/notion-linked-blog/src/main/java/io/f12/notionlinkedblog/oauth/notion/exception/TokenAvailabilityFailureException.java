package io.f12.notionlinkedblog.oauth.notion.exception;

public class TokenAvailabilityFailureException extends Exception {
	public TokenAvailabilityFailureException() {
		super("Token이 유효하지 않습니다.");
	}

	public TokenAvailabilityFailureException(String message) {
		super(message);
	}
}
