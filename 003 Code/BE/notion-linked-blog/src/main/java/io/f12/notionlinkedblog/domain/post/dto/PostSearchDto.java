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
	private String thumbnail;
	private Long viewCount;
	private Integer likes;

	@Builder
	public PostSearchDto(Long postId, String username, String title, String content, String thumbnail, Long viewCount,
		Integer likes) {
		this.postId = postId;
		this.username = username;
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.viewCount = viewCount;
		this.likes = likes;
	}
}
