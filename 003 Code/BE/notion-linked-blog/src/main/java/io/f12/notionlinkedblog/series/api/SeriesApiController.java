package io.f12.notionlinkedblog.series.api;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.common.Endpoint;
import io.f12.notionlinkedblog.common.domain.CommonErrorResponse;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.series.api.port.SeriesService;
import io.f12.notionlinkedblog.series.api.response.SeriesCreateResponseDto;
import io.f12.notionlinkedblog.series.api.response.SeriesDetailSearchDto;
import io.f12.notionlinkedblog.series.api.response.SeriesSimpleSearchDto;
import io.f12.notionlinkedblog.series.api.response.UserSeriesDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesCreateDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesPostRemoveDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesRemoveDto;
import io.f12.notionlinkedblog.series.domain.dto.request.TitleEditDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "SeriesEntity", description = "시리즈 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Api.SERIES)
public class SeriesApiController {

	private final SeriesService seriesService;

	@PostMapping
	@Operation(summary = "시리즈 생성", description = "시리즈를 생성합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = SeriesCreateResponseDto.class))),
		@ApiResponse(responseCode = "404", description = "조회실패",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public SeriesCreateResponseDto createSeries(@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody SeriesCreateDto seriesCreateDto) {
		checkSameUser(seriesCreateDto.getUserId(), loginUser);
		return seriesService.createSeries(seriesCreateDto);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "시리즈 삭제", description = "시리즈를 삭제합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공")
	})
	public void removeSeries(@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody SeriesRemoveDto seriesRemoveDto) {
		checkSameUser(seriesRemoveDto.getUserId(), loginUser);
		seriesService.removeSeries(seriesRemoveDto);
	}

	@GetMapping("/{userId}")
	@Operation(summary = "시리즈 조회 - userId 로 조회", description = "사용자가 특정 포스트를 시리즈에 포함하기 위해 선택할 때 조회용 API 입니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = UserSeriesDto.class))),
		@ApiResponse(responseCode = "404", description = "조회실패",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public List<UserSeriesDto> getSeriesByUserId(@PathVariable("userId") Long userId) {
		return seriesService.getSeriesByUserId(userId);
	}

	@GetMapping("/simple/{id}")
	@Operation(summary = "시리즈 조회 - 간단조회", description = "시리즈의 id, 이름, 포스트의 아이디, 이름 만 간단하게 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = SeriesSimpleSearchDto.class))),
		@ApiResponse(responseCode = "404", description = "조회실패",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public SeriesSimpleSearchDto seriesSimpleSearch(@PathVariable("id") Long id) {
		return seriesService.getSimpleSeriesInfo(id);
	}

	@GetMapping("/detail/desc/{id}/{page}")
	@Operation(summary = "시리즈 조회 - 상세조회, 내림차순", description = "시리즈와 포스트 정보를 상세히 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = SeriesDetailSearchDto.class))),
		@ApiResponse(responseCode = "404", description = "조회실패",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public SeriesDetailSearchDto seriesDetailSearchOrderByDesc(
		@PathVariable("id") Long id, @PathVariable("page") Integer page) {
		return seriesService.getDetailSeriesInfoOrderByDesc(id, page);
	}

	@GetMapping("/detail/asc/{id}/{page}")
	@Operation(summary = "시리즈 조회 - 상세조회, 오름차순", description = "시리즈와 포스트 정보를 상세히 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = SeriesDetailSearchDto.class))),
		@ApiResponse(responseCode = "404", description = "조회실패",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CommonErrorResponse.class)))
	})
	public SeriesDetailSearchDto seriesDetailSearchOrderByAsc(
		@PathVariable("id") Long id, @PathVariable("page") Integer page) {
		return seriesService.getDetailSeriesInfoOrderByAsc(id, page);
	}

	@DeleteMapping("/post")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "시리즈에서 포스트를 제외", description = "시리즈에 등록된 포스트를 제외합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = SeriesPostRemoveDto.class)))
	})
	public void removePostFromSeries(@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody SeriesPostRemoveDto removeDto) {
		checkSameUser(removeDto.getUserId(), loginUser);
		seriesService.removePostFrom(removeDto.getSeriesId(), removeDto.getPostId());
	}

	@PutMapping("/{series_id}/title")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "시리즈의 타이틀 변경", description = "시리즈 이름을 변경합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = TitleEditDto.class)))
	})
	public void editSeriesTitle(@PathVariable(name = "series_id") Long seriesId,
		@Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
		@RequestBody TitleEditDto editDto) {
		checkSameUser(editDto.getUserId(), loginUser);
		seriesService.editTitle(seriesId, editDto.getTitle());
	}

	private void checkSameUser(Long id, LoginUser loginUser) {

		if (!id.equals(loginUser.getUser().getId())) {
			throw new AccessDeniedException(USER_NOT_EXIST);
		}
	}

}
