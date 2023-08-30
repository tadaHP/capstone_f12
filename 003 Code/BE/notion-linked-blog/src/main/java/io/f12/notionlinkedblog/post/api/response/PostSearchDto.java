package io.f12.notionlinkedblog.post.api.response;

import java.time.LocalDateTime;
import java.util.List;

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
	private List<String> hashtags;
}
