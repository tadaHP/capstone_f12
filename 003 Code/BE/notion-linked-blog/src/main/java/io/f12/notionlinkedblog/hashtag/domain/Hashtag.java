package io.f12.notionlinkedblog.hashtag.domain;

import java.util.List;
import java.util.stream.Collectors;

import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;
import io.f12.notionlinkedblog.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class Hashtag {
	private Long id;
	private String name;
	private List<Post> post;

	public HashtagEntity toEntity() {
		return HashtagEntity.builder()
			.id(this.id)
			.name(this.name)
			.post(this.post.stream().map(Post::toEntity).collect(Collectors.toList()))
			.build();
	}

	public void addPost(Post post) {
		this.post.add(post);
	}
}
