package io.f12.notionlinkedblog.domain.series;

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

import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "series")
@Getter
public class Series {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private User user;

	@OneToMany(mappedBy = "series", cascade = CascadeType.PERSIST)
	private List<Post> post = new ArrayList<>();

	@NotNull
	@Setter
	private String title;

	@Builder
	public Series(Long id, User user, List<Post> post, String title) {
		this.id = id;
		this.user = user;
		this.post = post;
		this.title = title;
	}

	public void addPost(Post post) {
		this.post.add(post);
		post.setSeries(this);
	}

	public void removePost(Post post) {
		this.post.remove(post);
		post.setSeries(null);
	}
}
