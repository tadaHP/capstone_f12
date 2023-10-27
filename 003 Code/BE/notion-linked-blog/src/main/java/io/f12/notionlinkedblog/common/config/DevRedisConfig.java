package io.f12.notionlinkedblog.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Profile("dev")
public class DevRedisConfig {
	@Value("${spring.redis.host}")
	private String embeddedRedisHost;

	@Value("${spring.redis.port}")
	private int embeddedRedisPort;

	@Bean
	public RedisTemplate<String, Object> productionRedisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setConnectionFactory(embeddedRedisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	StringRedisTemplate productionStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	// Embedded
	@Bean
	public RedisConnectionFactory embeddedRedisConnectionFactory() {
		return new LettuceConnectionFactory(embeddedRedisHost, embeddedRedisPort);
	}
}
