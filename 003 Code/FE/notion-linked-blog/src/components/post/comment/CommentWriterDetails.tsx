import {Space, Typography} from "antd";
import styled from "styled-components";

const StyledSpace = styled(Space)`
	margin-bottom: 1.5rem;
`;

const CommentWriterAvatar = styled.img`
	width: 54px;
	height: 54px;

	@media screen and (max-width: 768px) {
		width: 40px;
		height: 40px;
	}
`;

const CommentWritingInfo = styled.div`
	display: flex;
	flex-direction: column;
`;

const CommentWriter = styled(Typography.Text)`
	font-size: 1rem;
	font-weight: bold;

	@media screen and (max-width: 768px) {
		font-size: 0.875rem;
	}
`;

export default function CommentWriterDetails() {
	return (
		<StyledSpace align="start">
			<CommentWriterAvatar />
			<CommentWritingInfo>
				<CommentWriter>댓글작성자</CommentWriter>
				<Typography.Text>댓글작성일</Typography.Text>
			</CommentWritingInfo>
		</StyledSpace>
	);
}
