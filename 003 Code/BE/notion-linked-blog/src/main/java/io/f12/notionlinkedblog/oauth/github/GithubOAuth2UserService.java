package io.f12.notionlinkedblog.oauth.github;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import io.f12.notionlinkedblog.oauth.github.dto.GithubOauthEmailEntity;

@Service
public class GithubOAuth2UserService {
	private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

	private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

	private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

	private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new GithubOAuth2RequestEntityConverter();

	private RestOperations restOperations;

	public GithubOAuth2UserService() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		this.restOperations = restTemplate;
	}

	public GithubOauthEmailEntity loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		Assert.notNull(userRequest, "userRequest cannot be null");
		if (!StringUtils
			.hasText(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
			OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
				"Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
					+ userRequest.getClientRegistration().getRegistrationId(),
				null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
			.getUserNameAttributeName();
		if (!StringUtils.hasText(userNameAttributeName)) {
			OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
				"Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
					+ userRequest.getClientRegistration().getRegistrationId(),
				null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
		ResponseEntity<GithubOauthEmailEntity[]> responseEntity = getResponse(userRequest, request);

		return getPrimary(responseEntity.getBody());
	}

	private GithubOauthEmailEntity getPrimary(GithubOauthEmailEntity[] entities) {
		for (GithubOauthEmailEntity entity : entities) {
			if (entity.isPrimary()) {
				return entity;
			}
		}
		return null;
	}

	private ResponseEntity<GithubOauthEmailEntity[]> getResponse(OAuth2UserRequest userRequest,
		RequestEntity<?> request) {
		try {
			return this.restOperations.exchange(request, GithubOauthEmailEntity[].class);
		} catch (OAuth2AuthorizationException ex) {
			OAuth2Error oauth2Error = ex.getError();
			StringBuilder errorDetails = new StringBuilder();
			errorDetails.append("Error details: [");
			errorDetails.append("UserInfo Uri: ")
				.append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
			errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
			if (oauth2Error.getDescription() != null) {
				errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
			}
			errorDetails.append("]");
			oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
				"An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails.toString(),
				null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
		} catch (UnknownContentTypeException ex) {
			String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
				+ userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()
				+ "': response contains invalid content type '" + ex.getContentType().toString() + "'. "
				+ "The UserInfo Response should return a JSON object (content type 'application/json') "
				+ "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
				+ "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
				+ userRequest.getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, "
				+ "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
		} catch (RestClientException ex) {
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
				"An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
		}
	}
}
