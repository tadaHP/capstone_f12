package io.f12.notionlinkedblog.user.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSeriesInfoDto {
	private Long seriesId;
	private String title;
	private String author;
	private Long authorId;
}
