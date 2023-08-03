package io.f12.notionlinkedblog.aop.exception;

import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import io.f12.notionlinkedblog.domain.common.CommonErrorResponse;
import io.f12.notionlinkedblog.domain.user.dto.response.NoUserProfileDto;
import io.f12.notionlinkedblog.exceptions.exception.AuthFailureException;
import io.f12.notionlinkedblog.exceptions.exception.NoProfileImageException;
import io.f12.notionlinkedblog.exceptions.runtimeexception.IllegalDatabaseStateException;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class DefaultRestControllerAdvice {

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public CommonErrorResponse handleIllegalState(IllegalStateException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.UNAUTHORIZED.value()).build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public CommonErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.NOT_FOUND.value()).build();
	}

	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonErrorResponse handleNullPointer(NullPointerException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
	}

	@ExceptionHandler(MalformedURLException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public CommonErrorResponse handleMalformedException(MalformedURLException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.NOT_FOUND.value()).build();
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonErrorResponse handleMultipartException(MultipartException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.BAD_REQUEST.value()).build();
	}

	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonErrorResponse handleNumberFormatException(NumberFormatException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.BAD_REQUEST.value()).build();
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public CommonErrorResponse handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).build();
	}

	@ExceptionHandler(DirectoryNotEmptyException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonErrorResponse handleDirectoryNotEmptyException(DirectoryNotEmptyException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
	}

	@ExceptionHandler(AuthFailureException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonErrorResponse handleAuthFailureException(AuthFailureException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.BAD_REQUEST.value()).build();
	}

	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonErrorResponse handleHttpClientErrorException(HttpClientErrorException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.BAD_REQUEST.value()).build();
	}

	@ExceptionHandler(IllegalDatabaseStateException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public CommonErrorResponse handleIllegalDatabaseStateException(IllegalDatabaseStateException ex) {
		return CommonErrorResponse.builder()
			.errorMassage(ex.getMessage())
			.errorCode(HttpStatus.BAD_REQUEST.value()).build();
	}

	@ExceptionHandler(NoProfileImageException.class)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public NoUserProfileDto handleNoProfileImageException(NoProfileImageException ex) {
		return NoUserProfileDto.builder()
			.status(PROFILE_IMAGE_NOT_EXIST)
			.build();
	}
}
