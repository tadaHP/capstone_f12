package io.f12.notionlinkedblog.post.api.port;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.api.response.PostSearchResponseDto;
import io.f12.notionlinkedblog.post.api.response.PostThumbnailDto;
import io.f12.notionlinkedblog.post.domain.dto.PostEditDto;
import io.f12.notionlinkedblog.post.domain.dto.SearchRequestDto;

public interface PostService {
	PostSearchDto createPost(Long userId, String title, String content, String description,
		Boolean isPublic, MultipartFile multipartFile, List<String> hashtags) throws IOException;

	PostSearchResponseDto getPostsByTitle(SearchRequestDto dto);

	PostSearchResponseDto getPostByContent(SearchRequestDto dto);

	PostSearchDto getPostDtoById(Long postId, Long userId);

	PostSearchResponseDto getLatestPosts(Integer pageNumber);

	PostSearchResponseDto getPopularityPosts(Integer pageNumber);

	void removePost(Long postId, Long userId);
	
	PostSearchDto editPost(Long postId, Long userId, PostEditDto postEditDto);

	void likeStatusChange(Long postId, Long userId);

	PostThumbnailDto editThumbnail(Long postId, Long userId, MultipartFile multipartFile) throws IOException;

	PostSearchResponseDto getByHashtagOrderByTrend(String hashtagName, Integer pageNumber);

	PostSearchResponseDto getByHashtagOrderByLatest(String hashtagName, Integer pageNumber);
}
