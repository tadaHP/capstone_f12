package io.f12.notionlinkedblog.post.api.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostThumbnailDto {
	private String url;
}
