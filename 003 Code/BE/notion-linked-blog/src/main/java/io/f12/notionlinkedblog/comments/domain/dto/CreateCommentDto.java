package io.f12.notionlinkedblog.comments.domain.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreateCommentDto {
	@NotEmpty
	private String comment;
	@NotNull
	private Integer depth;
	private Long parentCommentId;
}
