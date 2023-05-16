package io.f12.notionlinkedblog.repository.comments;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import io.f12.notionlinkedblog.domain.comments.Comments;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@DataJpaTest
class CommentsDataRepositoryTest {

	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private PostDataRepository postDataRepository;
	@Autowired
	private CommentsDataRepository commentsDataRepository;

	private User savedUser;
	private Post savedPost;
	private Comments savedComment;

	@BeforeEach
	void init() {
		User user = User.builder()
			.username("tester")
			.email("test@gamil.com")
			.password("1234")
			.build();
		savedUser = userDataRepository.save(user);

		Post post = Post.builder()
			.user(savedUser)
			.title("testTitle")
			.content("testContent")
			.build();
		savedPost = postDataRepository.save(post);
		Comments comments = Comments.builder()
			.user(savedUser)
			.post(savedPost)
			.content("testComment")
			.depth(0)
			.build();
		savedComment = commentsDataRepository.save(comments);

	}

	@AfterEach
	void clear() {
		commentsDataRepository.deleteAll();
		postDataRepository.deleteAll();
		userDataRepository.deleteAll();
	}

	@DisplayName("PostId로 댓글 조회")
	@Nested
	class GetCommentsWithPostId {
		@DisplayName("성공 케이스")
		@Test
		void successCase() {
			//given
			Comments testComment2 = commentsDataRepository.save(Comments.builder()
				.user(savedUser)
				.post(savedPost)
				.content("testComment2")
				.depth(0)
				.build());

			//when
			List<Comments> comments = commentsDataRepository.findByPostId(savedPost.getId());
			Comments comments1 = comments.get(0);
			Comments comments2 = comments.get(1);
			//then
			assertThat(comments).size().isEqualTo(2);
			assertThat(comments1).extracting("content").isEqualTo(savedComment.getContent());
			assertThat(comments2).extracting("content").isEqualTo(testComment2.getContent());
		}
	}

}