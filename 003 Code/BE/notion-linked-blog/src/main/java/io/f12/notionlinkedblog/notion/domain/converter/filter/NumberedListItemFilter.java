package io.f12.notionlinkedblog.notion.domain.converter.filter;

import java.util.List;

import io.f12.notionlinkedblog.notion.domain.converter.CheckAnnotations;
import io.f12.notionlinkedblog.notion.domain.converter.ChildrenConverter;
import io.f12.notionlinkedblog.notion.domain.converter.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.NumberedListItemBlock;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NumberedListItemFilter implements NotionFilter {
	private Integer deep;

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.NUMBERED_LIST_ITEM);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		NumberedListItemBlock numberedListItem = block.asNumberedListItem();
		List<PageProperty.RichText> texts = numberedListItem.getNumberedListItem().getRichText();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("1. ");
		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		stringBuilder.append("  ").append("\n");
		stringBuilder.append(getChildren(client, numberedListItem));
		String substring = stringBuilder.substring(stringBuilder.length() - 1, stringBuilder.length());
		if (substring.equals("\n")) {
			return stringBuilder.toString();
		}
		return stringBuilder + "\n";
	}

	private String getChildren(NotionClient client, NumberedListItemBlock numberedListItem) {
		StringBuilder stringBuilder = new StringBuilder();
		if (numberedListItem.getHasChildren()) {
			if (deep == null) {
				ChildrenConverter childrenConverter = new ChildrenConverter(numberedListItem.getId(), client, 1);
				stringBuilder.append(childrenConverter.getCommonChildren());
			} else {
				ChildrenConverter childrenConverter = new ChildrenConverter(numberedListItem.getId(), client, deep + 1);
				stringBuilder.append(childrenConverter.getCommonChildren());
			}
		}
		return stringBuilder.toString();
	}

}
