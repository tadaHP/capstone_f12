package io.f12.notionlinkedblog.hashtag.exception;

public class NoHashtagException extends Exception {
	public NoHashtagException() {
		super(HashtagExceptionMessage.INVALID_HASHTAG.getMessages());
	}

	public NoHashtagException(String message) {
		super(message);
	}
}
