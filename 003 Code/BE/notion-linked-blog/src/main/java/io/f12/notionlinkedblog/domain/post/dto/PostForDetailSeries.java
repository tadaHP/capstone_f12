package io.f12.notionlinkedblog.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostForDetailSeries {
	private String postTitle;
	private String postInfo;
	private String thumbnailRequestUrl;
}
