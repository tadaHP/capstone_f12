package io.f12.notionlinkedblog.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("F12")
			.pathsToMatch("/api/**")
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
}
