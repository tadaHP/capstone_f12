package io.f12.notionlinkedblog.series.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.SeriesExceptionMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.common.domain.EntityConverter;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;
import io.f12.notionlinkedblog.series.api.port.SeriesService;
import io.f12.notionlinkedblog.series.api.response.SeriesCreateResponseDto;
import io.f12.notionlinkedblog.series.api.response.SeriesDetailSearchDto;
import io.f12.notionlinkedblog.series.api.response.SeriesSimpleSearchDto;
import io.f12.notionlinkedblog.series.api.response.SimplePostDto;
import io.f12.notionlinkedblog.series.api.response.UserSeriesDto;
import io.f12.notionlinkedblog.series.api.response.UserSeriesResponseDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesCreateDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesRemoveDto;
import io.f12.notionlinkedblog.series.exception.SeriesNotExistException;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.series.service.port.SeriesRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesServiceImpl implements SeriesService {

	private final SeriesRepository seriesRepository;
	private final PostRepository postRepository;
	private final QuerydslPostRepository querydslPostRepository;
	private final UserRepository userRepository;
	private final EntityConverter entityConverter;

	private static final int pagingSize = 10;

	@Override
	public SeriesCreateResponseDto createSeries(SeriesCreateDto createDto) {
		UserEntity user = userRepository.findById(createDto.getUserId())
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		SeriesEntity series = SeriesEntity.builder()
			.user(user)
			.title(createDto.getSeriesTitle())
			.post(new ArrayList<>())
			.build();

		SeriesEntity savedSeries = seriesRepository.save(series);

		return SeriesCreateResponseDto.builder()
			.seriesId(savedSeries.getId())
			.build();
	}

	@Override
	public void removeSeries(SeriesRemoveDto removeDto) {
		SeriesEntity series = seriesRepository.findSeriesById(removeDto.getSeriesId())
			.orElseThrow(() -> new IllegalArgumentException(SERIES_NOT_EXIST));

		List<PostEntity> post = series.getPost();
		for (int i = post.size() - 1; i >= 0; i--) {
			PostEntity p = post.get(i);
			series.removePost(p);
		}
		seriesRepository.delete(series);
	}

	@Override
	public UserSeriesResponseDto getSeriesByUserId(Long userId) {
		List<SeriesEntity> series = userRepository.findSeriesByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST))
			.getSeries();

		List<UserSeriesDto> convertDto = entityConverter.convertSeriesToUserSeriesDto(series);

		return UserSeriesResponseDto.builder()
			.authorId(series.get(0).getUser().getId())
			.author(series.get(0).getUser().getUsername())
			.data(convertDto)
			.build();
	}

	@Override
	public SeriesSimpleSearchDto getSimpleSeriesInfo(Long seriesId) {
		SeriesEntity series = seriesRepository.findSeriesById(seriesId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 seriesId 입니다."));

		List<PostEntity> post = series.getPost();
		List<SimplePostDto> simplePosts = postToSimplePost(post);

		return SeriesSimpleSearchDto.builder()
			.author(series.getUser().getUsername())
			.authorId(series.getUser().getId())
			.seriesName(series.getTitle())
			.seriesId(series.getId())
			.posts(simplePosts)
			.build();
	}

	@Override
	public SeriesDetailSearchDto getDetailSeriesInfoOrderByDesc(Long seriesId, Integer page) throws
		SeriesNotExistException {
		PageRequest pageRequest = PageRequest.of(page, pagingSize);

		SeriesEntity findSeries = seriesRepository.findSeriesById(seriesId).orElseThrow(SeriesNotExistException::new);
		List<Long> postIds = querydslPostRepository.findIdsBySeriesIdDesc(seriesId, pageRequest);
		List<PostEntity> posts = querydslPostRepository.findByIdsJoinWithSeries(postIds);

		return entityConverter.convertPostToSeriesDetailSearchDto(posts, page, findSeries);
	}

	@Override
	public SeriesDetailSearchDto getDetailSeriesInfoOrderByAsc(Long seriesId, Integer page)
		throws SeriesNotExistException {
		PageRequest pageRequest = PageRequest.of(page, pagingSize);

		SeriesEntity findSeries = seriesRepository.findSeriesById(seriesId).orElseThrow(SeriesNotExistException::new);
		List<Long> postIds = querydslPostRepository.findIdsBySeriesIdAsc(seriesId, pageRequest);
		if (postIds.isEmpty()) {

		}
		List<PostEntity> posts = querydslPostRepository.findByIdsJoinWithSeries(postIds);

		return entityConverter.convertPostToSeriesDetailSearchDto(posts, page, findSeries);
	}

	@Override
	@Transactional
	public void addPostTo(Long seriesId, Long postId) {
		SeriesEntity series = seriesRepository.findSeriesById(seriesId)
			.orElseThrow(() -> new IllegalArgumentException(SERIES_NOT_EXIST));
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		series.addPost(post);
	}

	@Override
	public void removePostFrom(Long seriesId, Long postId) {
		SeriesEntity series = seriesRepository.findSeriesById(seriesId)
			.orElseThrow(() -> new IllegalArgumentException(SERIES_NOT_EXIST));
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		series.removePost(post);
	}

	@Override
	public void editTitle(Long id, String title) {
		SeriesEntity series = seriesRepository.findSeriesById(id)
			.orElseThrow(() -> new IllegalArgumentException(SERIES_NOT_EXIST));
		series.setTitle(title);
	}

	// 내부 사용 매서드
	private List<SimplePostDto> postToSimplePost(List<PostEntity> post) {
		return post.stream().map(p -> SimplePostDto.builder()
			.postId(p.getId())
			.postTitle(p.getTitle())
			.build()).collect(Collectors.toList());
	}
}
