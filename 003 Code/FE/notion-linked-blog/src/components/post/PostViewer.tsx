import dynamic from "next/dynamic";

import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import styled from "styled-components";
import {Button, Modal} from "antd";
import {useState} from "react";
import {requestDeletePostAPI} from "@/apis/post";
import {useRouter} from "next/router";
import {useSelector} from "react-redux";
import Link from "next/link";
import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";

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

const StyledButtonDiv = styled.div`
	& > Button {
		margin: 10px;
	}
`;

export default function PostViewer({post}) {
	const router = useRouter();
	const [isModalOpen, setIsModalOpen] = useState(false);
	const {user} = useSelector<RootState, UserState>(state => state.user);

	const showModal = () => {
		setIsModalOpen(true);
	};
	const handleOk = async () => {
		setIsModalOpen(false);
		try {
			await requestDeletePostAPI(post.postId);
			await router.replace("/");
		} catch (e) {
			console.log("포스트 삭제 요청 관련 에러", e);
		}
	};

	const handleCancel = () => {
		setIsModalOpen(false);
	};

	return (
		<StyledContainer data-color-mode="light">
			<StyledTop>
				<StyledTitle>{post.title}</StyledTitle>
				<StyledSubTitle>{post.author} · {post.createdAt}</StyledSubTitle>
			</StyledTop>
			<div className="wmde-markdown-var" />
			<StyledMDPreview source={post.content} height="100%" />
			{user?.username === post.author &&
				<StyledButtonDiv>
					<Link href={{
						pathname: `/write/${post.postId}`,
						query: {
							postId: post.postId,
							title: post.title,
							content: post.content,
							author: post.author,
						},
					}}
					as={`/write/${post.postId}`}>
						<Button type="primary">
							수정하기
						</Button>
					</Link>
					<Button type="primary" onClick={showModal}>
						삭제하기
					</Button>
					<Modal title="포스트 삭제" open={isModalOpen} onOk={handleOk} onCancel={handleCancel} okText="확인" cancelText="취소">
						<p>포스트를 정말 삭제하시겠습니까?</p>
					</Modal>
				</StyledButtonDiv>
			}
		</StyledContainer>
	);
}
