package io.f12.notionlinkedblog.notion.exception;

public class NoAccessTokenException extends Exception {
	public NoAccessTokenException() {
		super("AcessToken이 존재하지 않습니다.");
	}

	public NoAccessTokenException(String message) {
		super(message);
	}
}
