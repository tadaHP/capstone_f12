package io.f12.notionlinkedblog.api.common;

public class Endpoint {
	public static class Api {
		public static final String LOGIN_WITH_EMAIL = "/api/login/email";
		public static final String LOGOUT = "/api/logout";
		public static final String USER = "/api/users";
		public static final String POST = "/api/posts";
		public static final String COMMENTS = "/api/posts/{id}/comments";
		public static final String LOGIN_STATUS = "/api/users/login-status";
	}
}
