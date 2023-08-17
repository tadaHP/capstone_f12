package io.f12.notionlinkedblog.post.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.f12.notionlinkedblog.comments.domain.Comments;
import io.f12.notionlinkedblog.like.domain.Like;
import io.f12.notionlinkedblog.notion.domain.SyncedPages;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.series.domain.Series;
import io.f12.notionlinkedblog.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class Post {
	private Long id;
	private User user;
	private List<Comments> comments;
	private List<Like> likes;
	private Series series;
	private SyncedPages syncedPages;
	private String title;
	private String content;
	private String thumbnailName;
	private String storedThumbnailPath;
	private Long viewCount;
	private Double popularity;
	private String description;
	private Boolean isPublic;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public PostEntity toEntity() {
		return PostEntity.builder()
			.id(this.id)
			.user(this.user.toEntity())
			.comments(this.comments.stream().map(Comments::toEntity).collect(Collectors.toList()))
			.likes(this.likes.stream().map(Like::toEntity).collect(Collectors.toList()))
			.series(this.series.toEntity())
			.syncedPages(this.syncedPages.toEntity())
			.title(this.title)
			.content(this.content)
			.thumbnailName(this.thumbnailName)
			.storedThumbnailPath(this.storedThumbnailPath)
			.viewCount(this.viewCount)
			.popularity(this.popularity)
			.description(this.description)
			.isPublic(this.isPublic)
			.build();
	}
}
