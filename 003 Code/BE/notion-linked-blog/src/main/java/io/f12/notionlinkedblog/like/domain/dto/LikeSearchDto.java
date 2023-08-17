package io.f12.notionlinkedblog.like.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeSearchDto {
	private Long userID;
	private Long postId;
	private Long likeId;

	@Builder
	public LikeSearchDto(Long userID, Long postId, Long likeId) {
		this.userID = userID;
		this.postId = postId;
		this.likeId = likeId;
	}
}
