import {useState, useEffect, useRef} from "react";
import {Col, Row, Space, Spin, Typography} from "antd";
import {CloseOutlined} from "@ant-design/icons";
import Link from "next/link";
import styled from "styled-components";
import {faker} from "@faker-js/faker";

import PostCard from "@/components/post/PostCard";
import {loadPostAPI} from "@/apis/post";
import useInfiniteScroll from "@/hooks/useInfiniteScroll";

const StyledContentRow = styled(Row)`
	max-width: 1760px;
`;

const StyledSpin = styled(Spin)`
	margin: 64px 0;
`;

const StyledCloseOutlined = styled(CloseOutlined)`
	font-size: 3rem;
`;

const StyledText = styled(Typography.Text)`
	font-size: 2rem;
`;

export default function MainPosts() {
	const id = useRef(999999);
	const [mainPosts, setMainPosts] = useState(Array(40).fill(null)
		.map(() => ({
			"id": id.current++,
			"thumbnail": faker.image.url(),
			"title": faker.word.verb(10),
			"description": faker.lorem.paragraph(),
			"content": faker.lorem.paragraph(),
			"createdAt": faker.date.anytime(),
			"countOfComments": faker.number.int(50),
			"author": faker.person.firstName(),
			"likes": faker.number.int(1000),
			"avatar": faker.image.avatar(),
		})));
	const [isLoading, setIsLoading] = useState(false);
	const [errorMsg, setErrorMsg] = useState("");
	const target = useRef(null);

	const {count} = useInfiniteScroll({
		target,
		targetArray: mainPosts,
		threshold: 0.2,
		endPoint: 3,
	});

	const fetchPosts = async () => {
		setIsLoading(true);
		try {
			const posts = await loadPostAPI(count);

			setMainPosts([...mainPosts, ...posts]);
		} catch (e) {
			setErrorMsg(e.message);
		} finally {
			setIsLoading(false);
		}
	};

	useEffect(() => {
		fetchPosts();
	}, [count]);

	return (
		<Space align="center" direction="vertical">
			<StyledContentRow gutter={[32, 32]} justify="center" ref={target}>
				{mainPosts.map(post => (
					<Link key={post.postId} href={`/${post.author}/${post.postId}`}>
						<Col key={post.postId}>
							<PostCard key={post.postId} post={post} />
						</Col>
					</Link>
				))}
			</StyledContentRow>
			{isLoading && <StyledSpin size="large" tip="Loading" />}
			{errorMsg &&
				<Space align="center" direction="vertical">
					<StyledCloseOutlined />
					<StyledText>{errorMsg}</StyledText>
				</Space>
			}
		</Space>
	);
}
