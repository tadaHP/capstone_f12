package io.f12.notionlinkedblog.series.exception;

public class SeriesNotExistException extends Exception {
	public SeriesNotExistException() {
		super("시리즈가 존재하지 않습니다.");
	}

	public SeriesNotExistException(String message) {
		super(message);
	}
}
