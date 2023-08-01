package io.f12.notionlinkedblog.repository.like;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import io.f12.notionlinkedblog.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.domain.likes.Like;
import io.f12.notionlinkedblog.domain.post.Post;
import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.exceptions.message.ExceptionMessages;
import io.f12.notionlinkedblog.repository.post.PostDataRepository;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
class LikeDataRepositoryTest {
	@Autowired
	private PostDataRepository postDataRepository;
	@Autowired
	private UserDataRepository userDataRepository;
	@Autowired
	private LikeDataRepository likeDataRepository;
	@Autowired
	private EntityManager entityManager;

	String title = "testTitle";
	String content = "testContent";

	private User user;
	private Post post;

	@BeforeEach
	void init() {
		User savedUser = User.builder()
			.username("tester")
			.email("test@test.com")
			.password("nope")
			.build();
		user = userDataRepository.save(savedUser);

		Post savedPost = Post.builder()
			.title(title)
			.content(content)
			.user(user)
			.isPublic(true)
			.build();
		post = postDataRepository.save(savedPost);
	}

	@AfterEach
	void clear() {
		postDataRepository.deleteAll();
		userDataRepository.deleteAll();
		entityManager.createNativeQuery("ALTER SEQUENCE user_seq RESTART WITH 1").executeUpdate();
		entityManager.createNativeQuery("ALTER SEQUENCE post_seq RESTART WITH 1").executeUpdate();
	}

	@DisplayName("Like 존재시 Post 데이터 조회")
	@Test
	void getPostWhenLikeExist() {

		//given
		int count = 50;
		for (int i = 0; i < count; i++) {
			Like save = likeDataRepository.save(Like.builder()
				.post(post)
				.user(user)
				.build()
			);
		}
		entityManager.flush();
		entityManager.clear();
		//when
		Post getPost = postDataRepository.findById(post.getId())
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.PostExceptionsMessages.POST_NOT_EXIST));
		// //then
		assertThat(getPost.getLikes().size()).isEqualTo(count);
	}

	@DisplayName("Like 미존재시 Post 데이터 조회")
	@Test
	void getPostWhenLikeNonExist() {
		//given
		int count = 0;
		entityManager.flush();
		entityManager.clear();
		//when
		Post getPost = postDataRepository.findById(post.getId())
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.PostExceptionsMessages.POST_NOT_EXIST));
		//then
		assertThat(getPost.getLikes()).size().isEqualTo(count);
	}

	@DisplayName("Trend 관련 쿼리")
	@Test
	void trend() {
		//given
		int count = 50;
		for (int i = 0; i < count; i++) {
			Like save = likeDataRepository.save(Like.builder()
				.post(post)
				.user(user)
				.build()
			);
		}

		Post savedPost = Post.builder()
			.title(title)
			.content(content)
			.user(user)
			.isPublic(true)
			.build();
		postDataRepository.save(savedPost);

		entityManager.flush();
		entityManager.clear();

		// when
		List<Post> posts = postDataRepository.findByPostIdForTrend();
		Post postA = posts.get(0);
		Post postB = posts.get(1);

		//then
		assertThat(posts).size().isEqualTo(2);
		assertThat(postA.getLikes()).size().isEqualTo(50);

	}

}