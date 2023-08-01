package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

@AllArgsConstructor
@Builder
public class DivideFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.DIVIDER);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		return "---\n";
	}
}
