package io.f12.notionlinkedblog.service.notion;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.f12.notionlinkedblog.domain.notion.SyncedPages;
import io.f12.notionlinkedblog.exceptions.exception.NotionAuthenticationException;
import io.f12.notionlinkedblog.repository.syncedpages.SyncedPagesDataRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNotionSchedule {

	private final SyncedPagesDataRepository syncedPagesDataRepository;
	private final NotionService notionService;

	@Scheduled(fixedDelay = 3600000) // 1시간 (임시 설정)
	public void updateNotionData() throws NotionAuthenticationException {
		List<SyncedPages> everyData = syncedPagesDataRepository.findAll();

		for (SyncedPages data : everyData) {
			Long userId = data.getUser().getId();
			String pageId = data.getPageId();
			LocalDateTime updateTime = data.getPost().getUpdatedAt();
			if (notionService.needUpdate(userId, pageId, updateTime)) {
				notionService.editNotionPageToBlog(userId, data.getPost());
			}
		}

	}
}
