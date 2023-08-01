package io.f12.notionlinkedblog.service.comments;

import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.CommentExceptionsMessages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.comments.dto.CreateCommentDto;
import io.f12.notionlinkedblog.domain.comments.dto.response.ChildCommentDto;
import io.f12.notionlinkedblog.domain.comments.dto.response.CommentEditDto;
import io.f12.notionlinkedblog.domain.comments.dto.response.ParentsCommentDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.exceptions.message.ExceptionMessages;
import io.f12.notionlinkedblog.repository.comments.CommentsDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentsService {
	private final CommentsDataRepository commentsDataRepository;

	private final PostDataRepository postDataRepository;
	private final UserDataRepository userDataRepository;

	public List<ParentsCommentDto> getCommentsByPostId(Long postId) {
		List<Comments> comments = commentsDataRepository.findByPostId(postId);
		HashMap<Long, ParentsCommentDto> parentsMap = new HashMap<>();
		Queue<ChildCommentDto> childQueue = new LinkedList<>();
		convertCommentsToDto(comments, parentsMap, childQueue);
		return convertDtosToList(parentsMap, childQueue);
	}

	public CommentEditDto createComments(Long postId, Long userId, CreateCommentDto commentDto) {
		Post post = postDataRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.PostExceptionsMessages.POST_NOT_EXIST));
		User user = userDataRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.UserExceptionsMessages.USER_NOT_EXIST));

		Comments parentComment = null;
		if (isChild(commentDto)) {
			parentComment = commentsDataRepository.findById(commentDto.getParentCommentId())
				.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		}

		Comments builtComments = Comments.builder()
			.post(post)
			.content(commentDto.getComment())
			.user(user)
			.depth(commentDto.getDepth())
			.parent(parentComment)
			.build();

		Comments savedComments = commentsDataRepository.save(builtComments);

		CommentEditDto builtReturnDto = convertCommentsToCommentEditDto(savedComments, user);

		if (parentComment != null) {
			parentComment.addChildren(savedComments);
			builtReturnDto.setParentCommentId(savedComments.getParent().getId());
		}
		return builtReturnDto;
	}

	public CommentEditDto editComment(Long commentId, Long userId, String contents) {
		Comments comments = commentsDataRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		User user = comments.getUser();

		checkIsAuthor(userId, comments);
		comments.editComments(contents);
		return convertCommentsToCommentEditDto(comments, user);
	}

	public void removeComment(Long commentId, Long userId) {
		Comments comments = commentsDataRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		checkIsAuthor(userId, comments);
		commentsDataRepository.deleteById(commentId);
	}

	// 내부사용 매서드

	private boolean isSameUser(Long sessionUserId, Long databaseUserId) {
		return Objects.equals(sessionUserId, databaseUserId);
	}

	private static boolean isParent(Comments comments) {
		return comments.getDepth() == 0;
	}

	private static boolean isChild(CreateCommentDto commentDto) {
		return commentDto.getDepth().equals(1);
	}

	private void convertCommentsToDto(List<Comments> comments,
		HashMap<Long, ParentsCommentDto> parentsMap, Queue<ChildCommentDto> childQueue) {
		for (Comments comment : comments) {
			if (isParent(comment)) {
				parentsMap.put(comment.getId(), createParentsCommentDto(comment));
			} else {
				childQueue.add(createChildCommentDto(comment));
			}
		}
	}

	private List<ParentsCommentDto> convertDtosToList(HashMap<Long, ParentsCommentDto> parentsMap,
		Queue<ChildCommentDto> childQueue) {

		for (ChildCommentDto childCommentDto : childQueue) {
			ParentsCommentDto parentsCommentDto = parentsMap.get(childCommentDto.getParentCommentId());
			parentsCommentDto.addChildComment(childCommentDto);
		}

		return new ArrayList<>(parentsMap.values());
	}

	private static CommentEditDto convertCommentsToCommentEditDto(Comments comments, User user) {
		return CommentEditDto.builder()
			.commentId(comments.getId())
			.comment(comments.getContent())
			.parentCommentId(isParent(comments) ? null : comments.getParent().getId())
			.createdAt(comments.getCreatedAt())
			.author(user.getUsername())
			.authorId(user.getId())
			.authorProfileLink(user.getProfile())
			.build();
	}

	private void checkIsAuthor(Long userId, Comments comments) {
		if (!isSameUser(userId, comments.getUser().getId())) {
			throw new IllegalStateException(NOT_COMMENT_OWNER);
		}
	}

	private ParentsCommentDto createParentsCommentDto(Comments comment) {
		ParentsCommentDto parentsCommentDto = new ParentsCommentDto();
		return parentsCommentDto.createParentCommentDto(comment);
	}

	private ChildCommentDto createChildCommentDto(Comments comment) {
		ChildCommentDto childCommentDto = new ChildCommentDto();
		return childCommentDto.createChildCommentDto(comment);
	}
}
