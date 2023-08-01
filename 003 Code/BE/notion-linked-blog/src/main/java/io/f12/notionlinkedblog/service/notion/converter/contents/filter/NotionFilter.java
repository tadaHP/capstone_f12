package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

public interface NotionFilter {
	boolean isAcceptable(Block block);

	String doFilter(Block block, NotionClient client);
}
