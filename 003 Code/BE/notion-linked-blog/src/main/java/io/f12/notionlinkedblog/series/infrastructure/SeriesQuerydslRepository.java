package io.f12.notionlinkedblog.series.infrastructure;

import static io.f12.notionlinkedblog.series.infrastructure.QSeriesEntity.*;
import static io.f12.notionlinkedblog.user.infrastructure.QUserEntity.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import io.f12.notionlinkedblog.user.service.port.UserSeriesQuerydslRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeriesQuerydslRepository implements UserSeriesQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Long> findIdByUserId(Long userId) {
		return queryFactory.select(seriesEntity.id)
			.from(seriesEntity)
			.leftJoin(seriesEntity.user, userEntity)
			.where(seriesEntity.user.id.eq(userId))
			.fetch();
	}

	@Override
	public List<SeriesEntity> findByUserIds(List<Long> ids) {
		return queryFactory.selectFrom(seriesEntity)
			.where(seriesEntity.id.in(ids))
			.leftJoin(seriesEntity.user, userEntity)
			.fetch();
	}
}
