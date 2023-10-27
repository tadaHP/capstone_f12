package io.f12.notionlinkedblog.series.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostForDetailSeries {
	private Long postId;
	private String postTitle;
	private String postInfo;
	private String thumbnailRequestUrl;
}
