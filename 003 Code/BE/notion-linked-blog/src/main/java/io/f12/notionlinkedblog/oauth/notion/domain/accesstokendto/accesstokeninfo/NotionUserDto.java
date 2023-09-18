package io.f12.notionlinkedblog.oauth.notion.domain.accesstokendto.accesstokeninfo;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionUserDto {
	private String object;
	private String id;
	private String name;
	@SerializedName("avatar_url")
	private String avatarUrl;
	private String type;
	private NotionPersonDto person;

}
