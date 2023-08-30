package io.f12.notionlinkedblog.post.infrastructure;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import io.f12.notionlinkedblog.common.infrastructure.PostTimeEntity;
import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;
import io.f12.notionlinkedblog.like.infrastructure.LikeEntity;
import io.f12.notionlinkedblog.notion.infrastructure.SyncedPagesEntity;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
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
public class PostEntity extends PostTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq_generator")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	@NotNull
	private UserEntity user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentsEntity> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LikeEntity> likes = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "series_id")
	private SeriesEntity series;

	@OneToOne(mappedBy = "post", cascade = CascadeType.REMOVE)
	private SyncedPagesEntity syncedPages;

	@ManyToMany
	@JoinTable(name = "posts_hashtags",
		joinColumns = @JoinColumn(name = "hashtags_id"),
		inverseJoinColumns = @JoinColumn(name = "post_id"))
	private List<HashtagEntity> hashtag;

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
	public PostEntity(LocalDateTime createdAt, LocalDateTime updatedAt, Long id, UserEntity user,
		List<CommentsEntity> comments, List<LikeEntity> likes, List<HashtagEntity> hashtag, SeriesEntity series,
		SyncedPagesEntity syncedPages, String title, String content, String thumbnailName, String storedThumbnailPath,
		Long viewCount, Double popularity, String description, Boolean isPublic) {
		super(createdAt, updatedAt);
		this.id = id;
		this.user = user;
		this.comments = comments;
		this.likes = likes;
		this.hashtag = hashtag;
		this.syncedPages = syncedPages;
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

	public void changeHashtags(List<HashtagEntity> hashtags) {
		this.hashtag = hashtags;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//have to move
	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

	public void setSyncedPages(SyncedPagesEntity syncedPages) {
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

	public void setSeries(SeriesEntity series) {
		this.series = series;
	}
}
