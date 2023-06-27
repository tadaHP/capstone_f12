import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import React, {useEffect, useState} from "react";
import MDEditor, {
	bold,
	code,
	codeBlock,
	divider,
	image,
	italic,
	link,
	quote,
	strikethrough,
	title1,
	title2,
	title3,
	title4,
} from "@uiw/react-md-editor";
import styled from "styled-components";

const StyledMDEditor = styled(MDEditor)`
  .w-md-editor-toolbar > ul > li > button {
    height: 30px;
  }

  .w-md-editor-toolbar > ul > li > button > svg {
    width: 30px;
    height: 30px;
  }

  .w-md-editor-toolbar > ul > li > button > div {
    font-size: 30px !important;
  }

  display: inline-block;
  width: 100%;
  padding-bottom: 50px;
`;

const EditorCover = styled.div`
  display: flex;
`;


const commands = [
	title1,	title2,	title3,	title4,	divider, code, bold,
	italic, strikethrough, divider, quote, link, image, codeBlock,
];

const PostWrite = props => {
	const [content, setContent] = useState(props.content);

	useEffect(() => {
		props.editContent(content);
	}, [content]);

	return (
		<EditorCover>
			<StyledMDEditor
				value={content}
				onChange={setContent}
				commands={commands}
				extraCommands={[null]}
				preview="live"
				// height %로 설정할 경우 드래그바 사라짐
				height={window.innerHeight - 250}
			/>
		</EditorCover>
	);
};

export default PostWrite;
