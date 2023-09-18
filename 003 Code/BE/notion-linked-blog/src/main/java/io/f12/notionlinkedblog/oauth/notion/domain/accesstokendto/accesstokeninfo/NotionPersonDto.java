package io.f12.notionlinkedblog.oauth.notion.domain.accesstokendto.accesstokeninfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionPersonDto {
	private String email;
}
