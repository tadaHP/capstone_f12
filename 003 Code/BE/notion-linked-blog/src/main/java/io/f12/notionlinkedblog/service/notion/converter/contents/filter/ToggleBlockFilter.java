package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.util.List;

import io.f12.notionlinkedblog.service.notion.converter.contents.CheckAnnotations;
import io.f12.notionlinkedblog.service.notion.converter.contents.NotionBlockConverter;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.Blocks;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.request.blocks.RetrieveBlockChildrenRequest;

@AllArgsConstructor
@Builder
public class ToggleBlockFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.TOGGLE_BLOCK);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		String id = block.asToggle().getId();
		List<PageProperty.RichText> texts = block.asToggle().getToggle().getRichText();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<details>\n").append("<summary>");
		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		stringBuilder.append("</summary>\n").append("<div>\n");
		stringBuilder.append("　");
		stringBuilder.append(internalFilter(id, client)).append("\n");
		stringBuilder.append("</div>\n").append("</details>");

		return stringBuilder + "\n\n";
	}

	private String internalFilter(String id, NotionClient client) {
		List<Block> blocks = reRequestContents(id, client);
		NotionBlockConverter converter = new NotionBlockConverter();
		StringBuilder stringBuilder = new StringBuilder();
		for (Block block : blocks) {
			stringBuilder.append(converter.doFilter(block, client));
		}
		return stringBuilder.toString();
	}

	private List<Block> reRequestContents(String id, NotionClient client) {
		Blocks blocks;
		RetrieveBlockChildrenRequest retrieveBlockChildrenRequest
			= new RetrieveBlockChildrenRequest(id);
		try (client) {
			blocks = client.retrieveBlockChildren(retrieveBlockChildrenRequest);
		}
		return blocks.getResults();
	}
}
