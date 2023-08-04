package io.f12.notionlinkedblog.api.oauth;

import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.NotionValidateMessages.*;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.domain.oauth.dto.notion.NotionOAuthLinkDto;
import io.f12.notionlinkedblog.exceptions.exception.AuthFailureException;
import io.f12.notionlinkedblog.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.exceptions.exception.TokenAvailabilityFailureException;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
import io.f12.notionlinkedblog.service.notion.NotionService;
import io.f12.notionlinkedblog.service.notion.UpdateNotionSchedule;
import io.f12.notionlinkedblog.service.oauth.NotionOauthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Endpoint.Api.NOTION)
@RequiredArgsConstructor
@Slf4j
public class OAuthApiController {

	private final NotionOauthService notionOauthService;
	private final NotionService notionService;
	private final UpdateNotionSchedule test;

	//TODO: 추후 SCOPE 추가로 보안목적 달성필요
	@GetMapping("/startAuth")
	@Operation(summary = "Notion OAuth2.0 연동 api", description = "code 값을 백엔드단에 전송")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프론트에서 해당 페이지로 이동하여 code 값을 받아서 전송")
	})
	public NotionOAuthLinkDto requestNotionAuthLink(
		@NotNull @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		return notionOauthService.getNotionAuthSite();
	}

	// TODO: SCOPE 추가 이후 state required = true 로 변경
	@GetMapping("/auth")
	@Operation(summary = "Notion OAuth2.0 연동 api", description = "notion 인증 이후 code 값을 백엔드단에 전송")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "노션 연동 성공")
	})
	public void notionOAuth(@RequestParam(value = "code", required = false) String code,
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "state", required = false) String state,
		@NotNull @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) throws
		AuthFailureException,
		TokenAvailabilityFailureException, NotionAuthenticationException {
		isError(error);
		String accessToken = notionOauthService.saveAccessToken(code, loginUser.getUser().getId());
		List<String> everyPages = notionService.getEveryPages(accessToken);
		notionService.initEveryPages(everyPages, loginUser.getUser().getId(), accessToken);
	}

	@DeleteMapping
	@Operation(summary = "이미 인증된 Notion OAuth 토큰을 삭제", description = "기존에 존재하는 토큰을 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공")
	})
	public void removeNotionAccessToken(
		@NotNull @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser) {
		notionOauthService.removeAccessToken(loginUser.getUser().getId());
	}

	@GetMapping("/test")
	@Operation(summary = "일정시간 마다 동기화 확인 매서드, Test용", description = "노션 연동된 Post들 내용 업데이트")
	public void test() throws NotionAuthenticationException {
		test.updateNotionData();
	}

	private void isError(String error) throws AuthFailureException {
		if (error != null) {
			throw new AuthFailureException(NOT_ALLOW_ACCESS);
		}
	}

}
