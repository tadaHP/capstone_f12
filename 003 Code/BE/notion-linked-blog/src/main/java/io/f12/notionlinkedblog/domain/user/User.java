package io.f12.notionlinkedblog.domain.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.f12.notionlinkedblog.domain.BaseTimeEntity;
import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.series.Series;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.domain.user.dto.request.UserSocialInfoEditDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users", indexes = @Index(name = "user_index", columnList = "email"))
@SequenceGenerator(
	name = "user_seq_generator",
	sequenceName = "user_seq",
	allocationSize = 1
)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
	private Long id;

	@OneToMany(mappedBy = "user")
	private List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Comments> comments = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Like> likes = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Series> series = new ArrayList<>();

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
	public User(Long id, String username, String email, String password, String profile, String introduction,
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
