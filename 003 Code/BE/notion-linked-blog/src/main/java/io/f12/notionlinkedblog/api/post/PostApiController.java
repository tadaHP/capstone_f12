package io.f12.notionlinkedblog.api.post;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.post.dto.PostCreateDto;
import io.f12.notionlinkedblog.domain.post.dto.PostEditDto;
import io.f12.notionlinkedblog.domain.post.dto.PostSearchDto;
import io.f12.notionlinkedblog.domain.post.dto.SearchRequestDto;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.service.post.PostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.POST)
public class PostApiController {

	private final PostService postService;

	//TODO: 현재 Series 기능 미포함
	@PutMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostSearchDto createPost(@AuthenticationPrincipal LoginUser loginUser,
		@RequestBody PostCreateDto postCreateDto) {
		return postService.createPost(loginUser.getId(), postCreateDto);
	}

	@GetMapping("/{id}")
	public PostSearchDto getPostsById(@PathVariable("id") Long id) {
		return postService.getPostDtoById(id);
	}

	@GetMapping("/title")
	public List<PostSearchDto> searchPostsByTitle(@RequestBody @Validated SearchRequestDto titleDto) {
		return postService.getPostsByTitle(titleDto.getParam());
	}

	@GetMapping("/content")
	public List<PostSearchDto> searchPostsByContent(@RequestBody @Validated SearchRequestDto contentDto) {
		return postService.getPostByContent(contentDto.getParam());
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.FOUND)
	public String editPost(@PathVariable("id") Long postId, @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody PostEditDto editInfo) {
		postService.editPost(postId, loginUser.getId(), editInfo);
		return Endpoint.Api.POST + "/" + postId;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(@PathVariable("id") Long postId, @AuthenticationPrincipal LoginUser loginUser) {
		postService.removePost(postId, loginUser.getId());
	}
}
