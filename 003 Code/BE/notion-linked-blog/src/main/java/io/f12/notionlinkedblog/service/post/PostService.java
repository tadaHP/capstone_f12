package io.f12.notionlinkedblog.service.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostCreateDto;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchResponseDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

	private final PostDataRepository postDataRepository;
	private final UserDataRepository userDataRepository;
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

	public PostSearchResponseDto getPostsByTitle(SearchRequestDto dto) {
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);
		Slice<Post> posts = postDataRepository.findByTitle(dto.getParam(), paging);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return PostSearchResponseDto.builder()
			.pageSize(posts.getSize())
			.pageNow(posts.getNumber())
			.posts(postSearchDtos)
			.build();
	}

	public PostSearchResponseDto getPostByContent(SearchRequestDto dto) {
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);
		Slice<Post> posts = postDataRepository.findByContent(dto.getParam(), paging);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return PostSearchResponseDto.builder()
			.pageSize(posts.getSize())
			.pageNow(posts.getNumber())
			.posts(postSearchDtos)
			.build();
	}

	public PostSearchDto getPostDtoById(Long id) {
		Post post = postDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		return PostSearchDto.builder()
			.username(post.getUser().getUsername())
			.title(post.getTitle())
			.content(post.getContent())
			.thumbnail(post.getThumbnail())
			.viewCount(post.getViewCount())
			.build();
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

	private List<PostSearchDto> convertPostToPostDto(Slice<Post> posts) {
		Slice<PostSearchDto> mappedPosts = posts.map(p -> {
			return PostSearchDto.builder()
				.username(p.getUser().getUsername())
				.title(p.getTitle())
				.content(p.getContent())
				.thumbnail(p.getThumbnail())
				.viewCount(p.getViewCount())
				.build();
		});
		return mappedPosts.getContent();
	}

	private boolean isSame(Long idA, Long idB) {
		return !idA.equals(idB);
	}

}
