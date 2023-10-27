package io.f12.notionlinkedblog.series.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimplePostDto {
	private Long postId;
	private String postTitle;
}
