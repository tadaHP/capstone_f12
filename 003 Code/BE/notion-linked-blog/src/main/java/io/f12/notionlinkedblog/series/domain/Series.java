package io.f12.notionlinkedblog.series.domain;

import java.util.List;
import java.util.stream.Collectors;

import io.f12.notionlinkedblog.post.domain.Post;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class Series {
	private Long id;
	private User user;
	private List<Post> post;
	private String title;

	public SeriesEntity toEntity() {
		return SeriesEntity.builder()
			.id(this.id)
			.user(this.user.toEntity())
			.post(this.post.stream().map(Post::toEntity).collect(Collectors.toList()))
			.title(this.title)
			.build();
	}
}
