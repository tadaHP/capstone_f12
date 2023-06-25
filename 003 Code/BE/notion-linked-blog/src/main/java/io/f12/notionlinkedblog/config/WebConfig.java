package io.f12.notionlinkedblog.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.f12.notionlinkedblog.web.argumentresolver.email.EmailArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new EmailArgumentResolver());
	}

	@Bean
	public FilterRegistrationBean<Utf8Filter> utf8FilterRegistrationBean() {
		FilterRegistrationBean<Utf8Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new Utf8Filter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
