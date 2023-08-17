package io.f12.notionlinkedblog.series.api.port;

import java.util.List;

import io.f12.notionlinkedblog.series.api.response.SeriesCreateResponseDto;
import io.f12.notionlinkedblog.series.api.response.SeriesDetailSearchDto;
import io.f12.notionlinkedblog.series.api.response.SeriesSimpleSearchDto;
import io.f12.notionlinkedblog.series.api.response.UserSeriesDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesCreateDto;
import io.f12.notionlinkedblog.series.domain.dto.request.SeriesRemoveDto;

public interface SeriesService {

	public SeriesCreateResponseDto createSeries(SeriesCreateDto createDto);

	public void removeSeries(SeriesRemoveDto removeDto);

	public List<UserSeriesDto> getSeriesByUserId(Long userId);

	public SeriesSimpleSearchDto getSimpleSeriesInfo(Long seriesId);

	public SeriesDetailSearchDto getDetailSeriesInfoOrderByDesc(Long seriesId, Integer page);

	public SeriesDetailSearchDto getDetailSeriesInfoOrderByAsc(Long seriesId, Integer page);

	public void addPostTo(Long seriesId, Long postId);

	public void removePostFrom(Long seriesId, Long postId);

	public void editTitle(Long id, String title);
}