package io.f12.notionlinkedblog.exceptions.exception;

public class AuthFailureException extends Exception {
	public AuthFailureException() {
		super();
	}

	public AuthFailureException(String message) {
		super(message);
	}
}
