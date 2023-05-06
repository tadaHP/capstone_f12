package io.f12.notionlinkedblog.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PostEditDto {
	private String title;
	private String content;
	private String thumbnail;

}
