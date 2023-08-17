package io.f12.notionlinkedblog.notion.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserValidateMessages.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.component.oauth.NotionOAuthComponent;
import io.f12.notionlinkedblog.notion.api.port.NotionService;
import io.f12.notionlinkedblog.notion.domain.converter.NotionBlockConverter;
import io.f12.notionlinkedblog.notion.infrastructure.SyncedPagesEntity;
import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.Blocks;
import notion.api.v1.model.search.SearchResult;
import notion.api.v1.request.blocks.RetrieveBlockChildrenRequest;
import notion.api.v1.request.blocks.RetrieveBlockRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotionServiceImpl implements NotionService {

	private final NotionOAuthComponent notionOAuthComponent;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final NotionBlockConverter notionBlockConverter;
	private final SyncedPagesRepository syncedPagesRepository;

	public void setNotionLinkPages(String path, Long userId) {
		UserEntity user = userRepository.findUserById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		String pathToId = convertPathToId(path);
		SyncedPagesEntity syncedPages = SyncedPagesEntity.builder()
			.pageId(pathToId)
			.user(user)
			.build();
		syncedPagesRepository.save(syncedPages);
	}

	//아래 도메인 으로 묶기
	// public PostSearchDto saveNotionPageToBlog(String path, Long userId) throws NotionAuthenticationException {
	// 	String convertPath = convertPathToId(path);
	// 	Notion notion = notionDataRepository.findByPathValue(convertPath).orElse(null);
	// 	if (notion != null) {
	// 		throw new AlreadyExistException(DATA_ALREADY_EXIST);
	// 	}
	// 	String title = getTitle(convertPath, userId);
	// 	String content = getContent(convertPath, userId);
	//
	// 	UserEntity user = userRepository.findById(userId)
	// 		.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
	// 	PostEntity savePost = postRepository.save(PostEntity.builder()
	// 		.user(user)
	// 		.title(title)
	// 		.content(content)
	// 		.viewCount(0L)
	// 		.popularity(0.0)
	// 		.isPublic(true)
	// 		.build());
	// 	notionDataRepository.save(Notion.builder()
	// 		.notionId(convertPath)
	// 		.post(savePost)
	// 		.build());
	//
	// 	return PostSearchDto.builder()
	// 		.postId(savePost.getId())
	// 		.title(savePost.getTitle())
	// 		.content(savePost.getContent())
	// 		.viewCount(savePost.getViewCount())
	// 		.likes(0)
	// 		.requestThumbnailLink(savePost.getStoredThumbnailPath())
	// 		.description(savePost.getDescription())
	// 		.createdAt(LocalDateTime.now())
	// 		.countOfComments(0)
	// 		.author(user.getUsername())
	// 		.isLiked(false)
	// 		.build();
	// }

	@Override
	public void editNotionPageToBlog(Long userId, PostEntity post) throws NotionAuthenticationException {
		Long postUserId = post.getUser().getId();
		checkSameUser(postUserId, userId);

		String content = getContent(post.getSyncedPages().getPageId(), userId);
		String title = getTitle(post.getSyncedPages().getPageId(), userId);
		post.editPost(title, content);

		// return PostSearchDto.builder()
		// 	.postId(post.getId())
		// 	.title(post.getTitle())
		// 	.content(post.getContent())
		// 	.viewCount(post.getViewCount())
		// 	.likes(post.getLikes().size())
		// 	.requestThumbnailLink(post.getStoredThumbnailPath())
		// 	.description(post.getDescription())
		// 	.createdAt(post.getCreatedAt())
		// 	.countOfComments(post.getComments().size())
		// 	.author(post.getUser().getUsername())
		// 	.isLiked(false)
		// 	.build();
	}

	@Override
	public List<String> getEveryPages(String accessToken) throws NotionAuthenticationException {
		NotionClient client = createClient(accessToken);
		ArrayList<String> pageIds = new ArrayList<>();
		List<SearchResult> results = null;
		try (client) {
			results = client.search("").getResults();
		}

		for (SearchResult searchResult : results) {
			pageIds.add(searchResult.getId());
		}
		return pageIds;
	}

	@Override
	public void initEveryPages(List<String> pageIds, Long userId, String accessCode) throws
		NotionAuthenticationException {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		for (String pageId : pageIds) {
			String title = getTitleTemp(pageId, accessCode);
			String content = getContentTemp(pageId, accessCode);
			PostEntity post = postRepository.save(PostEntity.builder()
				.user(user)
				.title(title)
				.content(content)
				.isPublic(true)
				.comments(new ArrayList<>())
				.likes(new ArrayList<>())
				.viewCount(0L)
				.build());
			SyncedPagesEntity syncedPages = syncedPagesRepository.save(SyncedPagesEntity.builder()
				.pageId(pageId)
				.user(user)
				.post(post)
				.build());
			post.setSyncedPages(syncedPages);
		}

	}

	@Override
	public boolean needUpdate(Long userId, String pageId, LocalDateTime updateTime)
		throws NotionAuthenticationException {
		NotionClient client = createClient(userId);
		RetrieveBlockRequest retrieveBlockRequest = new RetrieveBlockRequest(pageId);
		String lastEditedTime;
		try (client) {
			Block block = client.retrieveBlock(retrieveBlockRequest);
			lastEditedTime = block.getLastEditedTime();
		}
		Instant instant = Instant.parse(lastEditedTime);
		LocalDateTime serverUpdateTime = instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
		int compareResult = updateTime.compareTo(serverUpdateTime);

		boolean b = compareResult < 0;

		return compareResult < 0;
		// return false;
	}

	// private 매소드
	private String getTitle(String fullPath, Long userId) throws NotionAuthenticationException {
		Block block;
		NotionClient client = createClient(userId);
		RetrieveBlockRequest retrieveBlockRequest = new RetrieveBlockRequest(fullPath);
		try (client) {
			block = client.retrieveBlock(retrieveBlockRequest);
		}

		return block.asChildPage().getChildPage().getTitle();
	}

	private String getContent(String fullPath, Long userId) throws NotionAuthenticationException {
		Blocks blocks;
		NotionClient client = createClient(userId);
		StringBuilder stringBuilder = new StringBuilder();
		RetrieveBlockChildrenRequest retrieveBlockChildrenRequest
			= new RetrieveBlockChildrenRequest(fullPath);
		try {
			blocks = client.retrieveBlockChildren(retrieveBlockChildrenRequest);
		} finally {
			client.close();
		}

		List<Block> blockedContents = blocks.getResults();

		for (Block blockedContent : blockedContents) {
			stringBuilder.append(notionBlockConverter.doFilter(blockedContent, client));
		}
		return stringBuilder.toString();
	}

	private String getTitleTemp(String fullPath, String accessCode) throws NotionAuthenticationException {
		Block block;
		NotionClient client = createClient(accessCode);
		RetrieveBlockRequest retrieveBlockRequest = new RetrieveBlockRequest(fullPath);
		try (client) {
			block = client.retrieveBlock(retrieveBlockRequest);
		}

		return block.asChildPage().getChildPage().getTitle();
	}

	private String getContentTemp(String fullPath, String accessCode) throws NotionAuthenticationException {
		Blocks blocks;
		NotionClient client = createClient(accessCode);
		StringBuilder stringBuilder = new StringBuilder();
		RetrieveBlockChildrenRequest retrieveBlockChildrenRequest
			= new RetrieveBlockChildrenRequest(fullPath);
		try {
			blocks = client.retrieveBlockChildren(retrieveBlockChildrenRequest);
		} finally {
			client.close();
		}

		List<Block> blockedContents = blocks.getResults();

		for (Block blockedContent : blockedContents) {
			stringBuilder.append(notionBlockConverter.doFilter(blockedContent, client));
		}
		return stringBuilder.toString();
	}

	private NotionClient createClient(Long userId) throws NotionAuthenticationException {
		String accessToken = userRepository.findUserByIdForNotionAuthToken(userId)
			.orElseThrow(NotionAuthenticationException::new)
			.getNotionOauth()
			.getAccessToken();

		NotionClient notionClient = new NotionClient();
		notionClient.setClientId(notionOAuthComponent.getClientId());
		notionClient.setClientSecret(notionOAuthComponent.getClientSecret());
		notionClient.setRedirectUri(notionOAuthComponent.getRedirectUrl());
		notionClient.setToken(accessToken);

		return notionClient;
	}

	private NotionClient createClient(String accessToken) throws NotionAuthenticationException {
		NotionClient notionClient = new NotionClient();
		notionClient.setClientId(notionOAuthComponent.getClientId());
		notionClient.setClientSecret(notionOAuthComponent.getClientSecret());
		notionClient.setRedirectUri(notionOAuthComponent.getRedirectUrl());
		notionClient.setToken(accessToken);

		return notionClient;
	}

	private String convertPathToId(String path) {
		String[] split = path.split("-");
		return split[split.length - 1];
	}

	private void checkSameUser(Long id1, Long id2) {
		if (!id1.equals(id2)) {
			throw new AccessDeniedException(USER_NOT_MATCH);
		}
	}

}


