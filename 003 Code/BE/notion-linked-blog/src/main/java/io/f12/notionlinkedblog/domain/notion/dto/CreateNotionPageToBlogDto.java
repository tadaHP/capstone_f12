package io.f12.notionlinkedblog.domain.notion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateNotionPageToBlogDto {
	private Long userId;
	private String path;
}
