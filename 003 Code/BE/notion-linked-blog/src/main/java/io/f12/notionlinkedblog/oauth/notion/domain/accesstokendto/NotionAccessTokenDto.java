package io.f12.notionlinkedblog.oauth.notion.domain.accesstokendto;

import com.google.gson.annotations.SerializedName;

import io.f12.notionlinkedblog.oauth.notion.domain.accesstokendto.accesstokeninfo.NotionOwnerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionAccessTokenDto {
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("token_type")
	private String tokenType;
	@SerializedName("bot_id")
	private String botId;
	@SerializedName("workspace_name")
	private String workspaceName;
	@SerializedName("workspace_icon")
	private String workspaceIcon;
	private NotionOwnerDto owner;
	@SerializedName("duplicated_template_id")
	private String duplicatedTemplateId;
}
