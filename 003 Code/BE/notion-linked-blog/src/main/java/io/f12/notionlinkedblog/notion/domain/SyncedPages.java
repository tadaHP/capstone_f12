package io.f12.notionlinkedblog.notion.domain;

import io.f12.notionlinkedblog.notion.infrastructure.single.SyncedPagesEntity;
import io.f12.notionlinkedblog.post.domain.Post;
import io.f12.notionlinkedblog.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SyncedPages {
	private Long id;
	private String pageId;
	private User user;
	private Post post;

	public SyncedPagesEntity toEntity() {
		return SyncedPagesEntity.builder()
			.id(this.id)
			.pageId(this.pageId)
			.user(this.user.toEntity())
			.post(this.post.toEntity())
			.build();
	}
}
