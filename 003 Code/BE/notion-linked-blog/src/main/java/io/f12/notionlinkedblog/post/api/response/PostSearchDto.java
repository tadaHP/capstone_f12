package io.f12.notionlinkedblog.post.api.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchDto {
	private Long postId;
	private String title;
	private String content;
	private Long viewCount;
	private Integer likes;
	private String requestThumbnailLink;
	private String description;
	private LocalDateTime createdAt;
	private Integer countOfComments;
	private String author;
	private Boolean isLiked;
	private String avatar;

	public PostSearchDto(Long postId, String title, String content, Long viewCount, Integer likes,
		String requestThumbnailLink, String description, LocalDateTime createdAt, Integer countOfComments,
		String author) {
		this.postId = postId;
		this.title = title;
		this.content = content;
		this.viewCount = viewCount;
		this.likes = likes;
		this.requestThumbnailLink = requestThumbnailLink;
		this.description = description;
		this.createdAt = createdAt;
		this.countOfComments = countOfComments;
		this.author = author;
	}
}
