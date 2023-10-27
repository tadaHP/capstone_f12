package io.f12.notionlinkedblog.small.hashtag.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;
import io.f12.notionlinkedblog.hashtag.serivce.HashtagServiceImpl;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.small.mock.FakeHashtagRepository;
import io.f12.notionlinkedblog.small.mock.FakePostRepository;

public class HashtagServiceTest {

	private HashtagServiceImpl hashtagService;

	FakeHashtagRepository fakeHashtagRepository;
	FakePostRepository fakePostRepository;

	@BeforeEach
	void init() {
		this.fakeHashtagRepository = new FakeHashtagRepository();
		this.fakePostRepository = new FakePostRepository();
		this.hashtagService = HashtagServiceImpl.builder()
			.hashtagRepository(fakeHashtagRepository)
			.postRepository(fakePostRepository)
			.build();

		PostEntity post = PostEntity.builder()
			.id(1L)
			.title("testTitle")
			.content("testContents")
			.build();

		ArrayList<PostEntity> postList = new ArrayList<>();
		postList.add(post);

		HashtagEntity tagA = HashtagEntity.builder()
			.post(postList)
			.name("tagA")
			.build();
		HashtagEntity tagB = HashtagEntity.builder()
			.post(postList)
			.name("tagB")
			.build();

		ArrayList<HashtagEntity> hashtagList = new ArrayList<>();
		hashtagList.add(tagA);
		hashtagList.add(tagB);
		post.changeHashtags(hashtagList);

		fakePostRepository.save(post);
		fakeHashtagRepository.save(tagA);
		fakeHashtagRepository.save(tagB);

	}

	@DisplayName("해쉬태그 추가 테스트")
	@Nested
	class AddHashtagTest {
		@DisplayName("성공 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("해쉬태그 없는 경우(비어있는 경우)")
			@Test
			void noHashtag() {
				//given
				PostEntity post = PostEntity.builder()
					.title("testTitle2")
					.content("testContent2")
					.build();
				ArrayList<String> hashtags = new ArrayList<>();
				//when
				PostEntity savedPost = hashtagService.addHashtags(hashtags, post);
				//then
				List<HashtagEntity> hashtagList = savedPost.getHashtag();
				assertThat(hashtagList).isNull();
			}

			@DisplayName("해쉬태그 없는 경우(null 경우)")
			@Test
			void nullHashtag() {
				//given
				PostEntity post = PostEntity.builder()
					.title("testTitle2")
					.content("testContent2")
					.build();
				//when
				PostEntity savedPost = hashtagService.addHashtags(null, post);
				//then
				List<HashtagEntity> hashtagList = savedPost.getHashtag();
				assertThat(hashtagList).isNull();
			}

			@DisplayName("해쉬태그 존재")
			@Test
			void withHashtag() {
				//given
				PostEntity post = PostEntity.builder()
					.title("testTitle2")
					.content("testContent2")
					.build();
				ArrayList<String> hashtags = new ArrayList<>();
				hashtags.add("tagB");
				hashtags.add("tagC");
				//when
				PostEntity savedPost = hashtagService.addHashtags(hashtags, post);
				//then
				List<HashtagEntity> hashtagList = savedPost.getHashtag();
				assertThat(hashtagList).size().isEqualTo(2);
			}

		}
	}

	@DisplayName("해쉬태그 수정, 삭제 테스트")
	@Nested
	class EditHashtagTest {
		@DisplayName("해쉬태그 부분수정")
		@Test
		void editPartialHashtag() {
			//given
			PostEntity post = fakePostRepository.findById(1L).get();
			ArrayList<String> hashtags = new ArrayList<>();
			hashtags.add("tagB");
			hashtags.add("tagC");
			//when
			PostEntity savedPost = hashtagService.editHashtags(hashtags, post);
			//then
			List<HashtagEntity> hashtagList = savedPost.getHashtag();
			assertThat(hashtagList).size().isEqualTo(2);
		}

		@DisplayName("해쉬태그 전체수정")
		@Test
		void editEntireHashtag() {
			//given
			PostEntity post = fakePostRepository.findById(1L).get();
			ArrayList<String> hashtags = new ArrayList<>();
			hashtags.add("tagC");
			hashtags.add("tagD");
			hashtags.add("tagE");
			//when
			PostEntity savedPost = hashtagService.editHashtags(hashtags, post);
			//then
			List<HashtagEntity> hashtagList = savedPost.getHashtag();
			assertThat(hashtagList).size().isEqualTo(3);
		}

		@DisplayName("해쉬태그 삭제")
		@Test
		void removeHashtag() {
			//given
			PostEntity post = fakePostRepository.findById(1L).get();
			ArrayList<String> hashtags = new ArrayList<>();
			//when
			PostEntity savedPost = hashtagService.editHashtags(hashtags, post);
			//then
			List<HashtagEntity> hashtagList = savedPost.getHashtag();
			assertThat(hashtagList).isEmpty();
		}
	}

}
