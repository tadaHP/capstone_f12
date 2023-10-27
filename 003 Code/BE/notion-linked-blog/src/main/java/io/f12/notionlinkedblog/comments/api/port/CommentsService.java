package io.f12.notionlinkedblog.comments.api.port;

import java.util.List;

import io.f12.notionlinkedblog.comments.api.response.CommentEditDto;
import io.f12.notionlinkedblog.comments.api.response.ParentsCommentDto;
import io.f12.notionlinkedblog.comments.domain.dto.CreateCommentDto;

public interface CommentsService {
	List<ParentsCommentDto> getCommentsByPostId(Long postId);

	CommentEditDto createComments(Long postId, Long userId, CreateCommentDto commentDto);

	CommentEditDto editComment(Long commentId, Long userId, String contents);

	void removeComment(Long commentId, Long userId);
}
