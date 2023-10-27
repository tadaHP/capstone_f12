import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import Link from "next/link";
import {Avatar, List, Space, Typography} from "antd";
import styled from "styled-components";
import {RootState} from "@/redux/store";
import {getMySeries, getProfileImageAPI} from "@/apis/user";
import AppLayout from "@/components/common/AppLayout";
import {modifyProfileImage} from "@/redux/userSlice";
import convertKRTimeStyle from "@/utils/time";
import Navigation from "../navigation";

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

export default function Series() {
	const id = useSelector<RootState, number>(state => state.user.user?.id);
	const profile = useSelector<RootState, string>(state => state.user.user?.profile);
	const username = useSelector<RootState, string>(state => state.user.user?.username);
	const introduction = useSelector<RootState, string>(state => state.user.user?.introduction);
	const [series, setSeries] = useState([]);
	const dispatch = useDispatch();

	useEffect(() => {
		const fetchProfileImage = async () => {
			const {imageUrl} = await getProfileImageAPI(id);

			dispatch(modifyProfileImage(imageUrl));
		};

		const fetchMySeries = async () => {
			const mySeries = await getMySeries(id);

			mySeries.data.forEach(item => {
				item.createdAt = convertKRTimeStyle(item.createdAt);
			});
			setSeries(mySeries.data);
		};

		if (id) {
			fetchProfileImage();
			fetchMySeries();
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
				<Navigation username={username} selected="시리즈" />
				<PostSpace>
					<List
						itemLayout="vertical"
						size="large"
						dataSource={series}
						renderItem={item => (
							<List.Item key={item.seriesId}>
								<List.Item.Meta
									title={<Link href={`/${username}/series/${item.seriesId}`}>{item.title}</Link>}
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
