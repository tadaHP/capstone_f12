package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.util.List;

import io.f12.notionlinkedblog.service.notion.converter.contents.CheckAnnotations;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@Builder
public class HeadingOneFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.H1);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		List<PageProperty.RichText> texts = block.asHeadingOne().getHeading1().getRichText();
		StringBuilder stringBuilder = new StringBuilder();
		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		return toHeadingOne(stringBuilder);
	}

	private String toHeadingOne(StringBuilder sb) {
		return "# " + sb.toString() + "\n";
	}
}
