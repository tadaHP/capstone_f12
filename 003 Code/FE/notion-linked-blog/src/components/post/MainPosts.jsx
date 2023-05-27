import {Col, Row} from "antd";
import PostCard from "@/components/post/PostCard";
import {useAppSelector} from "@/hooks/hooks";
import styled from "styled-components";

const StyledContentRow = styled(Row)`
	max-width: 1760px;
`;

export default function MainPosts() {
	const {mainPosts} = useAppSelector(state => state.post);

	return (
		<StyledContentRow gutter={[32, 32]} justify="center">
			{mainPosts.map(post => (
				<Col key={post.id}>
					<PostCard key={post.id} post={post} />
				</Col>
			))}
		</StyledContentRow>
	);
}
