package io.f12.notionlinkedblog.common.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(EmailConfig.SpringMailProperties.class)
@Configuration
public class EmailConfig {

	private final SpringMailProperties mailProperties;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private boolean auth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private boolean starttls;

	@Value("${spring.mail.transport.protocol}")
	private String protocol;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", protocol);
		properties.put("mail.smtp.auth", auth);
		properties.put("mail.smtp.starttls.enable", starttls);
		properties.put("mail.smtp.debug", mailProperties.isDebug());

		mailSender.setHost(mailProperties.getHost());
		mailSender.setUsername(mailProperties.getUsername());
		mailSender.setPassword(mailProperties.getPassword());
		mailSender.setPort(mailProperties.getPort());
		mailSender.setJavaMailProperties(properties);
		mailSender.setDefaultEncoding(mailProperties.getDefaultEncoding());
		return mailSender;
	}

	@Getter
	@RequiredArgsConstructor
	@ConstructorBinding
	@ConfigurationProperties("spring.mail")
	static class SpringMailProperties {
		private final String host;
		private final int port;
		private final String username;
		private final String password;
		private final String defaultEncoding;
		private final boolean debug;
	}
}
