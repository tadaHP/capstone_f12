package io.f12.notionlinkedblog.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	@Value("${spring.redis.host}")
	private String embeddedRedisHost;

	@Value("${spring.redis.port}")
	private int embeddedRedisPort;

	@Value("${spring.data.redis.port}")
	public int productionPort;
	@Value("${spring.data.redis.host}")
	public String productionHost;
	@Value("${spring.data.redis.password}")
	public String productionPassword;

	//Prod
	@Bean
	@Profile("prod")
	public LettuceConnectionFactory productionRedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(productionHost);
		redisStandaloneConfiguration.setPort(productionPort);
		redisStandaloneConfiguration.setPassword(productionPassword);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> productionRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setConnectionFactory(connectionFactory);
		return redisTemplate;
	}

	@Bean
	StringRedisTemplate productionStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	//Embedded
	@Bean
	@Profile("dev")
	public RedisConnectionFactory embeddedRedisConnectionFactory() {
		return new LettuceConnectionFactory(embeddedRedisHost, embeddedRedisPort);
	}

	// @Bean
	// public RedisTemplate<String, Object> embeddedRedisTemplate() {
	// 	RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
	// 	redisTemplate.setKeySerializer(new StringRedisSerializer());
	// 	redisTemplate.setValueSerializer(new StringRedisSerializer());
	// 	redisTemplate.setConnectionFactory(redisConnectionFactory());
	// 	return redisTemplate;
	// }
	//
	// @Bean
	// public StringRedisTemplate embeddedStringRedisTemplate() {
	// 	StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
	//
	// 	stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
	// 	stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
	// 	stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
	//
	// 	return stringRedisTemplate;
	// }
}
