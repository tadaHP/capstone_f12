package io.f12.notionlinkedblog.notion.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.notion.api.port.NotionService;
import io.f12.notionlinkedblog.notion.exception.NoTitleException;
import io.f12.notionlinkedblog.notion.infrastructure.multi.SyncedSeriesEntity;
import io.f12.notionlinkedblog.notion.infrastructure.single.SyncedPagesEntity;
import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;
import io.f12.notionlinkedblog.notion.service.port.SyncedSeriesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNotionSchedule {

	private final SyncedPagesRepository syncedPagesRepository;
	private final SyncedSeriesRepository syncedSeriesRepository;
	private final NotionService notionService;

	@Scheduled(fixedDelay = 3600000) // 1시간 (임시 설정)
	public void updatePostData() throws NotionAuthenticationException, NoTitleException {
		List<SyncedPagesEntity> everyData = syncedPagesRepository.findAll();

		for (SyncedPagesEntity data : everyData) {
			Long userId = data.getUser().getId();
			String pageId = data.getPageId();
			LocalDateTime updateTime = data.getPost().getUpdatedAt();
			if (notionService.needUpdate(userId, pageId, updateTime)) {
				notionService.editNotionPageToBlog(userId, data.getPost());
			}
		}

	}

	@Scheduled(fixedDelay = 3600000) // 1시간 (임시 설정)
	public void updateSeriesData() throws NotionAuthenticationException, NoTitleException {
		List<SyncedSeriesEntity> everyData
			= syncedSeriesRepository.findAll();

		for (SyncedSeriesEntity series : everyData) {
			Long userId = series.getUser().getId();
			String id = series.getPageId();
			LocalDateTime updateTime = series.getSeries().getUpdatedAt();
			if (notionService.needUpdate(userId, id, updateTime)) {
				notionService.updateSeriesRequest(userId, id);
			}
		}
	}
}
