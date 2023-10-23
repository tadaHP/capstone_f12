package io.f12.notionlinkedblog.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.f12.notionlinkedblog.hashtag.infrastructure.HashtagEntity;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.user.domain.dto.UserSeriesInfoDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityConverter {
	private final AwsBucket awsBucket;

	public List<UserSeriesInfoDto> convertSeriesToSeriesDto(List<SeriesEntity> series) {
		return series.stream().map(s -> {
			return UserSeriesInfoDto.builder()
				.seriesId(s.getId())
				.title(s.getTitle())
				.build();
		}).collect(Collectors.toList());
	}

	public List<PostSearchDto> convertPostToPostDto(List<PostEntity> posts) {
		return posts.stream().map(p -> {
			String thumbnailLink = null;
			Integer commentsSize = null;
			Integer likeSize = null;

			if (p.getThumbnailName() != null) {
				thumbnailLink = awsBucket.makeFileUrl(p.getThumbnailName());
			} else {
				thumbnailLink = getDefaultThumbnail();
			}

			if (p.getLikes() != null) {
				likeSize = p.getLikes().size();
			}
			if (p.getComments() != null) {
				commentsSize = p.getComments().size();
			}

			List<String> hashtagList = getHashtagsFromPost(p);

			return PostSearchDto.builder()
				.postId(p.getId())
				.title(p.getTitle())
				.content(p.getContent())
				.viewCount(p.getViewCount())
				.likes(likeSize)
				.requestThumbnailLink(thumbnailLink)
				.description(p.getDescription())
				.createdAt(p.getCreatedAt())
				.countOfComments(commentsSize)
				.author(p.getUser().getUsername())
				.avatar(awsBucket.makeFileUrl(p.getUser().getProfile()))
				.hashtags(hashtagList)
				.build();
		}).collect(Collectors.toList());
	}

	public List<String> getHashtagsFromPost(PostEntity savedPost) {
		List<HashtagEntity> hashtags = savedPost.getHashtag();
		if (hashtags == null) {
			return new ArrayList<>();
		}
		return hashtags.stream()
			.map(HashtagEntity::getName)
			.collect(Collectors.toList());
	}

	public String getDefaultThumbnail() {
		return awsBucket.makeFileUrl("thumbnail/DefaultThumbnail.jpg");
	}
}
