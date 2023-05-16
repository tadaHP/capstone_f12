package io.f12.notionlinkedblog.domain.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostSearchResponseDto {
	int pageSize;
	int pageNow;
	List<PostSearchDto> posts;
}
