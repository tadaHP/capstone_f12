package io.f12.notionlinkedblog.domain.series.dto;

import java.util.List;

import io.f12.notionlinkedblog.domain.post.dto.SimplePostDto;
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
