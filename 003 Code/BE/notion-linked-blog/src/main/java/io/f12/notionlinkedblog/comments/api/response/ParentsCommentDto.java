package io.f12.notionlinkedblog.comments.api.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ParentsCommentDto {
	private Long commentsId;
	private String comment;
	private LocalDateTime createdAt;
	private Long authorId;
	private String author;
	private String authorProfileLink;
	private List<ChildCommentDto> children;

	public ParentsCommentDto createParentCommentDto(CommentsEntity comments) {
		this.commentsId = comments.getId();
		this.comment = comments.getContent();
		this.createdAt = comments.getCreatedAt();
		this.authorId = comments.getUser().getId();
		this.author = comments.getUser().getUsername();
		this.authorProfileLink = comments.getUser().getProfile();
		this.children = new ArrayList<>();
		return this;
	}

	public void addChildComment(ChildCommentDto dto) {
		children.add(dto);
	}
}
