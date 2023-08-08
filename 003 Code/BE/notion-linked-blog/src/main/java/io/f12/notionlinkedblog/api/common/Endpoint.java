package io.f12.notionlinkedblog.api.common;

public class Endpoint {
	public static class Local {
		public static final String LOCAL_ADDRESS = "http://localhost:8080";
	}

	public static class Api {
		public static final String LOGIN_WITH_EMAIL = "/api/login/email";
		public static final String LOGOUT = "/api/logout";
		public static final String USER = "/api/users";
		public static final String POST = "/api/posts";
		public static final String COMMENTS = "/api/comments"; // accept develop,
		public static final String SERIES = "/api/series";
		// public static final String COMMENTS = "/api/posts/{id}/comments"; // before
		public static final String LOGIN_STATUS = "/api/users/login-status";
		public static final String NOTION = "/api/notion";
		public static final String EMAIL = "/api/email";
		public static final String REQUEST_THUMBNAIL_IMAGE = "/api/posts/thumbnail/";
		public static final String REQUEST_PROFILE_IMAGE = "/api/users/profile/";
		public static final String REQUEST_FILE = "/api/download";
		public static final String REQUEST_IMAGE = "/api/image";
	}

	public static class NotionAuth {
		public static final String NOTION_CODE_TO_ACCESS_TOKEN = "https://api.notion.com/v1/oauth/token";
	}
}
