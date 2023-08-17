package io.f12.notionlinkedblog.common.exceptions.exception;

public class AuthFailureException extends Exception {
	public AuthFailureException() {
		super();
	}

	public AuthFailureException(String message) {
		super(message);
	}
}
