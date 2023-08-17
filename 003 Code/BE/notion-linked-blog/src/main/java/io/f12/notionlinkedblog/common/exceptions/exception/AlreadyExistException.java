package io.f12.notionlinkedblog.common.exceptions.exception;

public class AlreadyExistException extends RuntimeException {
	public AlreadyExistException() {
	}

	public AlreadyExistException(String message) {
		super(message);
	}
}
