package io.f12.notionlinkedblog.comments.domain.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EditCommentDto {
	@NotEmpty
	private String comment;
}
