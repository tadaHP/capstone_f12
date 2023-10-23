package io.f12.notionlinkedblog.post.infrastructure;

import static io.f12.notionlinkedblog.comments.infrastructure.QCommentsEntity.*;
import static io.f12.notionlinkedblog.hashtag.infrastructure.QHashtagEntity.*;
import static io.f12.notionlinkedblog.like.infrastructure.QLikeEntity.*;
import static io.f12.notionlinkedblog.post.infrastructure.QPostEntity.*;
import static io.f12.notionlinkedblog.series.infrastructure.QSeriesEntity.*;
import static io.f12.notionlinkedblog.user.infrastructure.QUserEntity.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;
import io.f12.notionlinkedblog.user.service.port.UserPostQuerydslRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQuerydslRepositoryImpl implements QuerydslPostRepository, UserPostQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Long> findPostIdsByTitle(String title, Pageable pageable) {
		return queryFactory.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.title.contains(title).and(postEntity.isPublic.isTrue()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findPostIdsByContent(String content, Pageable pageable) {
		return queryFactory.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.content.contains(content).and(postEntity.isPublic.isTrue()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findLatestPostIdsByCreatedAtDesc(Pageable pageable) {
		return queryFactory.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.isPublic.isTrue())
			.orderBy(postEntity.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findPopularityPostIdsByViewCountAtDesc(Pageable pageable) {
		return queryFactory.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.isPublic.isTrue())
			.orderBy(postEntity.popularity.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findPostIdsByUserId(Long userId) {
		return queryFactory.select(postEntity.id)
			.from(postEntity)
			.join(postEntity.user, userEntity)
			.on(userEntity.id.eq(userId))
			.fetch();
	}

	@Override
	public List<PostEntity> findByPostIdsJoinWithUserAndLikeOrderByLatest(List<Long> ids) {
		return queryFactory.selectFrom(postEntity)
			.leftJoin(postEntity.user, userEntity)
			.leftJoin(postEntity.likes, likeEntity)
			.where(postEntity.id.in(ids))
			.orderBy(postEntity.createdAt.asc())
			.distinct()
			.fetch();
	}

	@Override
	public List<PostEntity> findByPostIdsJoinWithUserAndLikeOrderByTrend(List<Long> ids) {
		return queryFactory.selectFrom(postEntity)
			.leftJoin(postEntity.user, userEntity)
			.leftJoin(postEntity.likes, likeEntity)
			.where(postEntity.id.in(ids))
			.orderBy(postEntity.popularity.desc())
			.distinct()
			.fetch();
	}

	@Override
	public List<Long> findIdsBySeriesIdDesc(Long seriesId, Pageable pageable) {
		return queryFactory
			.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.series.id.eq(seriesId).and(postEntity.isPublic.isTrue()))
			.orderBy(postEntity.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Long> findIdsBySeriesIdAsc(Long seriesId, Pageable pageable) {
		return queryFactory
			.select(postEntity.id)
			.from(postEntity)
			.where(postEntity.series.id.eq(seriesId).and(postEntity.isPublic.isTrue()))
			.orderBy(postEntity.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<PostEntity> findByIdsJoinWithSeries(List<Long> ids) {
		return queryFactory.selectFrom(postEntity)
			.leftJoin(postEntity.series, seriesEntity)
			.where(postEntity.id.in(ids))
			.distinct()
			.fetch();

	}

	@Override
	public List<PostEntity> findPostsByIdsForUserPost(List<Long> ids) {
		return queryFactory.selectFrom(postEntity)
			.leftJoin(postEntity.hashtag, hashtagEntity)
			.leftJoin(postEntity.user, userEntity)
			.leftJoin(postEntity.likes, likeEntity)
			.leftJoin(postEntity.comments, commentsEntity)
			.where(postEntity.id.in(ids))
			.distinct()
			.fetch();
	}

}
