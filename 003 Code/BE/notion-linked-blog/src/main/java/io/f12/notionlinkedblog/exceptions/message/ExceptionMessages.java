package io.f12.notionlinkedblog.exceptions.message;

public class ExceptionMessages {
	public static class UserExceptionsMessages {
		public static final String USER_NOT_EXIST = "존재하지 않는 유저입니다.";
		public static final String EMAIL_ALREADY_EXIST = "이미 존재하는 이메일입니다.";
		public static final String IMAGE_NOT_EXIST = "존재하지 않는 이미지 입니다";
		public static final String FILE_IS_EMPTY = "파일이 존재하지 않습니다.";
		public static final String FILE_IS_INVALID = "파일의 종류가 잘못되었습니다.";
		public static final String PROFILE_IMAGE_NOT_EXIST = "프로필 이미지가 존재하지 않습니다.";
	}

	public static class PostExceptionsMessages {
		public static final String POST_NOT_EXIST = "존재하지 않는 포스트입니다.";
		public static final String WRITER_USER_NOT_MATCH = "글 작성자와 사용자가 일치하지 않습니다.";
		public static final String IMAGE_NOT_EXIST = "존재하지 않는 이미지 입니다";
	}

	public static class CommentExceptionsMessages {
		public static final String COMMENT_NOT_EXIST = "존재하지 않는 댓글 입니다.";
		public static final String NOT_COMMENT_OWNER = "작성자와 변경자가 상이합니다";
	}

	public static class FileExceptionsMessages {
		public static final String FILE_NOT_EXIST = "파일이 존재하지 않습니다.";
	}

	public static class UserValidateMessages {
		public static final String USER_NOT_MATCH = "동일한 사용자가 아닙니다.";
	}

	public static class NotionValidateMessages {
		public static final String DATA_ALREADY_EXIST = "이미 연동한 데이터 입니다.";
		public static final String NOT_ALLOW_ACCESS = "연동에 실패하였습니다.";
		public static final String TOKEN_AVAILABILITY_FAILURE = "토큰 고유값이 일치하지 않습니다.";
		public static final String ACCESS_TOKEN_INVALID = "토큰이 존재하지 않습니다";

	}

	public static class SeriesExceptionMessages {
		public static final String SERIES_NOT_EXIST = "시리즈가 존재하지 않습니다.";
	}
}
