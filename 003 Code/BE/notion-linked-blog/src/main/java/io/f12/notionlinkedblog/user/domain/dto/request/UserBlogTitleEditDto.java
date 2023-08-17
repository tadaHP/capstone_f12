package io.f12.notionlinkedblog.user.domain.dto.request;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlogTitleEditDto {
	@NotEmpty
	private String blogTitle;
}
