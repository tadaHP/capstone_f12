package io.f12.notionlinkedblog.notion.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.common.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.notion.api.port.NotionService;
import io.f12.notionlinkedblog.notion.infrastructure.SyncedPagesEntity;
import io.f12.notionlinkedblog.notion.service.port.SyncedPagesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNotionSchedule {

	private final SyncedPagesRepository syncedPagesRepository;
	private final NotionService notionService;

	@Scheduled(fixedDelay = 3600000) // 1시간 (임시 설정)
	public void updateNotionData() throws NotionAuthenticationException {
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
}
