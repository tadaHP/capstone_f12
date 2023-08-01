package io.f12.notionlinkedblog.service.notion.converter.contents;

import io.f12.notionlinkedblog.service.notion.converter.contents.type.NotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import notion.api.v1.model.common.RichTextColor;
import notion.api.v1.model.pages.PageProperty;

@AllArgsConstructor
@Builder
@Getter
public class CheckAnnotations {
	private Boolean bold; //**
	private Boolean italic;  //*
	private Boolean strikethrough; //~~
	private Boolean underline; // <u> </u>
	private Boolean code; //` `
	private RichTextColor color; //TODO: notYet
	private Boolean equation;

	// PageProperty.RichText.Annotations annotations
	public CheckAnnotations(PageProperty.RichText richText) {
		this.bold = richText.getAnnotations().getBold();
		this.italic = richText.getAnnotations().getItalic();
		this.strikethrough = richText.getAnnotations().getStrikethrough();
		this.underline = richText.getAnnotations().getUnderline();
		this.code = richText.getAnnotations().getCode();
		this.color = richText.getAnnotations().getColor();
		this.equation = richText.getType().toString() == NotionType.BlockType.EQUATION;
	}

	public String applyAnnotations(PageProperty.RichText text) {
		String returnText = text.getPlainText();
		StringBuilder stringBuilder = new StringBuilder(returnText);
		if (returnText.isBlank()) {
			return returnText;
		}
		applyGrammar(stringBuilder);
		applyColor(stringBuilder);
		return stringBuilder.toString();
	}

	private void applyColor(StringBuilder stringBuilder) {
		String value = color.getValue();
		if (value == "default") {
			return;
		}
		if (value.contains("_")) { //backGround Color
			String[] split = value.split("_");
			String coverStart = "<span style=\"background-color:" + split[0] + "\">";
			stringBuilder.insert(0, coverStart);
			stringBuilder.append("</span>");
		} else { //color
			String coverStart = "<span style=\"color:" + value + "\">";
			stringBuilder.insert(0, coverStart);
			stringBuilder.append("</span>");
		}

	}

	private void applyGrammar(StringBuilder stringBuilder) {
		if (code) {
			stringBuilder.insert(0, "`");
			stringBuilder.append("`");
		}
		if (bold) {
			stringBuilder.insert(0, "**");
			stringBuilder.append("**");
		}
		if (italic) {
			stringBuilder.insert(0, "*");
			stringBuilder.append("*");
		}
		if (strikethrough) {
			stringBuilder.insert(0, "~~");
			stringBuilder.append("~~");
		}
		if (underline) {
			stringBuilder.insert(0, "<u>");
			stringBuilder.append("</u>");
		}
		if (equation) {
			stringBuilder.insert(0, "$");
			stringBuilder.append("$");
		}
	}
}
