package io.f12.notionlinkedblog.config;

import static io.f12.notionlinkedblog.api.common.Endpoint.Api.*;
import static org.springframework.http.MediaType.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.util.ReflectionUtils;

import io.f12.notionlinkedblog.domain.common.CommonErrorResponse;
import io.f12.notionlinkedblog.security.common.dto.AuthenticationFailureDto;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;
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
			.addOperationCustomizer(springSecurityOperationCustomizer())
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
	public OperationCustomizer springSecurityOperationCustomizer() {
		return (operation, handlerMethod) -> {
			Method method = handlerMethod.getMethod();
			List<Class<?>> params = Arrays.asList(method.getParameterTypes());

			addUnAuthorizedApiResponse(operation, params);
			addNotFoundApiResponse(operation);

			return operation;
		};
	}

	private void addNotFoundApiResponse(Operation operation) {
		String statusCode = "404";
		String description = "존재하지 않는 자원 접근";

		setOperation(operation, statusCode, description, APPLICATION_JSON_VALUE, CommonErrorResponse.class);
	}

	private void addUnAuthorizedApiResponse(Operation operation, List<Class<?>> params) {
		if (params.contains(LoginUser.class)) {
			String statusCode = "401";
			String description = "인증 실패";

			setOperation(operation, statusCode, description, APPLICATION_JSON_VALUE, AuthenticationFailureDto.class);
		}
	}

	private void setOperation(
		Operation operation, String statusCode, String description, String mediaTypeName, Class<?> clazz) {
		Schema<?> schema;
		MediaType mediaType = new MediaType();

		Content content = new Content();
		content.addMediaType(mediaTypeName, mediaType);
		try {
			schema = createSchemaWithDefaultType(clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		mediaType.setSchema(schema);

		ApiResponse notFoundApiResponse = new ApiResponse()
			.description(description)
			.content(content);
		operation.getResponses().addApiResponse(statusCode, notFoundApiResponse);
	}

	/**
	 * 파라미터 타입으로 인스턴스를 생성하여 반환합니다.
	 * 만약 클래스에 public 기본 생성자가 없는 경우 싱글톤 클래스로 간주합니다.
	 * 이 경우 클래스에 정의된 instance 필드를 참조합니다. 따라서 싱글톤 클래스를 사용할 경우 instance 필드에 인스턴스가 존재해야 합니다.
	 * @author JIYONG JUNG
	 * @param clazz Class<?>
	 * @return Object
	 */
	private Object getInstance(Class<?> clazz) throws
		InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchFieldException {
		Object instance = null;
		try {
			Constructor<?> constructor = clazz.getConstructor();
			instance = constructor.newInstance();
		} catch (NoSuchMethodException e) {
			Field field = clazz.getDeclaredField("instance");
			field.setAccessible(true);
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
				instance = field.get(null);
			}
		}
		return instance;
	}

	/**
	 * final 및 static이 적용되지 않고 getter가 존재하는 string 타입의 필드에 대하여 "string" 문자열을 주입한 Schema를 반환합니다.
	 * primitive 타입은 기본값이 들어갑니다.
	 * String이 아닌 객체 타입에 대해서는 동작하지 않습니다.
	 * @author JIYONG JUNG
	 * @param clazz Class<?>
	 * @return Schema<?>
	 */
	private Schema<?> createSchemaWithDefaultType(Class<?> clazz) throws
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		ClassNotFoundException, NoSuchFieldException {
		Schema<?> schema = new Schema<>();
		Class.forName(clazz.getName());

		Object instance = getInstance(clazz);

		Field[] declaredFields = clazz.getDeclaredFields();

		for (Field field : declaredFields) {
			field.setAccessible(true);
			int modifiers = field.getModifiers();
			if (!Modifier.isStatic(modifiers) && field.getType().isAssignableFrom(String.class)) {
				ArrayList<Method> declaredMethods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
				String fieldName = field.getName();
				String getter = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				for (Method method : declaredMethods) {
					method.setAccessible(true);
					String methodName = method.getName();
					if (methodName.equals(getter)) {
						ReflectionUtils.setField(field, instance, field.getType().getSimpleName().toLowerCase());
						declaredMethods.remove(method);
						break;
					}
				}
			}
		}

		schema.setDefault(instance);
		return schema;
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
						new Content().addMediaType(APPLICATION_JSON_VALUE,
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
