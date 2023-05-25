package io.f12.notionlinkedblog.config;

import static io.f12.notionlinkedblog.api.common.Endpoint.Api.*;

import java.util.Optional;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import io.f12.notionlinkedblog.security.login.ajax.filter.AjaxEmailPasswordAuthenticationFilter;
import io.f12.notionlinkedblog.security.login.check.filter.LoginStatusCheckingFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	private final ApplicationContext applicationContext;

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("F12")
			.pathsToMatch("/api/**")
			.addOpenApiCustomiser(springSecurityLoginEndpointCustomiser())
			.addOpenApiCustomiser(loginStatusCheckingEndpointCustomiser())
			.build();
	}

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("API 문서")
				.version("0.0.1")
				.description("API 문서입니다."));
	}

	@Bean
	public OpenApiCustomiser springSecurityLoginEndpointCustomiser() {
		FilterChainProxy filterChainProxy = applicationContext.getBean(
			AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
		return openAPI -> {
			for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
				Optional<AjaxEmailPasswordAuthenticationFilter> optionalFilter =
					filterChain.getFilters().stream()
						.filter(AjaxEmailPasswordAuthenticationFilter.class::isInstance)
						.map(AjaxEmailPasswordAuthenticationFilter.class::cast)
						.findAny();
				if (optionalFilter.isPresent()) {
					AjaxEmailPasswordAuthenticationFilter ajaxEmailPasswordAuthenticationFilter = optionalFilter.get();
					Operation operation = new Operation();
					Schema<?> schema = new ObjectSchema()
						.addProperties(ajaxEmailPasswordAuthenticationFilter.getEmailParameter(), new StringSchema())
						.addProperties(ajaxEmailPasswordAuthenticationFilter.getPasswordParameter(),
							new StringSchema());
					RequestBody requestBody = new RequestBody().content(
						new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
							new MediaType().schema(schema)));
					operation.requestBody(requestBody);
					ApiResponses apiResponses = new ApiResponses();
					apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
						new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
					apiResponses.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()),
						new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase()));
					operation.responses(apiResponses);
					operation.addTagsItem("email-login-endpoint");
					PathItem pathItem = new PathItem().post(operation);
					openAPI.getPaths().addPathItem(LOGIN_WITH_EMAIL, pathItem);
				}
			}
		};
	}

	@Bean
	public OpenApiCustomiser loginStatusCheckingEndpointCustomiser() {
		FilterChainProxy filterChainProxy = applicationContext.getBean(
			AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
		return openAPI -> {
			for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
				Optional<LoginStatusCheckingFilter> optionalFilter =
					filterChain.getFilters().stream()
						.filter(LoginStatusCheckingFilter.class::isInstance)
						.map(LoginStatusCheckingFilter.class::cast)
						.findAny();
				if (optionalFilter.isPresent()) {
					Operation operation = new Operation();
					operation.addTagsItem("login-status-checking-endpoint");
					operation.setSummary("유저의 로그인 상태 확인");
					operation.setDescription(
						"로그인 중이었던 유저가 로그인 중이었음을 확인합니다."
							+ " 브라우저 새로고침 시 리덕스 스토어가 초기화되면서 인증 정보가 초기화되는 이슈를 해결하기 위한 API입니다.");
					ApiResponses apiResponses = new ApiResponses();
					apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
						new ApiResponse().description("로그인 중이었습니다."));
					apiResponses.addApiResponse(String.valueOf(HttpStatus.NO_CONTENT.value()),
						new ApiResponse().description("로그인하지 않은 유저이거나 세션이 만료되었습니다."));
					operation.responses(apiResponses);
					PathItem pathItem = new PathItem().get(operation);
					openAPI.getPaths().addPathItem(LOGIN_STATUS, pathItem);
				}
			}
		};
	}
}
