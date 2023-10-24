import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import Image from "next/image";
import {useRouter} from "next/router";
import Link from "next/link";
import {Avatar, List, Space, Typography} from "antd";
import {LikeOutlined, MessageOutlined} from "@ant-design/icons";
import styled from "styled-components";
import {RootState} from "@/redux/store";
import {getMyPosts, getProfileImageAPI} from "@/apis/user";
import AppLayout from "@/components/common/AppLayout";
import {modifyProfileImage} from "@/redux/userSlice";
import convertKRTimeStyle from "@/utils/time";

const MyInfoSpace = styled(Space)`
	width: 768px;
	margin: 0 42.8px;
	margin-top: 48px;
	padding-bottom: 24px;
	border-bottom: 1px solid lightgray;

	@media screen and (max-width: 768px) {
		flex-direction: column;
		width: 100%;
	}
`;

const PostSpace = styled(Space)`
	width: 768px;
	margin: 0 42.8px;

	> div {
		width: 100%;
	}

	@media screen and (max-width: 768px) {
		flex-direction: column;
		width: 100%;
	}
`;

const StyledAvatar = styled(Avatar)`
  width: 128px;
  height: 128px;

  @media screen and (max-width: 768px) {
    width: 96px;
    height: 96px;
  }
`;

const StyledUsername = styled(Typography.Text)`
	font-size: 1.5rem;
	line-height: 1.5;
	font-weight: bold;
	white-space: pre-wrap;

  @media screen and (max-width: 768px) {
    font-size: 1.125rem;
  }
`;

const StyledIntroduction = styled(Typography.Text)`
	font-size: 1.125rem;
	line-height: 1.5;
	margin-top: 0.25rem;

  @media screen and (max-width: 768px) {
    font-size: 0.875rem;
  }
`;

const IconText = ({icon, text}: {icon: React.FC; text: string;}) => (
	<Space>
		{React.createElement(icon)}
		{text}
	</Space>
);

export default function MyPage() {
	const id = useSelector<RootState, number>(state => state.user.user?.id);
	const profile = useSelector<RootState, string>(state => state.user.user?.profile);
	const username = useSelector<RootState, string>(state => state.user.user?.username);
	const introduction = useSelector<RootState, string>(state => state.user.user?.introduction);
	const [posts, setPosts] = useState([]);
	const dispatch = useDispatch();
	const router = useRouter();

	useEffect(() => {
		const fetchProfileImage = async () => {
			const {imageUrl} = await getProfileImageAPI(id);

			dispatch(modifyProfileImage(imageUrl));
		};

		const fetchMyPosts = async () => {
			const myPosts = await getMyPosts(id);

			myPosts.data.forEach(post => {
				post.createdAt = convertKRTimeStyle(post.createdAt);
			});
			setPosts(myPosts.data);
		};

		if (id) {
			fetchProfileImage();
			fetchMyPosts();
		}
	}, [id]);

	return (
		<AppLayout>
			<Space direction="vertical">
				<MyInfoSpace>
					<StyledAvatar src={profile} />
					<Space direction="vertical">
						<StyledUsername>
							{username}
						</StyledUsername>
						<StyledIntroduction>
							{introduction}
						</StyledIntroduction>
					</Space>
				</MyInfoSpace>
				<PostSpace>
					<List
						itemLayout="vertical"
						size="large"
						dataSource={posts}
						renderItem={item => (
							<List.Item
								key={item.id}
								actions={[
									<Space>{item.createdAt}</Space>,
									<IconText icon={LikeOutlined} text={item.likes} key="list-vertical-like-o" />,
									<IconText icon={MessageOutlined} text={item.countOfComments} key="list-vertical-message" />,
								]}
							>
								<div style={{
									position: "relative",
									width: "100%",
									maxWidth: "702px",
									aspectRatio: "16/10",
								}}>
									<Image
										src={item.requestThumbnailLink}
										fill
										priority
										alt="logo"
										onClick={() => router.replace(`${item.author}/${item.postId}`)}
										style={{cursor: "pointer"}} />
								</div>
								<List.Item.Meta
									title={<Link href={`${item.author}/${item.postId}`}>{item.title}</Link>}
									description={item.description}
								/>
							</List.Item>
						)}
					>
					</List>
				</PostSpace>
			</Space>
		</AppLayout >
	);
}
