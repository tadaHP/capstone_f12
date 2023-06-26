import {Typography} from "antd";
import {PlusSquareOutlined} from "@ant-design/icons";
import styled from "styled-components";
import CommentWriterDetails from "./CommentWriterDetails";

const Container = styled.div`
	border-top: 0.8px solid rgb(205, 205, 205);
	padding: 1.5rem;

	@media screen and (max-width: 768px) {
		padding: 1rem;
	}
`;

const StyledParagraph = styled(Typography.Paragraph)`
	font-size: 1.125rem;

	@media screen and (max-width: 768px) {
		font-size: 1rem;
	}
`;

const StyledText = styled(Typography.Text)`
	font-size: 1rem;

	@media screen and (max-width: 768px) {
		font-size: 0.875rem;
	}

	:hover {
		cursor: pointer;
	}
`;

export default function Comment() {
	return (
		<Container>
			<CommentWriterDetails />
			<StyledParagraph>
				댓글 내용입니다.
			</StyledParagraph>
			<StyledText strong><PlusSquareOutlined /> 답글 달기</StyledText>
		</Container>
	);
}
