package io.f12.notionlinkedblog.post.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTrendSchedule {
	private final PostRepository postRepository;

	/**
	 * 현재 비 개인화 알고리즘을 통한 추천
	 * 추후 개인화 알고리즘을 통한 개선 여지 있음(파이썬 필요)
	 */
	@Scheduled(fixedDelay = 3600000) // 1시간 (임시 설정)
	public void updateTrendIndex() {
		List<PostEntity> posts = postRepository.findByPostIdForTrend();
		for (PostEntity post : posts) {
			int likes = post.getLikes().size();
			Long timeNow = localDateTimeToLong(LocalDateTime.now());
			Long createdTime = localDateTimeToLong(post.getCreatedAt());
			Long viewCount = post.getViewCount();

			Double redditTrend = calculateRedditFormula(likes);
			Double hackerTrend = calculateHackerNews(viewCount, timeNow, createdTime);
			post.setPopularity((hackerTrend + redditTrend) / 2);
		}
	}

	/**
	 * 좋아요 개수를 통한 트랜드 계산, reddit 사용 지수
	 */
	private Double calculateRedditFormula(int likes) {
		return Math.log10(likes) + (Math.signum(likes) / 45000000);
	}

	/**
	 *  pageView 와 포스트 게시 시간을 이용한 트랜드 계산
	 *  최초 생성시에는 viewCount 가 0이고 이에 따라 음수로 값이 설정 될 수 있다.
	 */
	private Double calculateHackerNews(Long pageViews, Long timeNow, Long createdTime) {
		final double gravity = 1.8;
		return ((pageViews - 1) / Math.pow((timeNow - createdTime + 2), gravity));
	}

	private Long localDateTimeToLong(LocalDateTime time) {
		return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}
