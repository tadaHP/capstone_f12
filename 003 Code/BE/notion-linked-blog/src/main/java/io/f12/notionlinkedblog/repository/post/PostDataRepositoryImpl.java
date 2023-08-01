package io.f12.notionlinkedblog.repository.post;

import static io.f12.notionlinkedblog.domain.likes.QLike.*;
import static io.f12.notionlinkedblog.domain.post.QPost.*;
import static io.f12.notionlinkedblog.domain.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import io.f12.notionlinkedblog.domain.post.Post;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostDataRepositoryImpl implements PostRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Long> findPostIdsByTitle(String title, Pageable pageable) {
		return queryFactory.select(post.id)
			.from(post)
			.where(post.title.contains(title).and(post.isPublic.isTrue()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findPostIdsByContent(String content, Pageable pageable) {
		return queryFactory.select(post.id)
			.from(post)
			.where(post.content.contains(content).and(post.isPublic.isTrue()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findLatestPostIdsByCreatedAtDesc(Pageable pageable) {
		return queryFactory.select(post.id)
			.from(post)
			.where(post.isPublic.isTrue())
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findPopularityPostIdsByViewCountAtDesc(Pageable pageable) {
		return queryFactory.select(post.id)
			.from(post)
			.where(post.isPublic.isTrue())
			.orderBy(post.popularity.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Post> findByIds(List<Long> ids) {
		return queryFactory.selectFrom(post)
			.orderBy(post.popularity.desc())
			.leftJoin(post.user, user)
			.fetchJoin()
			.leftJoin(post.likes, like)
			.fetchJoin()
			.where(post.id.in(ids))
			.distinct()
			.fetch();
	}

	@Override
	public Post findWithNotion(Long id) {
		return queryFactory.select(post)
			.from(post)
			.leftJoin(post.notion)
			.fetchJoin()
			.leftJoin(post.user)
			.fetchJoin()
			.leftJoin(post.likes)
			.fetchJoin()
			.leftJoin(post.comments)
			.fetchJoin()
			.where(post.id.eq(id))
			.fetchOne();
	}

}
