package io.f12.notionlinkedblog.domain.notion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.f12.notionlinkedblog.domain.post.Post;
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
@Table(name = "notion")
public class Notion {
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	@JoinColumn(name = "post_id")
	@NotNull
	private Post post;

	@NotNull
	@Column(unique = true)
	private String notionId;

}
