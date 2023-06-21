import React, {useState} from "react";
import handleInput from "@/components/auth/common";
import {Input, Space, Divider, Button, Radio} from "antd";
import Link from "next/link";
import {FolderAddOutlined, GlobalOutlined, LockOutlined} from "@ant-design/icons";
import {requestSubmitPostAPI} from "@/apis/post";
import Uploader from "@/components/post/Uploader";
import {CoverDiv, UploaderDiv} from "@/components/post/Post";

const {TextArea} = Input;

const PostWriteSetting = props => {
	const [thumbImage, setThumbImage] = useState({
		image_file: "",
		preview_URL: "",
	});
	const [description, onChangeDescription] = handleInput("");
	const [isPublic, setIsPublic] = useState(0);
	const [loading, setLoading] = useState(false);
	const title = props.title;
	const content = props.content;
	const thumbnail = thumbImage.image_file;

	const setPublicRange = e => {
		setIsPublic(e.target.value);
	};

	const onChangeThumbImage = image => {
		setThumbImage(image);
	};

	const handleSubmitPost = async () => {
		setLoading(true);
		const formData = new FormData();

		formData.append("title", title);
		formData.append("content", content);
		formData.append("thumbnail", thumbnail);
		formData.append("description", description.toString());
		formData.append("isPublic", isPublic.toString());

		try {
			await requestSubmitPostAPI(formData);
		} catch (e) {
			console.log("포스트 전송 에러", e);
		} finally {
			setLoading(false);
		}
	};

	return (
		<>
			<CoverDiv>
				<Space split={<Divider type="vertical"/>}>
					<Space direction="vertical">
						<p>포스트 미리보기</p>
						<div>
							<UploaderDiv>
								<Uploader thumbImage={thumbImage} changeThumbImage={onChangeThumbImage}/>
							</UploaderDiv>
						</div>
						<p>{props.title}</p>
						<TextArea
							showCount
							maxLength={140}
							value={description}
							style={{
								height: 120,
								resize: "none",
							}}
							onChange={onChangeDescription}
							placeholder="disable resize"
						/>
					</Space>
					<Space direction="vertical">
						<Space direction="vertical">
							<p>공개설정</p>
							<Space style={{width: "100%"}}>
								<Radio.Group onChange={setPublicRange} value={isPublic} style={{margin: "10px"}}>
									<Radio.Button icon={<GlobalOutlined/>} value={0}>전체 공개</Radio.Button>
									<Radio.Button icon={<LockOutlined/>} value={1}>비공개</Radio.Button>
								</Radio.Group>
							</Space>
						</Space>
						<Space direction="vertical">
							<p>시리즈설정</p>
							<Button
								icon={<FolderAddOutlined/>} style={{margin: "10px"}}
							>
								시리즈에 추가하기
							</Button>
						</Space>
						<Space style={{margin: "10px"}}>
							<Button onClick={props.isDoneWritePost}>취소</Button>
							<Link href={"/"}><Button onClick={handleSubmitPost} loading={loading}>출간하기</Button></Link>
						</Space>
					</Space>
				</Space>
			</CoverDiv>
		</>
	);
};

export default PostWriteSetting;
