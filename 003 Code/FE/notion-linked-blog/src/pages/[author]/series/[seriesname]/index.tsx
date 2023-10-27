import {deleteSeries, editSeriesTitle, getPostsInSeries} from "@/apis/series";
import {RootState} from "@/redux/store";
import {Button, Form, Input, Modal, Space, Typography} from "antd";
import Image from "next/image";
import {useRouter} from "next/router";
import {useEffect, useState} from "react";
import {useSelector} from "react-redux";
import styled from "styled-components";

const StyledTitle = styled(Typography.Title)`
	text-align: start;
`;

const StyledHr = styled.div`
	height: 1px;
	background: #DEE2E6;
`;

interface PostInfo {
	postId: number;
	postInfo: string;
	postTitle: string;
	thumbnailRequestUrl: string;
}

interface ISeriesInfo {
	authorId: number;
	author: string;
	seriesId: number;
	seriesName: string;
	postsInfo: Array<PostInfo>;
}

const initialSeriesInfo = {
	authorId: -1,
	author: "anonymous",
	seriesId: -1,
	seriesName: "blank",
	postsInfo: [],
};

export default function Series() {
	const userId = useSelector<RootState, number>(state => state.user.user?.id);
	const username = useSelector<RootState, number>(state => state.user.user?.username);
	const [seriesInfo, setSeriesInfo] = useState<ISeriesInfo>(initialSeriesInfo);
	const [isOpenDeletingModal, setIsOpenDeletingModal] = useState(false);
	const [isActiveEditing, setIsActiveEditing] = useState(false);
	const router = useRouter();

	const seriesId = parseInt(router.asPath.substring(router.asPath.lastIndexOf("/") + 1), 10);

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setSeriesInfo(prev => ({
			...prev,
			seriesName: e.target.value,
		}));
	};

	const handleFinish = async ({seriesName}) => {
		await editSeriesTitle(userId, seriesId, seriesName);

		setSeriesInfo(prev => ({
			...prev,
			seriesName,
		}));
		setIsActiveEditing(false);
	};

	const handleOk = async () => {
		await deleteSeries(userId, seriesId);
		setIsOpenDeletingModal(false);
		router.replace(`/${username}/series`);
	};

	const handleCancel = () => {
		setIsOpenDeletingModal(false);
	};

	useEffect(() => {
		async function fetchPosts() {
			const data = await getPostsInSeries(seriesId);

			setSeriesInfo(data);
		}

		if (userId && seriesId) {
			fetchPosts();
		}
	}, [userId, seriesId]);

	return (
		<div style={{display: "flex", justifyContent: "center"}}>
			<div style={{width: 768}}>
				{!isActiveEditing ?
					<StyledTitle level={1}>
						{seriesInfo.seriesName}
					</StyledTitle> :
					<Form
						onFinish={handleFinish}>
						<Form.Item name="seriesName" initialValue={seriesInfo.seriesName}>
							<Input value={seriesInfo.seriesName} onChange={handleChange} style={{fontWeight: 600, fontSize: "2.5rem"}} />
						</Form.Item>
						<Form.Item style={{display: "flex", justifyContent: "flex-end"}}>
							<Button type="primary" htmlType="submit">적용</Button>
						</Form.Item>
					</Form>
				}
				<StyledHr />
				{userId === seriesInfo.authorId && !isActiveEditing && (
					<div style={{display: "flex", justifyContent: "flex-end"}}>
						<Space>
							<Button type="link" onClick={() => setIsActiveEditing(true)}>수정</Button>
							<Button type="link" onClick={() => setIsOpenDeletingModal(true)}>삭제</Button>
							<Modal title="시리즈 삭제" open={isOpenDeletingModal}
								footer={[
									<Button onClick={handleCancel}>취소</Button>,
									<Button key="submit" type="primary" danger onClick={handleOk}>삭제</Button>
								]}>
								<Typography.Paragraph>시리즈를 삭제해도 포스트는 삭제되지 않습니다</Typography.Paragraph>
							</Modal>
						</Space>
					</div>
				)}
				{seriesInfo.postsInfo.map((post, idx) => (
					<div key={post.postId} style={{display: "flex", justifyContent: "center", flexDirection: "column"}}>
						<Typography.Title level={3} style={{cursor: "pointer"}} onClick={() => router.replace(`/${username}/${post.postId}`)}>{idx + 1}. {post.postTitle}</Typography.Title>
						<Space align="start">
							<Image
								src={post.thumbnailRequestUrl}
								onClick={() => router.replace(`/${username}/${post.postId}`)}
								style={{cursor: "pointer"}}
								width={192}
								height={100}
								alt="PostThumbnail" />
							<Typography.Paragraph>{post.postInfo}</Typography.Paragraph>
						</Space>
					</div>
				))}
			</div>
		</div >
	);
}
