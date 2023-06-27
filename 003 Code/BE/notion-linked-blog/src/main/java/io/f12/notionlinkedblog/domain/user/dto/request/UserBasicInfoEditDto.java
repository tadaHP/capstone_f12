package io.f12.notionlinkedblog.domain.user.dto.request;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoEditDto {
	@NotEmpty
	private String username;
	private String introduction;
}
