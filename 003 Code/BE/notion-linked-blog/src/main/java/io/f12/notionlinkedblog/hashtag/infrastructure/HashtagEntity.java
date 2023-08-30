package io.f12.notionlinkedblog.hashtag.infrastructure;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "hashtags")
public class HashtagEntity {
	@Id
	@GeneratedValue
	private Long id;
	@NotEmpty
	@Column(unique = true)
	private String name;

	@ManyToMany(mappedBy = "hashtag")
	private List<PostEntity> post;

	public void addPost(PostEntity post) {
		this.post.add(post);
	}

	public void setId(Long id) {
		this.id = id;
	}
}
