package io.f12.notionlinkedblog.user.api.response;

import java.util.List;

import io.f12.notionlinkedblog.user.domain.dto.UserSeriesInfoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSeriesDto {
	private Integer seriesSize;
	private List<UserSeriesInfoDto> data;
}
