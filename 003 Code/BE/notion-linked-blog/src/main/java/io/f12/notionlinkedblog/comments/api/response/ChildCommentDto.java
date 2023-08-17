package io.f12.notionlinkedblog.comments.api.response;

import java.time.LocalDateTime;

import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChildCommentDto { //7
	private Long commentsId;
	private String comment;
	private Long parentCommentId;
	private LocalDateTime createdAt;
	private Long authorId;
	private String author;
	private String authorProfileLink;

	public ChildCommentDto createChildCommentDto(CommentsEntity comments) {
		this.commentsId = comments.getId();
		this.comment = comments.getContent();
		this.parentCommentId = comments.getParent().getId();
		this.createdAt = comments.getCreatedAt();
		this.authorId = comments.getUser().getId();
		this.author = comments.getUser().getUsername();
		this.authorProfileLink = comments.getUser().getProfile();

		return this;
	}
}
