package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

public class ChildPageFilter implements NotionFilter {
	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.CHILD_PAGE);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		//ChildBlock is Not Support
		return "";
	}
}
