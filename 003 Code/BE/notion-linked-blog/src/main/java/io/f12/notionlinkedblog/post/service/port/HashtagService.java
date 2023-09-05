package io.f12.notionlinkedblog.post.service.port;

import java.util.List;

import io.f12.notionlinkedblog.hashtag.exception.NoHashtagException;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface HashtagService {

	PostEntity addHashtags(List<String> hashtags, PostEntity post);

	PostEntity editHashtags(List<String> hashtagList, PostEntity post);

	List<Long> getPostIdsByHashtag(String hashtagName) throws NoHashtagException;
}
