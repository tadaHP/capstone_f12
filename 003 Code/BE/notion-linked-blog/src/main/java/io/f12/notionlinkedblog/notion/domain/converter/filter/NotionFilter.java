package io.f12.notionlinkedblog.notion.domain.converter.filter;

import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

public interface NotionFilter {
	boolean isAcceptable(Block block);

	String doFilter(Block block, NotionClient client);
}
