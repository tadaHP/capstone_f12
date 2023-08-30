package io.f12.notionlinkedblog.post.domain.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PostEditDto {
	@NotEmpty
	private String title;
	@NotEmpty
	private String content;
	private Long seriesId;
	private List<String> hashtags;

}
