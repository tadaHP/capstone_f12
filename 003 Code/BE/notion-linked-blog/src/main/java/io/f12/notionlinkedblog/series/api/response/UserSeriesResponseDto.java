package io.f12.notionlinkedblog.series.api.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSeriesResponseDto {
	private Long authorId;
	private String author;
	private List<UserSeriesDto> data;
}
