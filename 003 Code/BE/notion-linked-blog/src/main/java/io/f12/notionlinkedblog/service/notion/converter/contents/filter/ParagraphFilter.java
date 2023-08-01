package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.util.List;

import io.f12.notionlinkedblog.service.notion.converter.contents.CheckAnnotations;
import io.f12.notionlinkedblog.service.notion.converter.contents.ChildrenConverter;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.ParagraphBlock;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParagraphFilter implements NotionFilter {

	private Integer deep;

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.PARAGRAPH);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		ParagraphBlock paragraphBlock = block.asParagraph();
		List<PageProperty.RichText> texts = paragraphBlock.getParagraph().getRichText();
		StringBuilder stringBuilder = new StringBuilder();

		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		stringBuilder.append("  ").append("\n\n");
		stringBuilder.append(getChildren(client, paragraphBlock));
		String substring = stringBuilder.substring(stringBuilder.length() - 1, stringBuilder.length());
		if (substring.equals("\n")) {
			return stringBuilder.toString();
		}
		return stringBuilder + "\n";
	}

	private String getChildren(NotionClient client, ParagraphBlock paragraphBlock) {
		StringBuilder stringBuilder = new StringBuilder();
		if (paragraphBlock.getHasChildren()) {
			if (deep == null) {
				ChildrenConverter childrenConverter = new ChildrenConverter(paragraphBlock.getId(), client, 1);
				stringBuilder.append(childrenConverter.getParagraphChildren());
			} else {
				ChildrenConverter childrenConverter = new ChildrenConverter(paragraphBlock.getId(), client, deep + 1);
				stringBuilder.append(childrenConverter.getParagraphChildren());
			}
		}
		return stringBuilder.toString();
	}

}
