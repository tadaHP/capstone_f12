package io.f12.notionlinkedblog.medium.post.infrastructure;

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

import io.f12.notionlinkedblog.common.config.TestQuerydslConfiguration;
import io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages;
import io.f12.notionlinkedblog.like.infrastructure.LikeEntity;
import io.f12.notionlinkedblog.like.service.port.LikeRepository;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;

@DataJpaTest
@Import(TestQuerydslConfiguration.class)
class LikeDataRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private EntityManager entityManager;

	String title = "testTitle";
	String content = "testContent";

	private UserEntity user;
	private PostEntity post;

	@BeforeEach
	void init() {
		UserEntity savedUser = UserEntity.builder()
			.username("tester")
			.email("test@test.com")
			.password("nope")
			.build();
		user = userRepository.save(savedUser);

		PostEntity savedPost = PostEntity.builder()
			.title(title)
			.content(content)
			.user(user)
			.isPublic(true)
			.build();
		post = postRepository.save(savedPost);
	}

	@AfterEach
	void clear() {
		postRepository.deleteAll();
		userRepository.deleteAll();
		entityManager.createNativeQuery("ALTER SEQUENCE user_seq RESTART WITH 1").executeUpdate();
		entityManager.createNativeQuery("ALTER SEQUENCE post_seq RESTART WITH 1").executeUpdate();
	}

	@DisplayName("LikeEntity 존재시 PostEntity 데이터 조회")
	@Test
	void getPostWhenLikeExist() {

		//given
		int count = 50;
		for (int i = 0; i < count; i++) {
			LikeEntity save = likeRepository.save(LikeEntity.builder()
				.post(post)
				.user(user)
				.build()
			);
		}
		entityManager.flush();
		entityManager.clear();
		//when
		PostEntity getPost = postRepository.findById(post.getId())
			.orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.PostExceptionsMessages.POST_NOT_EXIST));
		// //then
		assertThat(getPost.getLikes().size()).isEqualTo(count);
	}

	@DisplayName("LikeEntity 미존재시 PostEntity 데이터 조회")
	@Test
	void getPostWhenLikeNonExist() {
		//given
		int count = 0;
		entityManager.flush();
		entityManager.clear();
		//when
		PostEntity getPost = postRepository.findById(post.getId())
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
			LikeEntity save = likeRepository.save(LikeEntity.builder()
				.post(post)
				.user(user)
				.build()
			);
		}

		PostEntity savedPost = PostEntity.builder()
			.title(title)
			.content(content)
			.user(user)
			.isPublic(true)
			.build();
		postRepository.save(savedPost);

		entityManager.flush();
		entityManager.clear();

		// when
		List<PostEntity> posts = postRepository.findByPostIdForTrend();
		PostEntity postA = posts.get(0);
		PostEntity postB = posts.get(1);

		//then
		assertThat(posts).size().isEqualTo(2);
		assertThat(postA.getLikes()).size().isEqualTo(50);

	}

}