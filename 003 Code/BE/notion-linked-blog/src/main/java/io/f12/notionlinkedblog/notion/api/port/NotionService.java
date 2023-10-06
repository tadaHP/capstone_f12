package io.f12.notionlinkedblog.notion.api.port;

import java.time.LocalDateTime;

import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;

public interface NotionService {

	public PostSearchDto saveSingleNotionPage(String path, Long userId) throws NotionAuthenticationException;

	void editNotionPageToBlog(Long userId, PostEntity post) throws NotionAuthenticationException;

	void saveMultipleNotionPage(String path, Long userId) throws NotionAuthenticationException;

	boolean needUpdate(Long userId, String pageId, LocalDateTime updateTime)
		throws NotionAuthenticationException;

	void updateSeriesRequest(Long userId, String seriesId) throws NotionAuthenticationException;

	void updatePostRequest(Long userId, Long postId) throws NotionAuthenticationException;
}
