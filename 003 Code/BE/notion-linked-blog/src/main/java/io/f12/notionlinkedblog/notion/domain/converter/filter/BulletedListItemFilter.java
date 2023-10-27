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
import notion.api.v1.model.blocks.BulletedListItemBlock;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulletedListItemFilter implements NotionFilter {
	private Integer deep;

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.BULLETED_LIST_ITEM);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		BulletedListItemBlock bulletedListItem = block.asBulletedListItem();
		List<PageProperty.RichText> texts = block.asBulletedListItem().getBulletedListItem().getRichText();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("- ");
		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}
		stringBuilder.append("  ").append("\n");
		stringBuilder.append(getChildren(client, bulletedListItem));
		return stringBuilder.toString();
	}

	private String getChildren(NotionClient client, BulletedListItemBlock bulletedListItem) {
		StringBuilder stringBuilder = new StringBuilder();
		if (bulletedListItem.getHasChildren()) {
			if (deep == null) {
				ChildrenConverter childrenConverter = new ChildrenConverter(bulletedListItem.getId(), client, 1);
				stringBuilder.append(childrenConverter.getCommonChildren());
			} else {
				ChildrenConverter childrenConverter = new ChildrenConverter(bulletedListItem.getId(), client, deep + 1);
				stringBuilder.append(childrenConverter.getCommonChildren());
			}
		}
		return stringBuilder.toString();
	}
}
