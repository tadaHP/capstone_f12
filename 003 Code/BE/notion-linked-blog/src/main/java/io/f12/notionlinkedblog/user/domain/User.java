package io.f12.notionlinkedblog.user.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.f12.notionlinkedblog.comments.domain.Comments;
import io.f12.notionlinkedblog.like.domain.Like;
import io.f12.notionlinkedblog.notion.domain.SyncedPages;
import io.f12.notionlinkedblog.oauth.notion.domain.NotionOauth;
import io.f12.notionlinkedblog.post.domain.Post;
import io.f12.notionlinkedblog.series.domain.Series;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class User {
	private Long id;
	private List<Post> posts;
	private List<Comments> comments;
	private List<Like> likes;
	private List<Series> series;
	private NotionOauth notionOauth;
	private List<SyncedPages> syncedPages;
	private String username;
	private String email;
	private String password;
	private String profile;
	private String introduction;
	private String blogTitle;
	private String githubLink;
	private String instagramLink;
	private LocalDateTime createdAt;

	public UserEntity toEntity() {
		return UserEntity.builder()
			.id(this.id)
			.posts(this.posts.stream().map(Post::toEntity).collect(Collectors.toList()))
			.comments(this.comments.stream().map(Comments::toEntity).collect(Collectors.toList()))
			.likes(this.likes.stream().map(Like::toEntity).collect(Collectors.toList()))
			.series(this.series.stream().map(Series::toEntity).collect(Collectors.toList()))
			.notionOauth(this.notionOauth.toEntity())
			.syncedPages(this.syncedPages.stream().map(SyncedPages::toEntity).collect(Collectors.toList()))
			.username(this.username)
			.email(this.email)
			.password(this.password)
			.profile(this.profile)
			.introduction(this.introduction)
			.blogTitle(this.blogTitle)
			.githubLink(this.githubLink)
			.instagramLink(this.instagramLink)
			.build();
	}
}
