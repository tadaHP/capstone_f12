package io.f12.notionlinkedblog.api.comments;

import static org.springframework.http.MediaType.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.comments.dto.CommentSearchDto;
import io.f12.notionlinkedblog.domain.comments.dto.CreateCommentDto;
import io.f12.notionlinkedblog.domain.comments.dto.EditCommentDto;
import io.f12.notionlinkedblog.domain.common.CommonErrorResponse;
import io.f12.notionlinkedblog.security.common.dto.AuthenticationFailureDto;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.service.comments.CommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comments", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.COMMENTS)
public class CommentsApiController {

	private final CommentsService commentsService;

	@GetMapping
	@Operation(summary = "postId 로 댓글 조회", description = "postId 에 해당하는 댓글들 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보변경 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE)),
		@ApiResponse(responseCode = "401", description = "회원 미 로그인",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthenticationFailureDto.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(댓글) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthenticationFailureDto.class)))
	})
	// TODO: 추후 리턴타입 감싸기 필요
	public List<CommentSearchDto> getComments(@PathVariable("id") Long postId) {
		return commentsService.getCommentsByPostId(postId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "댓글 생성", description = "postId에 해당하는 댓글 생성")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 정보변경 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommentSearchDto.class))),
		@ApiResponse(responseCode = "401", description = "회원 미 로그인",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthenticationFailureDto.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(포스트) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(DB에 저장된 유저) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(포스트) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(댓글, 부모댓글 ID 불일치) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public CommentSearchDto createComments(@PathVariable("id") Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated CreateCommentDto commentDto) {
		return commentsService.createComments(postId, loginUser.getUser().getId(), commentDto);
	}

	@PutMapping
	@ResponseStatus(HttpStatus.FOUND)
	@Operation(summary = "댓글 수정", description = "commentsId 에 해당하는 댓글 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "302", description = "댓글 변경 성공",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommentSearchDto.class))),
		@ApiResponse(responseCode = "401", description = "회원 미 로그인",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthenticationFailureDto.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(댓글) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "댓글 수정자와 댓글 소유자 불일치",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public CommentSearchDto editComments(@PathVariable("id") Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody @Validated EditCommentDto commentDto) {
		return commentsService.editComment(commentId, loginUser.getUser().getId(), commentDto.getComment());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "댓글 삭제", description = "commentId에 해당하는 댓글 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "회원 미 로그인",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthenticationFailureDto.class))),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 리소스(댓글) 접근",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class))),
		@ApiResponse(responseCode = "301", description = "댓글 삭제자와 댓글 소유자 불일치",
			content = @Content(mediaType = APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public void removeComments(@PathVariable("id") Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		commentsService.removeComment(commentId, loginUser.getUser().getId());
	}

}
