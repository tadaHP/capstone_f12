import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import React, {useState} from "react";
import dynamic from "next/dynamic";
import {handleInput} from "@/components/auth/common";
import PostWriteSetting from "@/components/post/PostWriteSetting";
import {Button, Input} from "antd";
import Link from "next/link";
import {
	ButtonSpace,
	TempButton,
	WriteDiv,
} from "@/components/post/Post";

const PostEditor = dynamic(
	() => import("@/components/post/PostWrite"),
	{ssr: false},
);


const Write = () => {
	const [title, onChangeTitle] = handleInput("");
	const [content, setContent] = useState("**Hello Test World!**");
	const [isDoneWrite, setIsDoneWrite] = useState(false);

	const isDoneWritePost = () => {
		setIsDoneWrite(prev => !prev);
	};

	const editContent = content => {
		setContent(content);
	};

	return (
		isDoneWrite ?
			<PostWriteSetting title={title} content={content} isDoneWritePost={isDoneWritePost}/> :
			<WriteDiv>
				<Input bordered={false} value={title} placeholder="제목을 입력하세요" onChange={onChangeTitle} style={{fontSize: "3rem"}}></Input>
				<PostEditor content={content} editContent={editContent}/>
				<ButtonSpace align="center">
					<Link href={"./"}><Button>나가기</Button></Link>
					<div>
						<TempButton type="primary">임시저장</TempButton>
						<Button type="primary" onClick={isDoneWritePost}>출간하기</Button>
					</div>
				</ButtonSpace>
			</WriteDiv>
	);
};

export default Write;
