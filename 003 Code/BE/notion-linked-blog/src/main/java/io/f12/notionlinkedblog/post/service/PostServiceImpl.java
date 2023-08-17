package io.f12.notionlinkedblog.post.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages;
import io.f12.notionlinkedblog.like.domain.dto.LikeSearchDto;
import io.f12.notionlinkedblog.like.infrastructure.LikeEntity;
import io.f12.notionlinkedblog.like.service.port.LikeRepository;
import io.f12.notionlinkedblog.post.api.port.PostService;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.api.response.PostSearchResponseDto;
import io.f12.notionlinkedblog.post.domain.dto.PostEditDto;
import io.f12.notionlinkedblog.post.domain.dto.SearchRequestDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final QuerydslPostRepository querydslPostRepository;
	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final int pageSize = 20;
	@Value("${server.url}")
	private String serverUrl;

	@Override
	public PostSearchDto createPost(Long userId, String title, String content, String description,
		Boolean isPublic, MultipartFile multipartFile) throws IOException {
		UserEntity findUser = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		String systemPath = System.getProperty("user.dir");

		String fileName = makeThumbnailFileName();
		String fullPath = null;
		String newName = null;
		String requestThumbnailLink = null;
		String avatar;

		if (multipartFile != null) {
			fullPath = getSavedDirectory(multipartFile, systemPath, fileName);
			multipartFile.transferTo(new File(fullPath));
			newName = fileName + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
			requestThumbnailLink = Endpoint.Api.REQUEST_THUMBNAIL_IMAGE + newName;
		}

		PostEntity post = PostEntity.builder()
			.user(findUser)
			.title(title)
			.content(content)
			.storedThumbnailPath(fullPath)
			.thumbnailName(newName)
			.viewCount(0L)
			.description(description)
			.isPublic(isPublic)
			.build();

		PostEntity savedPost = postRepository.save(post);

		return PostSearchDto.builder()
			.isLiked(false)
			.postId(savedPost.getId())
			.title(savedPost.getTitle())
			.content(savedPost.getContent())
			.viewCount(savedPost.getViewCount())
			.likes(0)
			.requestThumbnailLink(requestThumbnailLink)
			.description(savedPost.getDescription())
			.createdAt(savedPost.getCreatedAt())
			.author(savedPost.getUser().getUsername())
			.avatar(getAvatarRequestUrl(userId))
			.build();
	}

	@Override
	public PostSearchResponseDto getPostsByTitle(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = querydslPostRepository.findPostIdsByTitle(dto.getParam(), paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public PostSearchResponseDto getPostByContent(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = querydslPostRepository.findPostIdsByContent(dto.getParam(), paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);

		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public PostSearchDto getPostDtoById(Long postId, Long userId) { //DONE
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		post.addViewCount();

		String thumbnailLink = null;
		Integer likeSize = null;
		Integer commentsSize = null;
		LikeSearchDto likeInfo = null;

		if (post.getThumbnailName() != null) {
			thumbnailLink = Endpoint.Api.REQUEST_THUMBNAIL_IMAGE + post.getThumbnailName();
		}
		if (post.getLikes() != null) {
			likeSize = post.getLikes().size();
		}
		if (post.getComments() != null) {
			commentsSize = post.getComments().size();
		}
		if (userId != null) {
			likeInfo = likeRepository.findByUserIdAndPostId(userId, postId)
				.orElse(null);
		}

		return PostSearchDto.builder()
			.isLiked(likeInfo != null)
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
			.avatar(getAvatarRequestUrl(post.getUser().getId()))
			.build();
	}

	@Override
	public PostSearchResponseDto getLatestPosts(Integer pageNumber) { //
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = querydslPostRepository.findLatestPostIdsByCreatedAtDesc(paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);
		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public PostSearchResponseDto getPopularityPosts(Integer pageNumber) {
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = querydslPostRepository.findPopularityPostIdsByViewCountAtDesc(paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = convertPostToPostDto(posts);
		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public void removePost(Long postId, Long userId) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(post.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		postRepository.deleteById(postId);

	}

	//TODO: 추후 EditThumbnail 을 따로 만들어야 함
	public PostSearchDto editPostContent(Long postId, Long userId, PostEditDto postEditDto) {
		PostEntity changedPost = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(changedPost.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		changedPost.editPost(postEditDto.getTitle(), postEditDto.getContent());

		return PostSearchDto.builder()
			.isLiked(false)
			.postId(changedPost.getId())
			.title(changedPost.getTitle())
			.content(changedPost.getContent())
			.viewCount(changedPost.getViewCount())
			.likes(0)
			.requestThumbnailLink(Endpoint.Api.REQUEST_THUMBNAIL_IMAGE + changedPost.getThumbnailName())
			.description(changedPost.getDescription())
			.createdAt(changedPost.getCreatedAt())
			.author(changedPost.getUser().getUsername())
			.avatar(getAvatarRequestUrl(userId))
			.build();
	}

	public void likeStatusChange(Long postId, Long userId) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		Optional<LikeSearchDto> dto = likeRepository.findByUserIdAndPostId(userId, postId);

		if (dto.isPresent()) {
			likeRepository.removeById(dto.get().getLikeId());
		} else {
			likeRepository.save(LikeEntity.builder()
				.user(user)
				.post(post)
				.build());
		}
	}

	public File readImageFile(String imageName) throws MalformedURLException {
		String thumbnailPathWithName = postRepository.findThumbnailPathWithName(imageName);
		if (thumbnailPathWithName == null) {
			throw new IllegalArgumentException(ExceptionMessages.UserExceptionsMessages.IMAGE_NOT_EXIST);
		}
		return new File(thumbnailPathWithName);
	}

	// 내부 사용 매서드
	private List<PostSearchDto> convertPostToPostDto(List<PostEntity> posts) {
		return posts.stream().map(p -> {
			String thumbnailLink = null;
			Integer commentsSize = null;
			Integer likeSize = null;

			if (p.getThumbnailName() != null) {
				thumbnailLink = Endpoint.Api.REQUEST_THUMBNAIL_IMAGE + p.getThumbnailName();
			}
			if (p.getLikes() != null) {
				likeSize = p.getLikes().size();
			}

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
				.avatar(getAvatarRequestUrl(p.getUser().getId()))
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

	private String makeThumbnailFileName() {
		Date now = new Date();
		SimpleDateFormat savedDataFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SS");
		return savedDataFormat.format(now);
	}

	private String getSavedDirectory(MultipartFile multipartFile, String systemPath, String fileName) {
		return
			systemPath + "\\" + fileName + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
	}

	private String getAvatarRequestUrl(Long userId) {
		return serverUrl + Endpoint.Api.USER + "/profile/" + userId;
	}
}
