package io.f12.notionlinkedblog.oauth.domain.notion.acceesstokendto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionOauthAccessTokenDto {
	private String accessToken;
	private String tokenType;
	private String botId;
	private String workspaceName;
	private String workspaceIcon;
	private String workspaceId;
	private String duplicatedTemplateId;

}
