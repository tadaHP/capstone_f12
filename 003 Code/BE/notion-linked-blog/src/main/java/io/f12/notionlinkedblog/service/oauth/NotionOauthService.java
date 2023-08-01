package io.f12.notionlinkedblog.service.oauth;

import static io.f12.notionlinkedblog.api.common.Endpoint.NotionAuth.*;
import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.NotionValidateMessages.*;
import static io.f12.notionlinkedblog.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.f12.notionlinkedblog.Component.OAtuh.NotionOAuthComponent;
import io.f12.notionlinkedblog.domain.oauth.NotionOauth;
import io.f12.notionlinkedblog.domain.oauth.dto.notion.NotionOAuthLinkDto;
import io.f12.notionlinkedblog.domain.oauth.dto.notion.accesstokendto.NotionAccessTokenDto;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.exceptions.exception.TokenAvailabilityFailureException;
import io.f12.notionlinkedblog.exceptions.runtimeexception.IllegalDatabaseStateException;
import io.f12.notionlinkedblog.repository.oauth.NotionOauthDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotionOauthService {
	private final NotionOAuthComponent notionOAuthComponent;
	private final UserDataRepository userDataRepository;
	private final NotionOauthDataRepository notionOauthDataRepository;

	public NotionOAuthLinkDto getNotionAuthSite() {
		return NotionOAuthLinkDto.builder()
			.authUrl(notionOAuthComponent.getAuthUrl())
			.build();
	}

	public void saveAccessToken(String code, Long userId) throws TokenAvailabilityFailureException {
		User user = userDataRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		boolean exist = isExist(userId);
		NotionAccessTokenDto notionAccessTokenDto = authNotion(code);

		saveToken(notionAccessTokenDto, userId, user, exist);

	}

	@Transactional
	public void removeAccessToken(Long userId) {
		notionOauthDataRepository.deleteNotionOauthByUserId(userId);
	}

	private void saveToken(NotionAccessTokenDto notionAccessTokenDto, Long userId, User user, boolean exist) throws
		TokenAvailabilityFailureException {
		if (exist) {
			changeExistAccessCode(notionAccessTokenDto, userId);
		} else {
			saveNewAccessCode(notionAccessTokenDto, user);
		}
	}

	private void changeExistAccessCode(NotionAccessTokenDto notionAccessTokenDto, Long userId)
		throws TokenAvailabilityFailureException {

		NotionOauth authenticationObject = notionOauthDataRepository.findNotionOauthByUserId(userId)
			.orElseThrow(() -> new IllegalDatabaseStateException(ACCESS_TOKEN_INVALID));

		if (!Objects.equals(authenticationObject.getBotId(), notionAccessTokenDto.getBotId())) {
			throw new TokenAvailabilityFailureException(TOKEN_AVAILABILITY_FAILURE);
		}
		authenticationObject.renewAccessToken(notionAccessTokenDto.getAccessToken());
	}

	private void saveNewAccessCode(NotionAccessTokenDto notionAccessTokenDto, User user) {
		NotionOauth save = notionOauthDataRepository.save(
			NotionOauth.builder()
				.botId(notionAccessTokenDto.getBotId())
				.accessToken(notionAccessTokenDto.getAccessToken())
				.user(user)
				.build());
	}

	private boolean isExist(Long userId) {
		return notionOauthDataRepository.findNotionOauthByUserId(userId).isPresent();
	}

	private NotionAccessTokenDto authNotion(String code) {
		//추후 WebClient 사용 으로 바꾸면 좋음
		Gson gson = new Gson();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("grant_type", "authorization_code");
		params.add("redirect_uri", notionOAuthComponent.getRedirectUrl());

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(notionOAuthComponent.getClientId(), notionOAuthComponent.getClientSecret());

		// 파라미터와 헤더 합치기
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> exchange = restTemplate.exchange(
			NOTION_CODE_TO_ACCESS_TOKEN,
			HttpMethod.POST,
			entity,
			String.class
		);
		String body = exchange.getBody();

		JsonObject parsed = JsonParser.parseString(body).getAsJsonObject();
		return gson.fromJson(parsed, NotionAccessTokenDto.class);

	}
}
