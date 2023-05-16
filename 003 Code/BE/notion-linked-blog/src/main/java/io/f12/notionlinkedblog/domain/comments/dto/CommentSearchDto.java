package io.f12.notionlinkedblog.domain.comments.dto;

import io.f12.notionlinkedblog.domain.comments.Comments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentSearchDto {

	private String comments;
	private String username;
	private Integer depth;
	private Long parentCommentId;

	public CommentSearchDto createParentComment(Comments comment) {
		return CommentSearchDto.builder()
			.comments(comment.getContent())
			.username(comment.getUser().getUsername())
			.depth(comment.getDepth())
			.build();
	}

	public CommentSearchDto createChildComment(Comments comment) {
		return CommentSearchDto.builder()
			.comments(comment.getContent())
			.username(comment.getUser().getUsername())
			.depth(comment.getDepth())
			.parentCommentId(comment.getParent().getId())
			.build();
	}
}
