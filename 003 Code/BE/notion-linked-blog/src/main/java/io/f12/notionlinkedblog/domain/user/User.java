package io.f12.notionlinkedblog.domain.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.f12.notionlinkedblog.domain.BaseTimeEntity;
import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.dto.info.UserEditDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
@SequenceGenerator(
	name = "user_seq_generator",
	sequenceName = "user_seq",
	allocationSize = 1
)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
	private Long id;

	@OneToMany(mappedBy = "id")
	private List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "id")
	private List<Comments> comments = new ArrayList<>();

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

	public void editProfile(UserEditDto editDto) {
		this.username = editDto.getUsername();
		this.profile = editDto.getProfile();
		this.blogTitle = editDto.getBlogTitle();
		this.githubLink = editDto.getGithubLink();
		this.instagramLink = editDto.getInstagramLink();
		this.introduction = editDto.getIntroduction();
	}

}
