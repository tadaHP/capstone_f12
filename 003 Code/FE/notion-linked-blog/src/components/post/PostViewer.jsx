import dynamic from "next/dynamic";

import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import {styled} from "styled-components";

const MDPreview = dynamic(
	() => import("@uiw/react-markdown-preview").then(mod => mod.default),
	{ssr: false},
);

const StyledMDPreview = styled(MDPreview)`
	background-color: transparent !important;
	margin-top: 80px;
`;

const StyledContainer = styled.div`
	width: 768px;

	@media screen and (max-width: 768px) {
		width: 100vw;
		padding: 0 16px;
	}

	margin: 0 567.5px;
	margin-top: 88px;
	margin-bottom: 64px;
`;

const StyledTop = styled.div`
`;

const StyledTitle = styled.div`
	font-size: 3rem;
`;

const StyledSubTitle = styled.div`
`;

export default function PostViewer({post}) {
	return (
		<StyledContainer data-color-mode="light" style={{

		}}>
			<StyledTop>
				<StyledTitle>{post.title}</StyledTitle>
				<StyledSubTitle>{post.author} · {post.createdAt}</StyledSubTitle>
			</StyledTop>
			<div className="wmde-markdown-var" />
			<StyledMDPreview source={post.content} height="100%" />
		</StyledContainer>
	);
}
