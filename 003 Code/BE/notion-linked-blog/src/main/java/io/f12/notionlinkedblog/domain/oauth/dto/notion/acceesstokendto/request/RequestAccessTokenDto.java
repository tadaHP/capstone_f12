package io.f12.notionlinkedblog.domain.oauth.dto.notion.acceesstokendto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestAccessTokenDto {
	private String code;
	private String grantType;
	private String redirectUri;
}
