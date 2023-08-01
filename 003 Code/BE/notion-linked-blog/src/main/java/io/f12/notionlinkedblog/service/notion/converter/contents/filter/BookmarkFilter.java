package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.util.List;

import io.f12.notionlinkedblog.service.notion.converter.contents.CheckAnnotations;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@Builder
@Slf4j
public class BookmarkFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.BOOKMARK);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		String url = block.asBookmark().getBookmark().getUrl();
		List<PageProperty.RichText> caption = block.asBookmark().getBookmark().getCaption();

		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder captionStringBuilder = new StringBuilder();

		for (PageProperty.RichText text : caption) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			captionStringBuilder.append(letterShape.applyAnnotations(text));
		}
		if (captionStringBuilder.toString().equals("")) {
			captionStringBuilder.append("bookmark");
		}

		stringBuilder.append("[").append(captionStringBuilder).append("]");
		stringBuilder.append("(").append(url).append(")");

		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
}
