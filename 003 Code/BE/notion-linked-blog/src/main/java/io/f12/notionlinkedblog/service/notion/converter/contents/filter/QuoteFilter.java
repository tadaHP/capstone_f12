package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.util.List;

import org.springframework.util.StringUtils;

import io.f12.notionlinkedblog.service.notion.converter.contents.CheckAnnotations;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@Builder
public class QuoteFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.QUOTE);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		List<PageProperty.RichText> texts = block.asQuote().getQuote().getRichText();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("> ");

		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		String replace = StringUtils.replace(stringBuilder.toString(), "\n", "</br>");
		return replace + "\n\n";
	}
}
