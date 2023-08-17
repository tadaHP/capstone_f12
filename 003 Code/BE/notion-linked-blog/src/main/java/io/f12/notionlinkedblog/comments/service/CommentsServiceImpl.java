package io.f12.notionlinkedblog.comments.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.CommentExceptionsMessages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.comments.api.port.CommentsService;
import io.f12.notionlinkedblog.comments.api.response.ChildCommentDto;
import io.f12.notionlinkedblog.comments.api.response.CommentEditDto;
import io.f12.notionlinkedblog.comments.api.response.ParentsCommentDto;
import io.f12.notionlinkedblog.comments.domain.dto.CreateCommentDto;
import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import io.f12.notionlinkedblog.comments.service.port.CommentsRepository;
import io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

	private final CommentsRepository commentsRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public List<ParentsCommentDto> getCommentsByPostId(Long postId) {
		List<CommentsEntity> comments = commentsRepository.findByPostId(postId);
		HashMap<Long, ParentsCommentDto> parentsMap = new HashMap<>();
		Queue<ChildCommentDto> childQueue = new LinkedList<>();
		convertCommentsToDto(comments, parentsMap, childQueue);
		return convertDtosToList(parentsMap, childQueue);
	}

	public CommentEditDto createComments(Long postId, Long userId, CreateCommentDto commentDto) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.PostExceptionsMessages.POST_NOT_EXIST));
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.UserExceptionsMessages.USER_NOT_EXIST));

		CommentsEntity parentComment = null;
		if (isChild(commentDto)) {
			parentComment = commentsRepository.findById(commentDto.getParentCommentId())
				.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		}

		CommentsEntity builtComments = CommentsEntity.builder()
			.post(post)
			.content(commentDto.getComment())
			.user(user)
			.depth(commentDto.getDepth())
			.parent(parentComment)
			.build();

		CommentsEntity savedComments = commentsRepository.save(builtComments);

		CommentEditDto builtReturnDto = convertCommentsToCommentEditDto(savedComments, user);

		if (parentComment != null) {
			parentComment.addChildren(savedComments);
			builtReturnDto.setParentCommentId(savedComments.getParent().getId());
		}
		return builtReturnDto;
	}

	public CommentEditDto editComment(Long commentId, Long userId, String contents) {
		CommentsEntity comments = commentsRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		UserEntity user = comments.getUser();

		checkIsAuthor(userId, comments);
		comments.editComments(contents);
		return convertCommentsToCommentEditDto(comments, user);
	}

	public void removeComment(Long commentId, Long userId) {
		CommentsEntity comments = commentsRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException(COMMENT_NOT_EXIST));
		checkIsAuthor(userId, comments);
		commentsRepository.deleteById(commentId);
	}

	// 내부사용 매서드

	private boolean isSameUser(Long sessionUserId, Long databaseUserId) {
		return Objects.equals(sessionUserId, databaseUserId);
	}

	private static boolean isParent(CommentsEntity comments) {
		return comments.getDepth() == 0;
	}

	private static boolean isChild(CreateCommentDto commentDto) {
		return commentDto.getDepth().equals(1);
	}

	private void convertCommentsToDto(List<CommentsEntity> comments,
		HashMap<Long, ParentsCommentDto> parentsMap, Queue<ChildCommentDto> childQueue) {
		for (CommentsEntity comment : comments) {
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

	private static CommentEditDto convertCommentsToCommentEditDto(CommentsEntity comments, UserEntity user) {
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

	private void checkIsAuthor(Long userId, CommentsEntity comments) {
		if (!isSameUser(userId, comments.getUser().getId())) {
			throw new IllegalStateException(NOT_COMMENT_OWNER);
		}
	}

	private ParentsCommentDto createParentsCommentDto(CommentsEntity comment) {
		ParentsCommentDto parentsCommentDto = new ParentsCommentDto();
		return parentsCommentDto.createParentCommentDto(comment);
	}

	private ChildCommentDto createChildCommentDto(CommentsEntity comment) {
		ChildCommentDto childCommentDto = new ChildCommentDto();
		return childCommentDto.createChildCommentDto(comment);
	}
}
