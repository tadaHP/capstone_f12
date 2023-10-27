package io.f12.notionlinkedblog.common.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;

import com.querydsl.jpa.impl.JPAQueryFactory;

import io.f12.notionlinkedblog.post.infrastructure.PostQuerydslRepositoryImpl;
import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;

public class TestQuerydslConfiguration {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public QuerydslPostRepository querydslPostRepository() {
		return new PostQuerydslRepositoryImpl(new JPAQueryFactory(entityManager));
	}
}
