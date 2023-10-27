package io.f12.notionlinkedblog.notion.api.port;

import java.time.LocalDateTime;

import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.notion.exception.NoAccessTokenException;
import io.f12.notionlinkedblog.notion.exception.NoTitleException;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.security.login.ajax.dto.LoginUser;

public interface NotionService {

	void notionAccessAvailable(LoginUser loginUser) throws NoAccessTokenException;

	PostSearchDto saveSingleNotionPage(String path, Long userId) throws
		NotionAuthenticationException,
		NoTitleException;

	void editNotionPageToBlog(Long userId, PostEntity post) throws NotionAuthenticationException, NoTitleException;

	void saveMultipleNotionPage(String path, Long userId) throws NotionAuthenticationException, NoTitleException;

	boolean needUpdate(Long userId, String pageId, LocalDateTime updateTime)
		throws NotionAuthenticationException;

	void updateSeriesRequest(Long userId, String seriesId) throws NotionAuthenticationException, NoTitleException;

	void updatePostRequest(Long userId, Long postId) throws NotionAuthenticationException, NoTitleException;
}
