package io.f12.notionlinkedblog.service.notion.converter.contents.filter;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import io.f12.notionlinkedblog.api.common.Endpoint;
import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

public class ImageFilter implements NotionFilter {
	@Override
	public boolean isAcceptable(Block block) {
		return block.getType().getValue().equals(NotionType.BlockType.IMAGE);
	}

	@Override
	public String doFilter(Block block, NotionClient client) {
		String type = block.asImage().getImage().getType();
		String systemPath = System.getProperty("user.dir");
		File file = null;
		String urlString = null;
		String imageName = null;
		if (type.equals("file")) {
			urlString = block.asImage().getImage().getFile().getUrl();
			imageName = urlToImageName(urlString);

			try {
				file = new File(systemPath + "\\" + imageName);
				FileUtils.copyURLToFile(new URL(urlString), file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			urlString = block.asImage().getImage().getExternal().getUrl();
		}
		return "![]" + "(" + Endpoint.Local.LOCAL_ADDRESS + Endpoint.Api.REQUEST_IMAGE + "/" + imageName
			+ ")\n\n";
	}

	private String urlToImageName(String urlString) {
		String[] split1 = urlString.split("/");
		String second = split1[split1.length - 1];

		String[] split2 = second.split("\\?");
		return split2[0];
	}
}
