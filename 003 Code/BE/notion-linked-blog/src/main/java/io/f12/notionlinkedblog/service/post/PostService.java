package io.f12.notionlinkedblog.service.post;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.likes.dto.LikeSearchDto;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchResponseDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.domain.post.dto.ThumbnailReturnDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.like.LikeDataRepository;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class PostService {

	private final PostDataRepository postDataRepository;
	private final UserDataRepository userDataRepository;
	private final LikeDataRepository likeDataRepository;
	private final int pageSize = 20;

	public PostSearchDto createPost(Long userId, String title, String content, String description,
		Boolean isPublic, MultipartFile multipartFile) throws IOException {
		User findUser = userDataRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		
		String systemPath = System.getProperty("user.dir");

		String fileName = makeFileName();
		String fullPath = null;
		String newName = null;
		String requestThumbnailLink = null;

		if (multipartFile != null) {
			fullPath = getSavedDirectory(multipartFile, systemPath, fileName);
			multipartFile.transferTo(new File(fullPath));
			newName = fileName + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
			requestThumbnailLink = Endpoint.Api.REQUEST_IMAGE + newName;
		}

		Post post = Post.builder()
			.user(findUser)
			.title(title)
			.content(content)
			.storedThumbnailPath(fullPath)
			.thumbnailName(newName)
			.viewCount(0L)
			.description(description)
			.isPublic(isPublic)
			.build();

		Post savedPost = postDataRepository.save(post);

		return PostSearchDto.builder()
			.postId(savedPost.getId())
			.title(savedPost.getTitle())
			.content(savedPost.getContent())
			.viewCount(savedPost.getViewCount())
			.likes(0)
			.requestThumbnailLink(requestThumbnailLink)
			.description(savedPost.getDescription())
			.createdAt(savedPost.getCreatedAt())
			.author(savedPost.getUser().getUsername())
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

		String thumbnailLink = null;
		Integer likeSize = null;
		Integer commentsSize = null;

		if (post.getThumbnailName() != null) {
			thumbnailLink = Endpoint.Api.REQUEST_IMAGE + post.getThumbnailName();
		}
		if (post.getLikes() != null) {
			likeSize = post.getLikes().size();
		}
		if (post.getComments() != null) {
			commentsSize = post.getComments().size();
		}

		return PostSearchDto.builder()
			.postId(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.viewCount(post.getViewCount())
			.requestThumbnailLink(thumbnailLink)
			.description(post.getDescription())
			.createdAt(post.getCreatedAt())
			.countOfComments(commentsSize)
			.author(post.getUser().getUsername())
			.likes(likeSize)
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

	//TODO: 추후 EditThumbnail 을 따로 만들어야 함
	public void editPostContent(Long postId, Long userId, PostEditDto postEditDto) {
		Post changedPost = postDataRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(changedPost.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		changedPost.editPost(postEditDto.getTitle(), postEditDto.getContent());
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

	public ThumbnailReturnDto readImageFile(String imageName) throws MalformedURLException {
		String thumbnailPathWithName = postDataRepository.findThumbnailPathWithName(imageName);
		if (thumbnailPathWithName == null) {
			throw new IllegalArgumentException(IMAGE_NOT_EXIST);
		}
		UrlResource urlResource = new UrlResource("file:" + thumbnailPathWithName);
		return ThumbnailReturnDto.builder()
			.thumbnailPath(thumbnailPathWithName)
			.image(urlResource)
			.build();
	}

	// 내부 사용 매서드
	private List<PostSearchDto> convertPostToPostDto(List<Post> posts) {
		return posts.stream().map(p -> {
			String thumbnailLink = null;
			if (p.getThumbnailName() != null) {
				thumbnailLink = Endpoint.Api.REQUEST_IMAGE + p.getThumbnailName();
			}
			Integer likeSize = null;
			if (p.getLikes() != null) {
				likeSize = p.getLikes().size();
			}
			Integer commentsSize = null;
			if (p.getComments() != null) {
				commentsSize = p.getComments().size();
			}
			return PostSearchDto.builder()
				.postId(p.getId())
				.title(p.getTitle())
				.content(p.getContent())
				.viewCount(p.getViewCount())
				.likes(likeSize)
				.requestThumbnailLink(thumbnailLink)
				.description(p.getDescription())
				.createdAt(p.getCreatedAt())
				.countOfComments(commentsSize)
				.author(p.getUser().getUsername())
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

	private String makeFileName() {
		Date now = new Date();
		SimpleDateFormat savedDataFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SS");
		return savedDataFormat.format(now);
	}

	private String getSavedDirectory(MultipartFile multipartFile, String systemPath, String fileName) {
		return
			systemPath + "/" + fileName + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
	}
}
