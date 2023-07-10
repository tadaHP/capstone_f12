package io.f12.notionlinkedblog.domain.comments;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import io.f12.notionlinkedblog.domain.PostTimeEntity;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "comments")
@SequenceGenerator(
	name = "comments_seq_generator",
	sequenceName = "comments_seq",
	allocationSize = 1
)
public class Comments extends PostTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_seq_generator")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	@NotNull
	private Post post;
	private String content;
	@NotNull
	private Integer depth; // 0 = parents, 1 = children
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comments parent;
	@OneToMany(mappedBy = "parent", orphanRemoval = true)
	private List<Comments> children = new ArrayList<>();

	public void editComments(String content) {
		if (StringUtils.hasText(content)) {
			this.content = content;
		}
	}

	public void addChildren(Comments children) {
		this.children.add(children);
	}
}
