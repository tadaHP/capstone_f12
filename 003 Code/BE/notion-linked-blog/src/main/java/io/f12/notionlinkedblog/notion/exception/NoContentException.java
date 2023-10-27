package io.f12.notionlinkedblog.notion.exception;

public class NoContentException extends RuntimeException {
	public NoContentException() {
		super("연동할 게시물이 없습니다.");
	}

	public NoContentException(String message) {
		super(message);
	}
}
