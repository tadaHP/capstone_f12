package io.f12.notionlinkedblog.domain.post;

import static javax.persistence.FetchType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import io.f12.notionlinkedblog.domain.PostTimeEntity;
import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.notion.Notion;
import io.f12.notionlinkedblog.domain.notion.SyncedPages;
import io.f12.notionlinkedblog.domain.series.Series;
import io.f12.notionlinkedblog.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "posts")
@SequenceGenerator(
	name = "post_seq_generator",
	sequenceName = "post_seq",
	allocationSize = 1
)
public class Post extends PostTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_generator")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private User user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comments> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Like> likes = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "series_id")
	private Series series;

	@OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
	private Notion notion;

	@OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
	private SyncedPages syncedPages;

	@NotBlank
	private String title;
	@NotBlank
	@Column(columnDefinition = "TEXT")
	private String content;
	@Column(unique = true)
	private String thumbnailName;
	private String storedThumbnailPath;
	private Long viewCount = 0L;
	private Double popularity = 0.0;
	private String description;
	@NotNull
	private Boolean isPublic = false;

	@Builder
	public Post(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, User user,
		List<Comments> comments, List<Like> likes, Series series, String title, String content, String thumbnailName,
		String storedThumbnailPath, Long viewCount, Double popularity, String description, Boolean isPublic) {
		super(createdAt, updatedAt);
		this.id = id;
		this.user = user;
		this.comments = comments;
		this.likes = likes;
		this.series = series;
		this.title = title;
		this.content = content;
		this.thumbnailName = thumbnailName;
		this.storedThumbnailPath = storedThumbnailPath;
		this.viewCount = viewCount;
		this.popularity = popularity;
		this.description = description;
		this.isPublic = isPublic;
	}

	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

	public void setSyncedPages(SyncedPages syncedPages) {
		this.syncedPages = syncedPages;
	}

	public void editPost(String title, String content) { // 비어있는 데이터는 예외처리
		if (StringUtils.hasText(title)) {
			this.title = title;
		}
		if (StringUtils.hasText(content)) {
			this.content = content;
		}
	}

	public void addViewCount() {
		viewCount++;
	}
}
