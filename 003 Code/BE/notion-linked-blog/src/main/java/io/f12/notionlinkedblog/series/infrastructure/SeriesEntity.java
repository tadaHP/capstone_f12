package io.f12.notionlinkedblog.series.infrastructure;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "series")
@Getter
public class SeriesEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private UserEntity user;

	@OneToMany(mappedBy = "series", cascade = CascadeType.PERSIST)
	private List<PostEntity> post = new ArrayList<>();

	@NotNull
	@Setter
	private String title;

	@Builder
	public SeriesEntity(Long id, UserEntity user, List<PostEntity> post, String title) {
		this.id = id;
		this.user = user;
		this.post = post;
		this.title = title;
	}

	// have to remove
	public void addPost(PostEntity post) {
		this.post.add(post);
		post.setSeries(this);
	}

	public void removePost(PostEntity post) {
		this.post.remove(post);
		post.setSeries(null);
	}
}
