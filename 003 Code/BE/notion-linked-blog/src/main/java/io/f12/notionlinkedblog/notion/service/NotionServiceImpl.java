package io.f12.notionlinkedblog.notion.service;

import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.NotionValidateMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.PostExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.SeriesExceptionMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserExceptionsMessages.*;
import static io.f12.notionlinkedblog.common.exceptions.message.ExceptionMessages.UserValidateMessages.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.common.exceptions.exception.AlreadyExistException;
import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.component.oauth.NotionOAuthComponent;
import io.f12.notionlinkedblog.notion.api.port.NotionService;
import io.f12.notionlinkedblog.notion.domain.converter.NotionBlockConverter;
import io.f12.notionlinkedblog.notion.exception.NoContentException;
import io.f12.notionlinkedblog.notion.infrastructure.multi.SyncedSeriesEntity;
import io.f12.notionlinkedblog.notion.infrastructure.single.SyncedPagesEntity;
import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;
import io.f12.notionlinkedblog.notion.service.port.SyncedSeriesRepository;
import io.f12.notionlinkedblog.post.api.response.PostSearchDto;
import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.PostRepository;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.series.service.port.SeriesRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;
import io.f12.notionlinkedblog.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.blocks.Blocks;
import notion.api.v1.request.blocks.RetrieveBlockChildrenRequest;
import notion.api.v1.request.blocks.RetrieveBlockRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotionServiceImpl implements NotionService {

	private final NotionOAuthComponent notionOAuthComponent;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final SeriesRepository seriesRepository;
	private final NotionBlockConverter notionBlockConverter;
	private final SyncedPagesRepository syncedPagesRepository;
	private final SyncedSeriesRepository syncedSeriesRepository;

	@Override
	public PostSearchDto saveSingleNotionPage(String path, Long userId) throws NotionAuthenticationException {

		String convertPath = convertPathToId(path);

		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		checkPostExist(convertPath);

		PostEntity savePost = postRepository.save(createPost(convertPath, userId));

		syncedPagesRepository.save(SyncedPagesEntity.builder()
			.pageId(convertPath)
			.post(savePost)
			.user(user)
			.build());

		return PostSearchDto.builder()
			.postId(savePost.getId())
			.title(savePost.getTitle())
			.content(savePost.getContent())
			.viewCount(savePost.getViewCount())
			.likes(0)
			.requestThumbnailLink(savePost.getStoredThumbnailPath())
			.description(savePost.getDescription())
			.createdAt(LocalDateTime.now())
			.countOfComments(0)
			.author(user.getUsername())
			.isLiked(false)
			.build();
	}

	@Override
	@Transactional
	public void saveMultipleNotionPage(String path, Long userId) throws NotionAuthenticationException {
		String convertPath = convertPathToId(path);

		if (syncedSeriesRepository.findByPageId(convertPath).isPresent()) {
			throw new AlreadyExistException(DATA_ALREADY_EXIST);
		}
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		String seriesTitle = getTitle(convertPath, userId);

		List<String> ids = getIds(convertPath, userId);

		SeriesEntity series = seriesRepository.save(SeriesEntity.builder()
			.user(user)
			.title(seriesTitle)
			.post(new ArrayList<>())
			.build());

		for (String id : ids) {
			PostEntity post = createPost(id, userId);
			syncedPagesRepository.findByPageId(id).ifPresentOrElse(s -> {
				s.getPost().editPost(post);
				series.addPost(s.getPost());
			}, () -> {
				PostEntity savePost = postRepository.save(post);
				series.addPost(savePost);
				syncedPagesRepository.save(SyncedPagesEntity.builder()
					.pageId(id)
					.post(savePost)
					.user(user)
					.build());
			});
		}
		syncedSeriesRepository.save(SyncedSeriesEntity.builder()
			.pageId(convertPath)
			.user(user)
			.series(seriesRepository.save(series))
			.build());
	}

	@Override
	public void editNotionPageToBlog(Long userId, PostEntity post) throws NotionAuthenticationException {
		Long postUserId = post.getUser().getId();
		checkSameUser(postUserId, userId);

		String content = getContent(post.getSyncedPages().getPageId(), userId);
		String title = getTitle(post.getSyncedPages().getPageId(), userId);
		post.editPost(title, content);

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

		return compareResult < 0;
	}

	@Override
	public void updateSeriesRequest(Long userId, String seriesId) throws NotionAuthenticationException {
		SyncedSeriesEntity existSeries = syncedSeriesRepository.findByPageId(seriesId)
			.orElseThrow(() -> new IllegalArgumentException(SERIES_NOT_EXIST));
		checkSameUser(userId, existSeries.getUser().getId());

		String seriesTitle = getTitle(existSeries.getPageId(), userId);
		existSeries.getSeries().setTitle(seriesTitle);
	}

	@Override
	public void updatePostRequest(Long userId, Long postId) throws NotionAuthenticationException {
		PostEntity existPost = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException(POST_NOT_EXIST));
		checkSameUser(userId, existPost.getUser().getId());

		PostEntity newPost = createPost(existPost.getSyncedPages().getPageId(), userId);
		existPost.editPost(newPost);
	}

	// private 매소드
	private PostEntity createPost(String path, Long userId) throws NotionAuthenticationException {
		String title = getTitle(path, userId);
		String content = getContent(path, userId);

		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		return PostEntity.builder()
			.user(user)
			.title(title)
			.content(content)
			.viewCount(0L)
			.popularity(0.0)
			.isPublic(true)
			.build();
	}

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

	private List<String> getIds(String fullPath, Long userId) throws NotionAuthenticationException {
		Blocks blocks;
		NotionClient client = createClient(userId);
		RetrieveBlockChildrenRequest retrieveBlockChildrenRequest
			= new RetrieveBlockChildrenRequest(fullPath);
		try {
			blocks = client.retrieveBlockChildren(retrieveBlockChildrenRequest);
		} finally {
			client.close();
		}

		List<Block> blockedContents = blocks.getResults();
		List<String> results = new ArrayList<>();

		for (Block blockedContent : blockedContents) {
			if (blockedContent.getType().equals(BlockType.ChildPage)) {
				results.add((blockedContent.getId()));
			}
		}

		if (results.isEmpty()) {
			throw new NoContentException();
		}

		return results;
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

	private String convertPathToId(String path) {
		String[] split = path.split("-");
		String rawId = split[split.length - 1];
		return rawId.substring(0, 8) + "-" + rawId.substring(8, 12) + "-" + rawId.substring(12, 16) + "-"
			+ rawId.substring(16, 20) + "-" + rawId.substring(20);

	}

	private void checkSameUser(Long id1, Long id2) {
		if (!id1.equals(id2)) {
			throw new AccessDeniedException(USER_NOT_MATCH);
		}
	}

	private void checkPostExist(String path) {
		if (syncedPagesRepository.findByPageId(path).isPresent()) {
			throw new AlreadyExistException(DATA_ALREADY_EXIST);
		}
	}

}


