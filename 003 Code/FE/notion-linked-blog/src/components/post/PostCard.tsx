import {Card, Typography} from "antd";
import styled from "styled-components";
import {HeartFilled} from "@ant-design/icons";
import convertKRTimeStyle from "@/utils/time";
import {useEffect, useState} from "react";
import {getThumbnailAPI} from "@/apis/post";
import Image from "next/image";

const {Meta} = Card;

const StyledCard = styled(Card)`
  width: 320px;
  min-width: 320px;
  height: 100%;
`;

const StyledDiv = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const StyledAvatar = styled(Image)`
  border-radius: 50%;
`;

const StyledParagraph = styled(Typography.Paragraph)`
  height: 66px;
`;

const StyledMeta = styled(Meta)`
  width: 100%;
`;

export default function PostCard({post}) {
	const [thumbnail, setThumbnail] = useState("");

	useEffect(() => {
		async function getThumbnail() {
			if (post.requestThumbnailLink) {
				const resp = await getThumbnailAPI(post.requestThumbnailLink);

				setThumbnail(URL.createObjectURL(resp.data));
			}
		}

		getThumbnail();
	}, []);

	return (
		<StyledCard
			hoverable
			bordered={false}
			cover={
				<Image
					src={thumbnail}
					alt="CardCover"
					width={170}
					height={170}
				/>}
			actions={
				[
					<StyledDiv key="left">
						{post.avater && <StyledAvatar
							src={post.avatar}
							alt="PostAuthorAvatar"
							width={15}
							height={15}
						/>}
						&nbsp;by
						<Typography.Text strong={true}>&nbsp;{post.author}</Typography.Text>
						{/* eslint-disable-next-line array-element-newline */
						}
					</StyledDiv>,
					<Typography.Text key="right">
						<HeartFilled />
						&nbsp;{post.likes}
					</Typography.Text>,
				]
			}
		>
			<
				StyledMeta
				title={post.title}
				description={(
					<>
						<StyledParagraph
							ellipsis={{rows: 3}}>{post.description}</StyledParagraph>
						<Typography.Text>{convertKRTimeStyle(post.createdAt)}</Typography.Text>
						<Typography.Text> · </Typography.Text>
						<Typography.Text>{post.countOfComments}개의 댓글</Typography.Text>
					</>
				)
				}
			/>
		</StyledCard>
	);
}
