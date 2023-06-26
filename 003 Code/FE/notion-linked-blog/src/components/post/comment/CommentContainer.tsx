import {Typography} from "antd";
import styled from "styled-components";

import CommentForm from "./CommentForm";
import Comment from "./Comment";

const StyledSection = styled.section`
	display: flex;
	flex-direction: column;
	justify-content: center;
	width: 768px;

	@media screen and (max-width: 768px) {
		width: 100vw;
		padding: 0 1rem;
	}
`;

export default function CommentContainer() {
	return (
		<StyledSection>
			<Typography.Title level={4}>4개의 댓글</Typography.Title>
			<CommentForm />
			<Comment />
			<Comment />
			<Comment />
			<Comment />
		</StyledSection>
	);
}
