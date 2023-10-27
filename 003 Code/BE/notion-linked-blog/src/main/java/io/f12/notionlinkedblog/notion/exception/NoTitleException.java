package io.f12.notionlinkedblog.notion.exception;

public class NoTitleException extends Exception {
	public NoTitleException() {
		super("컨텐츠에 제목이 존재하지 않습니다.");
	}

	public NoTitleException(String message) {
		super(message);
	}
}
