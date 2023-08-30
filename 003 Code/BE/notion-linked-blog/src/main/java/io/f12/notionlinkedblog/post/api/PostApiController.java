package io.f12.notionlinkedblog.post.api;

import static org.springframework.http.MediaType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.common.domain.CommonErrorResponse;
import io.f12.notionlinkedblog.post.api.port.PostService;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.api.response.PostSearchResponseDto;
import io.f12.notionlinkedblog.post.domain.dto.PostEditDto;
import io.f12.notionlinkedblog.post.domain.dto.SearchRequestDto;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.series.api.port.SeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PostEntity", description = "포스트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.POST)
@Slf4j
public class PostApiController {

	private final PostService postService;
	private final SeriesService seriesService;

	//TODO: 현재 SeriesEntity 기능 미포함
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "포스트 생성", description = "포스트를 생성합니다, 시리즈에 등록할 경우 Series의 Id를 입력해야 합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "포스트 생성 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다"))),
		@ApiResponse(responseCode = "400", description = "isPublic 값이 0, 1 이 아닌경우",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public PostSearchDto createPost(@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestPart(value = "thumbnail", required = false) MultipartFile file,
		@RequestPart(value = "title") String title,
		@RequestPart(value = "content") String content,
		@RequestPart(value = "description", required = false) String description,
		@RequestPart(value = "isPublic") String isPublic,
		@RequestPart(value = "seriesId", required = false) String seriesId,
		@RequestParam(value = "hashtags", required = false) List<String> hashtags
	) throws IOException {
		validateIsPublic(isPublic);
		PostSearchDto post = postService.createPost(loginUser.getUser().getId(), title, content, description,
			isPublic(isPublic), file, hashtags);
		if (seriesId != null) {
			seriesService.addPostTo(Long.parseLong(seriesId), post.getPostId());
		}
		return post;
	}

	@GetMapping("/{id}")
	@Operation(summary = "포스트 id로 포스트 조회", description = "id에 해당하는 포스트를 하나 가져옵니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포스트 조회 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다"))),
		@ApiResponse(responseCode = "400", description = "RequestDto 미존재",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "415", description = "필수 데이터 미존재",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public PostSearchDto getPostsById(@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@PathVariable("id") Long postId) {
		Long userId = null;
		try {
			userId = loginUser.getUser().getId();
		} catch (Exception e) {
			log.info("UserIsNotExist: {}", e.getMessage());
		}
		return postService.getPostDtoById(postId, userId);
	}

	@GetMapping("/title")
	@Operation(summary = "title 로 포스트 조회", description = "title 이 포함되어있는 포스트들을 가져옴")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포스트 조회 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchResponseDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다")))
	})
	public PostSearchResponseDto searchPostsByTitle(@RequestBody @Validated SearchRequestDto titleDto) {
		return postService.getPostsByTitle(titleDto);
	}

	@GetMapping("/content")
	@Operation(summary = "content 로 포스트 조회", description = "content 가 포함되어 있는 포스들을 가져옴")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포스트 조회 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchResponseDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다")))
	})
	public PostSearchResponseDto searchPostsByContent(@RequestBody @Validated SearchRequestDto contentDto) {
		return postService.getPostByContent(contentDto);
	}

	@GetMapping("/newest/{pageNumber}")
	@Operation(summary = "최신순으로 포스트 조회", description = "메인페이지에서 사용하는 API, 최신순으로 작성된 포스트들을 가져온다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포스트 조회 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchResponseDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다"))),
		@ApiResponse(responseCode = "400", description = "파라미터(페이지 번호) 미존재 혹은 파라미터 타입 오류",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public PostSearchResponseDto searchLatestPosts(@PathVariable Integer pageNumber) {
		return postService.getLatestPosts(pageNumber);
	}

	@GetMapping("/trend/{pageNumber}")
	@Operation(summary = "인기순으로 포스트 조회", description = "메인페이지에서 사용하는 API, 인기순으로 작성된 포스트들을 가져온다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포스트 조회 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = PostSearchResponseDto.class,
					description = "requestThumbnailLink 은 해당 API 로 이미지를 다시 요청해야 합니다"))),
		@ApiResponse(responseCode = "400", description = "파라미터(페이지 번호) 미존재 혹은 파라미터 타입 오류",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public PostSearchResponseDto searchPopularPosts(@PathVariable Integer pageNumber) {
		return postService.getPopularityPosts(pageNumber);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "포스트 수정", description = "id 에 해당하는 포스트 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "302", description = "포스트 수정 성공")
	})
	public PostSearchDto editPost(@PathVariable("id") Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated PostEditDto editInfo) {
		PostSearchDto postSearchDto = postService.editPost(postId, loginUser.getUser().getId(), editInfo);
		if (editInfo.getSeriesId() != null) {
			seriesService.addPostTo(editInfo.getSeriesId(), postSearchDto.getPostId());
		}
		return postSearchDto;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "포스트 삭제", description = "id에 해당하는 포스트 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "포스트 삭제 성공")
	})
	public void deletePost(@PathVariable("id") Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		postService.removePost(postId, loginUser.getUser().getId());
	}

	@PostMapping("/like/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "해당하는 PostEntity 에 LikeEntity 를 추가/삭제 합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "LikeEntity 상태 변경 성공"),
	})
	public void addLikeToPost(@PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		postService.likeStatusChange(postId, loginUser.getUser().getId());
	}

	@GetMapping("/thumbnail/{imageName}")
	@Operation(summary = "imageName 에 해당하는 이미지를 가져옵니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이미지 가져오기 성공",
			content = @Content(mediaType = "image/*")),
		@ApiResponse(responseCode = "401", description = "DB에 이미지 이름 저장 오류, 문의 요망",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public ResponseEntity<byte[]> getThumbnail(@PathVariable String imageName) throws IOException {
		File imageFile = postService.readImageFile(imageName);
		ResponseEntity<byte[]> result = null;

		try {
			HttpHeaders header = new HttpHeaders();
			header.add("Content-type", Files.probeContentType(imageFile.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(imageFile), header, HttpStatus.OK);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return result;
	}

	private void validateIsPublic(String isPublic) {
		if (!(isPublic.equals("0") || isPublic.equals("1"))) {
			throw new IllegalArgumentException("isPublic must be 0 or 1");
		}
	}

	private boolean isPublic(String isPublic) {
		return isPublic.equals("0");
	}
}
