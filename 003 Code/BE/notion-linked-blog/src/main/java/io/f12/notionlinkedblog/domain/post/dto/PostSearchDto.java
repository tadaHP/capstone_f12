package io.f12.notionlinkedblog.domain.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSearchDto {
	private Long postId;
	private String username;
	private String title;
	private String content;
	private Long viewCount;
	private Integer likes;
	private String requestThumbnailLink;

	@Builder
	public PostSearchDto(Long postId, String username, String title, String content, Long viewCount, Integer likes,
		String requestThumbnailLink) {
		this.postId = postId;
		this.username = username;
		this.title = title;
		this.content = content;
		this.viewCount = viewCount;
		this.likes = likes;
		this.requestThumbnailLink = requestThumbnailLink;
	}
}
