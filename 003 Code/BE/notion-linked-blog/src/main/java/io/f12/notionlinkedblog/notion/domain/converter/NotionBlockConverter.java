package io.f12.notionlinkedblog.notion.domain.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.f12.notionlinkedblog.notion.domain.converter.filter.ImageFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.LinkPreviewFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.NotionFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.NumberedListItemFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.ParagraphFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.PdfFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.QuoteFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.SyncedBlockFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.TableFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.ToDoFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.ToggleBlockFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.BookmarkFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.BulletedListItemFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.CallOutBlockFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.ChildPageFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.CodeBlockFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.DivideFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.FileFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.HeadingOneFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.HeadingThreeFilter;
import io.f12.notionlinkedblog.notion.domain.converter.filter.HeadingTwoFilter;
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
