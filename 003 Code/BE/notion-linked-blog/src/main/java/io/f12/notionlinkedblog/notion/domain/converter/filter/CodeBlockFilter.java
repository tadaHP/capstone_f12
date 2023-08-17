package io.f12.notionlinkedblog.notion.domain.converter.filter;

import java.util.List;

import io.f12.notionlinkedblog.notion.domain.converter.CheckAnnotations;
import io.f12.notionlinkedblog.notion.domain.converter.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.CodeBlock;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@Builder
public class CodeBlockFilter implements NotionFilter {

	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.CODE_BLOCK);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		CodeBlock.Element codeBlock = block.asCode().getCode();
		List<PageProperty.RichText> texts = codeBlock.getRichText();
		String codeLanguage = codeBlock.getLanguage();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("```").append(codeLanguage).append("\n");

		for (PageProperty.RichText text : texts) {
			CheckAnnotations letterShape = new CheckAnnotations(text);
			stringBuilder.append(letterShape.applyAnnotations(text));
		}

		stringBuilder.append("\n").append("```");

		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
}
