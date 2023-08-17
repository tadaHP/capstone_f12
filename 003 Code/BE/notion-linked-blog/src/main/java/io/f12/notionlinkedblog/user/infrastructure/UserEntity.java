package io.f12.notionlinkedblog.user.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.f12.notionlinkedblog.comments.infrastructure.CommentsEntity;
import io.f12.notionlinkedblog.common.infrastructure.BaseTimeEntity;
import io.f12.notionlinkedblog.like.infrastructure.LikeEntity;
import io.f12.notionlinkedblog.notion.infrastructure.SyncedPagesEntity;
import io.f12.notionlinkedblog.oauth.infrastructure.NotionOauthEntity;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.user.domain.User;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserSocialInfoEditDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "users", indexes = @Index(name = "user_index", columnList = "email"))
@SequenceGenerator(
	name = "user_seq_generator",
	sequenceName = "user_seq",
	allocationSize = 1
)
public class UserEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
	private Long id;

	@OneToMany(mappedBy = "user")
	private List<PostEntity> posts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<CommentsEntity> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<LikeEntity> likes = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<SeriesEntity> series = new ArrayList<>();

	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
	private NotionOauthEntity notionOauth;

	@OneToMany(mappedBy = "user")
	private List<SyncedPagesEntity> syncedPages;

	@Column(nullable = false)
	private String username;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	private String profile;
	private String introduction;
	private String blogTitle;
	private String githubLink;
	private String instagramLink;

	@Builder
	public UserEntity(Long id, String username, String email, String password, String profile, String introduction,
		String blogTitle, String githubLink, String instagramLink) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.profile = profile;
		this.introduction = introduction;
		this.blogTitle = blogTitle;
		this.githubLink = githubLink;
		this.instagramLink = instagramLink;
	}

	public User toModel() {
		return User.builder()
			.id(this.id)
			.posts(this.posts.stream().map(PostEntity::toModel).collect(Collectors.toList()))
			.comments(this.comments.stream().map(CommentsEntity::toModel).collect(Collectors.toList()))
			.likes(this.likes.stream().map(LikeEntity::toModel).collect(Collectors.toList()))
			.series(this.series.stream().map(SeriesEntity::toModel).collect(Collectors.toList()))
			.notionOauth(this.notionOauth.toModel())
			.syncedPages(this.syncedPages.stream().map(SyncedPagesEntity::toModel).collect(Collectors.toList()))
			.username(this.username)
			.email(this.email)
			.password(this.password)
			.profile(this.profile)
			.introduction(this.introduction)
			.blogTitle(this.blogTitle)
			.githubLink(this.githubLink)
			.instagramLink(this.instagramLink)
			.build();
	}

	//have to move
	public void editProfile(UserBasicInfoEditDto editDto) {
		this.username = editDto.getUsername();
		this.introduction = editDto.getIntroduction();
	}

	public void editProfile(UserSocialInfoEditDto editDto) {
		this.githubLink = editDto.getGithubLink();
		this.instagramLink = editDto.getInstagramLink();
	}

	public void editProfile(UserBlogTitleEditDto editDto) {
		this.blogTitle = editDto.getBlogTitle();
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
}
