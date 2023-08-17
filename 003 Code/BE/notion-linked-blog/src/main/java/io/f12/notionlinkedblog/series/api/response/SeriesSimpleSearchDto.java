package io.f12.notionlinkedblog.series.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesSimpleSearchDto {
	private Long seriesId;
	private String seriesName;
	private List<SimplePostDto> posts;
}
