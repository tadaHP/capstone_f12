package io.f12.notionlinkedblog.user.api.response;

import java.util.List;

import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPostsDto {
	private final Integer postsSize;
	private final List<PostSearchDto> data;
}
