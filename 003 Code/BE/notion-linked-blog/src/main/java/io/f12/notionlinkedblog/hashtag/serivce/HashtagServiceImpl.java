package io.f12.notionlinkedblog.hashtag.serivce;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;
import io.f12.notionlinkedblog.hashtag.serivce.port.HashtagRepository;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.post.service.port.RegistrationPostHashtagService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@Builder
@RequiredArgsConstructor
public class HashtagServiceImpl implements RegistrationPostHashtagService {

	private final HashtagRepository hashtagRepository;
	private final PostRepository postRepository;

	@Override
	public PostEntity addHashtags(List<String> hashtags, PostEntity post) {
		if (hashtags == null) {
			return post;
		}
		if (hashtags.isEmpty()) {
			return post;
		}
		List<HashtagEntity> domainHashtag = findHashtag(hashtags);

		addPostsToHashtag(post, domainHashtag);
		post.changeHashtags(domainHashtag);

		for (HashtagEntity hashtag : domainHashtag) {
			hashtagRepository.save(hashtag);
		}
		postRepository.save(post);

		return post;
	}

	@Override
	public PostEntity editHashtags(List<String> hashtagList, PostEntity post) {
		removeHashtags(post);
		return addHashtags(hashtagList, post);
	}

	private void removeHashtags(PostEntity post) {
		List<HashtagEntity> newHashtagList = new ArrayList<>();
		post.changeHashtags(newHashtagList);
	}

	private void addPostsToHashtag(PostEntity post, List<HashtagEntity> domainHashtag) {
		for (HashtagEntity hashtag : domainHashtag) {
			hashtag.addPost(post);
		}
	}

	private List<HashtagEntity> findHashtag(List<String> hashtags) {
		List<HashtagEntity> returnHashtags = new ArrayList<>();
		for (String hashtag : hashtags) {
			Optional<HashtagEntity> findEntity = hashtagRepository.findByName(hashtag);
			if (findEntity.isPresent()) {
				returnHashtags.add(findEntity.get());
			} else {
				returnHashtags.add(hashtagRepository.save(HashtagEntity.builder()
					.name(hashtag)
					.post(new ArrayList<>())
					.build()));
			}
		}

		return returnHashtags;
	}
}
