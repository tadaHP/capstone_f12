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
	public PostSearchDto createPost(Long userId, String title, String content, String description,
		Boolean isPublic, MultipartFile multipartFile, List<String> hashtags) throws IOException;

	public PostSearchResponseDto getPostsByTitle(SearchRequestDto dto);

	public PostSearchResponseDto getPostByContent(SearchRequestDto dto);

	public PostSearchDto getPostDtoById(Long postId, Long userId);

	public PostSearchResponseDto getLatestPosts(Integer pageNumber);

	public PostSearchResponseDto getPopularityPosts(Integer pageNumber);

	public void removePost(Long postId, Long userId);

	//TODO: 추후 EditThumbnail 을 따로 만들어야 함
	public PostSearchDto editPost(Long postId, Long userId, PostEditDto postEditDto);

	public void likeStatusChange(Long postId, Long userId);

	public File readImageFile(String imageName) throws MalformedURLException;
}
