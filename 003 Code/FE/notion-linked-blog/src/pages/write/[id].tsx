import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import React, {useState} from "react";
import dynamic from "next/dynamic";
import handleInput from "@/components/auth/common";
import PostWriteSetting from "@/components/post/PostWriteSetting";
import {Button} from "antd";
import Link from "next/link";
import {
	ButtonSpace, StyledInput,
	WriteDiv,
} from "@/components/post/Post";
import {useRouter} from "next/router";
import {requestUpdatePostAPI} from "@/apis/post";
import {styled} from "styled-components";
import {useSelector} from "react-redux";
import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";

const PostEditor = dynamic(
	() => import("@/components/post/PostWrite"),
	{ssr: false},
);

const Write = () => {
	const router = useRouter();
	const post = router.query;
	const [title, onChangeTitle] = handleInput(post.title);
	const [content, setContent] = useState(post.content);
	const [isDoneWrite, setIsDoneWrite] = useState(false);
	const {user} = useSelector<RootState, UserState>(state => state.user);

	const StyledButtonDiv = styled.div`
		& > Button {
			margin: 10px;
		}
	`;

	const StyledWrongDiv = styled.div`
		display: flex;
		height: 100%;
    justify-content: center;
    align-items: center;
		font-size: 3rem;
	`;

	const isDoneWritePost = () => {
		setIsDoneWrite(prev => !prev);
	};

	const editContent = contents => {
		setContent(contents);
	};

	const updatePost = async () => {
		const postData = {
			postId: post.postId,
			title,
			content,
		};

		try {
			await requestUpdatePostAPI(postData);
			await router.replace("/");
		} catch (e) {
			console.log("업데이트 에러", e);
		}
	};

	if (user?.username !== post.author) {
		return <StyledWrongDiv>🙅‍♂️타인의 블로그 내용을 수정할 수 없습니다.</StyledWrongDiv>;
	}

	return (
		isDoneWrite ?
			<PostWriteSetting title={title} content={content} isDoneWritePost={isDoneWritePost} /> :
			<WriteDiv>
				<StyledInput bordered={false} value={title} placeholder="제목을 입력하세요" onChange={onChangeTitle} />
				<PostEditor content={content} editContent={editContent} />
				<ButtonSpace align="center">
					<Link href={"/"}><Button>나가기</Button></Link>
					<StyledButtonDiv>
						<Button type="primary" onClick={updatePost}>수정완료</Button>
					</StyledButtonDiv>
				</ButtonSpace>
			</WriteDiv>
	);
};

export default Write;
