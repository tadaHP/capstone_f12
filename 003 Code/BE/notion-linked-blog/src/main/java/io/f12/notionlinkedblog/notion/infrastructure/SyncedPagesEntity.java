package io.f12.notionlinkedblog.notion.infrastructure;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "synced_pages")
public class SyncedPagesEntity {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private String pageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private UserEntity user;

	@OneToOne
	@JoinColumn(name = "post_id")
	@NotNull
	private PostEntity post;

}