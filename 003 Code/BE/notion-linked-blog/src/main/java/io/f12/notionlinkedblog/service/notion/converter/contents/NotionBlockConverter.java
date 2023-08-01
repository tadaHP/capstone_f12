package io.f12.notionlinkedblog.service.notion.converter.contents;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.f12.notionlinkedblog.service.notion.converter.contents.filter.BookmarkFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.BulletedListItemFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.CallOutBlockFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.ChildPageFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.CodeBlockFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.DivideFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.FileFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.HeadingOneFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.HeadingThreeFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.HeadingTwoFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.ImageFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.LinkPreviewFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.NotionFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.NumberedListItemFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.ParagraphFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.PdfFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.QuoteFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.SyncedBlockFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.TableFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.ToDoFilter;
import io.f12.notionlinkedblog.service.notion.converter.contents.filter.ToggleBlockFilter;
import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

@Slf4j
@Component
public class NotionBlockConverter {

	private final List<NotionFilter> filterList = new ArrayList<>();

	public NotionBlockConverter() {
		filterList.add(new ParagraphFilter());
		filterList.add(new HeadingOneFilter());
		filterList.add(new HeadingTwoFilter());
		filterList.add(new HeadingThreeFilter());
		filterList.add(new BulletedListItemFilter());
		filterList.add(new NumberedListItemFilter());
		filterList.add(new QuoteFilter());
		filterList.add(new TableFilter());
		filterList.add(new ToDoFilter());
		filterList.add(new ToggleBlockFilter());
		filterList.add(new BookmarkFilter());
		filterList.add(new CodeBlockFilter());
		filterList.add(new DivideFilter());
		filterList.add(new CallOutBlockFilter());
		filterList.add(new ChildPageFilter());
		filterList.add(new ImageFilter());
		filterList.add(new FileFilter());
		filterList.add(new LinkPreviewFilter());
		filterList.add(new PdfFilter());
		filterList.add(new SyncedBlockFilter());
	}

	public NotionBlockConverter(Integer deep) {
		filterList.add(new ParagraphFilter(deep));
		filterList.add(new HeadingOneFilter());
		filterList.add(new HeadingTwoFilter());
		filterList.add(new HeadingThreeFilter());
		filterList.add(new BulletedListItemFilter(deep));
		filterList.add(new NumberedListItemFilter(deep));
		filterList.add(new QuoteFilter());
		filterList.add(new TableFilter());
		filterList.add(new ToDoFilter(deep));
		filterList.add(new ToggleBlockFilter());
		filterList.add(new BookmarkFilter());
		filterList.add(new CodeBlockFilter());
		filterList.add(new DivideFilter());
		filterList.add(new CallOutBlockFilter());
		filterList.add(new ChildPageFilter());
		filterList.add(new ImageFilter());
		filterList.add(new FileFilter());
		filterList.add(new LinkPreviewFilter());
		filterList.add(new PdfFilter());
		filterList.add(new SyncedBlockFilter());
	}

	public String doFilter(Block block, NotionClient client) {
		String result = null;

		for (NotionFilter filter : filterList) {
			if (filter.isAcceptable(block)) {
				result = filter.doFilter(block, client);
				break;
			}
		}
		if (result == null) {
			return "";
		}
		return result;
	}
}
