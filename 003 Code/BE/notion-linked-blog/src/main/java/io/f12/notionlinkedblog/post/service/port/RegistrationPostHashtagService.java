package io.f12.notionlinkedblog.post.service.port;

import java.util.List;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface RegistrationPostHashtagService {

	PostEntity addHashtags(List<String> hashtags, PostEntity post);

	PostEntity editHashtags(List<String> hashtagList, PostEntity post);
}
