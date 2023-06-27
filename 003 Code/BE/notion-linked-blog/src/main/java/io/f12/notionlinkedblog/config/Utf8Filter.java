package io.f12.notionlinkedblog.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(filterName = "Utf8Filter", urlPatterns = {"/*"})
public class Utf8Filter implements Filter {

	private static final String[] EXCLUDED_URL_PATTERNS = {"/api/posts/thumbnail/", "/api/users/profile/"};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		String url = ((HttpServletRequest)request).getRequestURI();

		for (String excludedPattern : EXCLUDED_URL_PATTERNS) {
			if (url.contains(excludedPattern)) {
				chain.doFilter(request, response);
				return;
			}
		}

		response.setCharacterEncoding("UTF-8");
		chain.doFilter(request, response);
	}
}