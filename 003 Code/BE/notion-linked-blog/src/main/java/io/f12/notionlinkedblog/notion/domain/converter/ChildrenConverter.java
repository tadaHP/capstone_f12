package io.f12.notionlinkedblog.notion.domain.converter;

import java.util.List;

import lombok.AllArgsConstructor;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.request.blocks.RetrieveBlockChildrenRequest;

@AllArgsConstructor
public class ChildrenConverter {
	String id;
	NotionClient client;
	Integer deep;

	public String getCommonChildren() {
		List<Block> results = requestBlocks();
		NotionBlockConverter notionBlockConverter = new NotionBlockConverter(deep);
		StringBuilder stringBuilder = new StringBuilder();

		for (Block block : results) {
			for (int i = 0; i < deep; i++) {
				stringBuilder.append("\t");
			}
			stringBuilder.append(notionBlockConverter.doFilter(block, client));
		}

		return stringBuilder.toString();
	}

	public String getParagraphChildren() {
		List<Block> results = requestBlocks();
		NotionBlockConverter notionBlockConverter = new NotionBlockConverter(deep);
		StringBuilder stringBuilder = new StringBuilder();

		for (Block block : results) {
			for (int i = 0; i < deep; i++) {
				stringBuilder.append("　"); //전각문자가 들어가 있습니다
			}
			stringBuilder.append(notionBlockConverter.doFilter(block, client)).append("\n");
		}
		return stringBuilder.toString();
	}

	private List<Block> requestBlocks() {
		RetrieveBlockChildrenRequest retrieveBlockChildrenRequest = new RetrieveBlockChildrenRequest(id);
		List<Block> results = null;
		try {
			results = client.retrieveBlockChildren(retrieveBlockChildrenRequest).getResults();
		} finally {
			client.close();
		}
		return results;
	}
}
