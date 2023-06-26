package io.f12.notionlinkedblog.web.argumentresolver.email;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class EmailArgumentResolver implements HandlerMethodArgumentResolver {

	static final String emailValidRegex = "^(?=.{1,64}@.{1,255}$)[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
	static final Pattern pattern = Pattern.compile(emailValidRegex);

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasParameterAnnotation = parameter.hasParameterAnnotation(Email.class);
		boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());

		return hasParameterAnnotation && hasStringType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			char[] charBuffer = new char[128];
			int bytesRead;
			while ((bytesRead = br.read(charBuffer)) > 0) {
				sb.append(charBuffer, 0, bytesRead);
			}
		}

		String email = sb.toString();
		boolean matches = pattern.matcher(email).matches();

		if (matches) {
			return email;
		}

		throw new IllegalArgumentException("잘못된 이메일 형식입니다.");
	}
}
