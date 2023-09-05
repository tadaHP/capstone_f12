package io.f12.notionlinkedblog.post.api.port;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.api.response.PostSearchResponseDto;
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

	//TODO: 추후 EditThumbnail 을 따로 만들어야 함
	PostSearchDto editPost(Long postId, Long userId, PostEditDto postEditDto);

	void likeStatusChange(Long postId, Long userId);

	File readImageFile(String imageName) throws MalformedURLException;

	PostSearchResponseDto getByHashtagOrderByTrend(String hashtagName, Integer pageNumber);

	PostSearchResponseDto getByHashtagOrderByLatest(String hashtagName, Integer pageNumber);
}
