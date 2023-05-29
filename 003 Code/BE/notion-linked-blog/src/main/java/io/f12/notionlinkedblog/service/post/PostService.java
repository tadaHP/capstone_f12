package io.f12.notionlinkedblog.service.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.likes.dto.LikeSearchDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostCreateDto;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchResponseDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.like.LikeDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

	private final PostDataRepository postDataRepository;
	private final UserDataRepository userDataRepository;
	private final LikeDataRepository likeDataRepository;
	private final int pageSize = 20;

	public PostSearchDto createPost(Long userId, PostCreateDto postCreateDto) {

		User findUser = userDataRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		Post post = Post.builder()
			.user(findUser)
			.title(postCreateDto.getTitle())
			.content(postCreateDto.getContent())
			.thumbnail(postCreateDto.getContent())
			.viewCount(0L)
			.build();

		Post savedPost = postDataRepository.save(post);

		return PostSearchDto.builder()
			.username(savedPost.getUser().getUsername())
			.title(savedPost.getTitle())
			.content(savedPost.getContent())
			.thumbnail(savedPost.getThumbnail())
			.viewCount(savedPost.getViewCount())
			.build();
	}

	public PostSearchResponseDto getPostsByTitle(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = postDataRepository.findPostIdsByTitle(dto.getParam(), paging);
		List<Post> posts = postDataRepository.findByIds(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	public PostSearchResponseDto getPostByContent(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = postDataRepository.findPostIdsByContent(dto.getParam(), paging);
		List<Post> posts = postDataRepository.findByIds(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	public PostSearchDto getPostDtoById(Long id) { //DONE
		Post post = postDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		post.addViewCount();

		return PostSearchDto.builder()
			.postId(post.getId())
			.username(post.getUser().getUsername())
			.title(post.getTitle())
			.content(post.getContent())
			.thumbnail(post.getThumbnail())
			.viewCount(post.getViewCount())
			.likes(post.getLikes().size())
			.build();
	}

	public PostSearchResponseDto getLatestPosts(Integer pageNumber) { //
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = postDataRepository.findLatestPostIdsByCreatedAtDesc(paging);
		List<Post> posts = postDataRepository.findByIds(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);
		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	public PostSearchResponseDto getPopularityPosts(Integer pageNumber) {
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = postDataRepository.findPopularityPostIdsByViewCountAtDesc(paging);
		List<Post> posts = postDataRepository.findByIds(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);
		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	public void removePost(Long postId, Long userId) {
		Post post = postDataRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(post.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		postDataRepository.deleteById(postId);

	}

	public void editPost(Long postId, Long userId, PostEditDto postEditDto) {
		Post changedPost = postDataRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(changedPost.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		changedPost.editPost(postEditDto.getTitle(), postEditDto.getContent(), postEditDto.getThumbnail());
	}

	public void likeStatusChange(Long postId, Long userId) {
		Post post = postDataRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		User user = userDataRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		Optional<LikeSearchDto> dto = likeDataRepository.findByUserIdAndPostId(userId, postId);

		if (dto.isPresent()) {
			likeDataRepository.removeById(dto.get().getLikeId());
		} else {
			likeDataRepository.save(Like.builder()
				.user(user)
				.post(post)
				.build());
		}
	}

	private List<PostSearchDto> convertPostToPostDto(List<Post> posts) {
		return posts.stream().map(p -> {
			return PostSearchDto.builder()
				.postId(p.getId())
				.username(p.getUser().getUsername())
				.title(p.getTitle())
				.content(p.getContent())
				.thumbnail(p.getThumbnail())
				.viewCount(p.getViewCount())
				.likes(p.getLikes().size())
				.build();
		}).collect(Collectors.toList());
	}

	private PostSearchResponseDto buildPostSearchResponseDto(PageRequest paging, List<PostSearchDto> dto, int size) {
		return PostSearchResponseDto.builder()
			.pageSize(paging.getPageSize())
			.pageNow(paging.getPageNumber())
			.posts(dto)
			.elementsSize(size)
			.build();
	}

	private boolean isSame(Long idA, Long idB) {
		return !idA.equals(idB);
	}

}
