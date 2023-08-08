package io.f12.notionlinkedblog.domain.series.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SeriesCreateResponseDto {
	Long seriesId;
}
