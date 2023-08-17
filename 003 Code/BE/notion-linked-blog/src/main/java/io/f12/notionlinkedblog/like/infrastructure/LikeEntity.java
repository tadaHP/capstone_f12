package io.f12.notionlinkedblog.like.infrastructure;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.like.domain.Like;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
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
@Table(name = "likes")
@SequenceGenerator(
	name = "like_seq_generator",
	sequenceName = "like_seq",
	allocationSize = 1
)
public class LikeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_seq_generator")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	@NotNull
	private PostEntity post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private UserEntity user;

	public Like toModel() {
		return Like.builder()
			.id(this.id)
			.post(this.post.toModel())
			.user(this.user.toModel())
			.build();
	}
}
