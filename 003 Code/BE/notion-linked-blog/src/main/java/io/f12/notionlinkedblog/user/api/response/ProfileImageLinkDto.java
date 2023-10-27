package io.f12.notionlinkedblog.user.api.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileImageLinkDto {
	private String imageUrl;
}
