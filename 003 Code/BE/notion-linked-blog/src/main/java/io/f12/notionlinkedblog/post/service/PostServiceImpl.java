package io.f12.notionlinkedblog.post.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import io.f12.notionlinkedblog.common.domain.AwsBucket;
import io.f12.notionlinkedblog.common.domain.EntityConverter;
import io.f12.notionlinkedblog.hashtag.exception.NoHashtagException;
import io.f12.notionlinkedblog.like.domain.dto.LikeSearchDto;
import io.f12.notionlinkedblog.like.infrastructure.LikeEntity;
import io.f12.notionlinkedblog.like.service.port.LikeRepository;
import io.f12.notionlinkedblog.post.api.port.PostService;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.api.response.PostSearchResponseDto;
import io.f12.notionlinkedblog.post.api.response.PostThumbnailDto;
import io.f12.notionlinkedblog.post.domain.dto.PostEditDto;
import io.f12.notionlinkedblog.post.domain.dto.SearchRequestDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.HashtagService;
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
	private final HashtagService hashtagService;
	private final AmazonS3Client amazonS3Client;
	private final EntityConverter entityConverter;
	private final AwsBucket awsBucket;
	@Value("${application.bucket.name}")
	private String bucket;
	private final int pageSize = 20;
	@Value("${server.url}")
	private String serverUrl;

	@Override
	public PostSearchDto createPost(Long userId, String title, String content, String description,
		Boolean isPublic, MultipartFile multipartFile, List<String> hashtags) throws IOException {
		UserEntity findUser = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		PostEntity post = PostEntity.builder()
			.user(findUser)
			.title(title)
			.content(content)
			.viewCount(0L)
			.description(description)
			.isPublic(isPublic)
			.build();

		PostEntity savedPost = hashtagService.addHashtags(hashtags, postRepository.save(post));
		List<String> hashtagList = entityConverter.getHashtagsFromPost(savedPost);
		String url = null;

		if (multipartFile != null) {
			String fileName = makeThumbnailName(multipartFile, savedPost);
			url = awsBucket.makeFileUrl(fileName);

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(multipartFile.getContentType());
			metadata.setContentLength(multipartFile.getSize());
			amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);

			savedPost.editThumbnailName(fileName);
		} else {
			url = entityConverter.getDefaultThumbnail();
		}

		return PostSearchDto.builder()
			.isLiked(false)
			.postId(savedPost.getId())
			.title(savedPost.getTitle())
			.content(savedPost.getContent())
			.viewCount(savedPost.getViewCount())
			.likes(0)
			.requestThumbnailLink(url)
			.description(savedPost.getDescription())
			.createdAt(savedPost.getCreatedAt())
			.author(savedPost.getUser().getUsername())
			.avatar(awsBucket.makeFileUrl(findUser.getProfile()))
			.hashtags(hashtagList)
			.build();
	}

	@Override
	public PostSearchResponseDto getPostsByTitle(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = querydslPostRepository.findPostIdsByTitle(dto.getParam(), paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);

		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public PostSearchResponseDto getPostByContent(SearchRequestDto dto) { // DONE
		PageRequest paging = PageRequest.of(dto.getPageNumber(), pageSize);

		List<Long> ids = querydslPostRepository.findPostIdsByContent(dto.getParam(), paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);

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
			thumbnailLink = awsBucket.makeFileUrl(post.getThumbnailName());
		} else {
			thumbnailLink = entityConverter.getDefaultThumbnail();
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

		List<String> hashtagList = entityConverter.getHashtagsFromPost(post);

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
			.avatar(awsBucket.makeFileUrl(post.getUser().getProfile()))
			.hashtags(hashtagList)
			.build();
	}

	@Override
	public PostSearchResponseDto getLatestPosts(Integer pageNumber) { //
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = querydslPostRepository.findLatestPostIdsByCreatedAtDesc(paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(ids);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);
		return buildPostSearchResponseDto(paging, postSearchDtos, ids.size());
	}

	@Override
	public PostSearchResponseDto getPopularityPosts(Integer pageNumber) {
		PageRequest paging = PageRequest.of(pageNumber, pageSize);
		List<Long> ids = querydslPostRepository.findPopularityPostIdsByViewCountAtDesc(paging);
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(ids);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);
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

	@Override
	public PostSearchResponseDto getByHashtagOrderByTrend(String hashtagName, Integer pageNumber) {
		List<Long> postIds = null;
		try {
			postIds = hashtagService.getPostIdsByHashtag(hashtagName);
		} catch (NoHashtagException e) {
			return PostSearchResponseDto.builder()
				.pageSize(0)
				.pageNow(pageNumber)
				.posts(new ArrayList<>())
				.elementsSize(0)
				.build();
		}

		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByTrend(postIds);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);
		PageRequest paging = PageRequest.of(pageNumber, pageSize);

		return buildPostSearchResponseDto(paging, postSearchDtos, posts.size());
	}

	@Override
	public PostSearchResponseDto getByHashtagOrderByLatest(String hashtagName, Integer pageNumber) {
		List<Long> postIds = null;
		try {
			postIds = hashtagService.getPostIdsByHashtag(hashtagName);
		} catch (NoHashtagException e) {
			return PostSearchResponseDto.builder()
				.pageSize(0)
				.pageNow(pageNumber)
				.posts(new ArrayList<>())
				.elementsSize(0)
				.build();
		}
		List<PostEntity> posts = querydslPostRepository.findByPostIdsJoinWithUserAndLikeOrderByLatest(postIds);

		List<PostSearchDto> postSearchDtos = entityConverter.convertPostToPostDto(posts);
		PageRequest paging = PageRequest.of(pageNumber, pageSize);

		return buildPostSearchResponseDto(paging, postSearchDtos, posts.size());
	}

	@Override
	public PostSearchDto editPost(Long postId, Long userId, PostEditDto postEditDto) {
		PostEntity changedPost = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(changedPost.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}
		changedPost.editPost(postEditDto.getTitle(), postEditDto.getContent());
		PostEntity savedPost = hashtagService.editHashtags(postEditDto.getHashtags(), changedPost);
		List<String> savedHashtagList = entityConverter.getHashtagsFromPost(savedPost);

		return PostSearchDto.builder()
			.isLiked(false)
			.postId(savedPost.getId())
			.title(savedPost.getTitle())
			.content(savedPost.getContent())
			.viewCount(savedPost.getViewCount())
			.likes(0)
			.requestThumbnailLink(awsBucket.makeFileUrl(savedPost.getThumbnailName()))
			.description(savedPost.getDescription())
			.createdAt(savedPost.getCreatedAt())
			.author(savedPost.getUser().getUsername())
			.avatar(awsBucket.makeFileUrl(changedPost.getUser().getProfile()))
			.hashtags(savedHashtagList)
			.build();
	}

	@Override
	public PostThumbnailDto editThumbnail(Long postId, Long userId, MultipartFile multipartFile) throws IOException {
		PostEntity changedPost = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		if (isSame(changedPost.getUser().getId(), userId)) {
			throw new IllegalStateException(WRITER_USER_NOT_MATCH);
		}

		String fileName = makeThumbnailName(multipartFile, changedPost);
		String url = awsBucket.makeFileUrl(fileName);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(multipartFile.getContentType());
		metadata.setContentLength(multipartFile.getSize());
		amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);

		changedPost.editThumbnailName(fileName);

		return PostThumbnailDto.builder()
			.url(url)
			.build();
	}

	@Override
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

	// 내부 사용 매서드

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

	private String makeThumbnailName(MultipartFile imageFile, PostEntity post) {
		return "thumbnail/" + post.getId() + "." + StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
	}
}
