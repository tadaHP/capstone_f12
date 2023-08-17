package io.f12.notionlinkedblog.series.api.response;

import java.util.List;

import io.f12.notionlinkedblog.common.domain.PagingInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SeriesDetailSearchDto {
	private Long seriesId;
	private String seriesName;
	private PagingInfo pagingInfo;
	private List<PostForDetailSeries> postsInfo;

}
