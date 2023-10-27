import React, {useEffect, useState} from "react";
import handleInput from "@/components/auth/common";
import {Input, Space, Divider, Button, Radio, Form, List, Typography} from "antd";
import Link from "next/link";
import {FolderAddOutlined, GlobalOutlined, LockOutlined, SettingFilled} from "@ant-design/icons";
import {requestSubmitPostAPI} from "@/apis/post";
import Uploader from "@/components/post/Uploader";
import {UploaderDiv} from "@/components/post/Post";
import {getMySeries} from "@/apis/user";
import {useSelector} from "react-redux";
import {RootState} from "@/redux/store";
import styled from "styled-components";
import {addSeries} from "@/apis/series";

const {TextArea} = Input;

const ContainerSpace = styled(Space)`
	display: flex;
	justify-content: center;
	align-items: flex-start;
`;

interface IStyledListItem {
	myTitle: string;
	selected: string;
}

const StyledListItem = styled(List.Item) <IStyledListItem>`
		background-color: ${props => props.myTitle === props.selected && "#1677ff"};
		> span {
			color: ${props => props.myTitle === props.selected && "#fff"};
		}
`;

const PostWriteSetting = props => {
	const [thumbImage, setThumbImage] = useState({
		image_file: "",
		preview_URL: "https://notionlinkedblog.s3.ap-northeast-2.amazonaws.com/thumbnail/DefaultThumbnail.jpg",
	});
	const [description, onChangeDescription] = handleInput("");
	const [isPublic, setIsPublic] = useState(0);
	const [loading, setLoading] = useState(false);
	const [isOpenSereisCreate, setIsOpenSereisCreate] = useState(false);
	const [isOpenSeriesAddingBtn, setIsOpenSeriesAddingBtn] = useState(false);
	const [series, setSeries] = useState([]);
	const [selectedSeriesTitle, setSelectedSeriesTitle] = useState("");
	const [selectedSeriesId, setSelectedSeriesId] = useState(-1);
	const title = props.title;
	const content = props.content;
	const thumbnail = thumbImage.image_file;
	const id = useSelector<RootState, number>(state => state.user.user?.id);

	useEffect(() => {
		const fetchMySeries = async () => {
			const resp = await getMySeries(id);

			setSeries(resp.data);
		};

		if (id) {
			fetchMySeries();
		}
	}, [id]);

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
		formData.append("seriesId", selectedSeriesId !== -1 && selectedSeriesId.toString());

		try {
			await requestSubmitPostAPI(formData);
		} catch (e) {
			console.log("포스트 전송 에러", e);
		} finally {
			setLoading(false);
		}
	};

	const handleFinish = async ({seriesTitle}) => {
		const newSeriesId = await addSeries(id, seriesTitle);

		setSeries(prev => prev.concat({seriesId: newSeriesId, title: seriesTitle}));
	};

	return (
		<div style={{height: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
			<ContainerSpace split={<Divider type={window.innerWidth >= 768 ? "vertical" : "horizontal"} />}>
				<Space direction="vertical">
					<Typography.Title level={4}>포스트 미리보기</Typography.Title>
					<div>
						<UploaderDiv>
							<Uploader thumbImage={thumbImage} changeThumbImage={onChangeThumbImage} />
						</UploaderDiv>
					</div>
					<Typography.Title level={5}>{props.title}</Typography.Title>
					<TextArea
						showCount
						maxLength={140}
						value={description}
						style={{
							height: 100,
							resize: "none",
						}}
						onChange={onChangeDescription}
						placeholder="당신의 포스트를 짧게 소개해 보세요"
					/>
				</Space>
				{!isOpenSereisCreate ?
					<Space direction="vertical">
						<Space direction="vertical">
							<Typography.Title level={4}>공개설정</Typography.Title>
							<Space style={{width: "100%"}}>
								<Radio.Group onChange={setPublicRange} value={isPublic} style={{margin: "10px"}}>
									<Radio.Button value={0}><GlobalOutlined /> 전체 공개</Radio.Button>
									<Radio.Button value={1}><LockOutlined /> 비공개</Radio.Button>
								</Radio.Group>
							</Space>
						</Space>
						<Space direction="vertical" style={{width: "100%"}}>
							<Typography.Title level={4}>시리즈설정</Typography.Title>
							{!selectedSeriesTitle ? <Button
								onClick={() => setIsOpenSereisCreate(true)}
								icon={<FolderAddOutlined />} style={{margin: "10px"}}
							>시리즈에 추가하기
							</Button> :
								<div style={{width: "100%"}}>
									<div style={{height: "48px", borderRadius: "4px", backgroundColor: "white"}}>
										<div style={{height: "100%", padding: "0 1rem", fontSize: "1.125rem", display: "flex", justifyContent: "space-between", alignItems: "center"}}>
											<span>{selectedSeriesTitle}</span>
											<Button icon={<SettingFilled />} onClick={() => setIsOpenSereisCreate(true)}></Button>
										</div>
									</div>
									<Button type="link" danger onClick={() => {
										setSelectedSeriesTitle("");
										setSelectedSeriesId(-1);
									}}>
										시리즈에서 제거
									</Button>
								</div>
							}
						</Space>
						<Space style={{margin: "10px"}}>
							<Button onClick={props.isDoneWritePost}>취소</Button>
							<Link href={"/"}><Button type="primary" onClick={handleSubmitPost} loading={loading}>출간하기</Button></Link>
						</Space>
					</Space> :
					<Space style={{width: "350px"}} direction="vertical">
						<Typography.Title level={4}>시리즈 설정</Typography.Title>
						<Form
							onFinish={handleFinish}>
							<Form.Item name="seriesTitle">
								<Input style={{fontSize: "0.875rem"}} onClick={() => setIsOpenSeriesAddingBtn(true)} placeholder="새로운 시리즈 이름을 입력하세요" />
							</Form.Item>
							{isOpenSeriesAddingBtn &&
								<div style={{display: "flex", justifyContent: "flex-end"}}>
									<Space>
										<Form.Item>
											<Button onClick={() => setIsOpenSeriesAddingBtn(false)}>취소</Button>
										</Form.Item>
										<Form.Item>
											<Button type="primary" htmlType="submit">시리즈 추가</Button>
										</Form.Item>
									</Space>
								</div>
							}
						</Form>
						<div style={{height: "250px", position: "relative"}}>
							<List
								style={{backgroundColor: "white", maxHeight: "100%", overflow: "auto"}}
								size="small"
								bordered
								dataSource={series}
								renderItem={item => (
									<StyledListItem
										key={item.seriesId}
										myTitle={item.title}
										selected={selectedSeriesTitle}
										onClick={() => {
											setSelectedSeriesTitle(item.title);
											setSelectedSeriesId(item.seriesId);
										}}>
										<Typography.Text>{item.title}</Typography.Text>
									</StyledListItem>
								)}>
							</List>
						</div>
						<div style={{display: "flex", justifyContent: "flex-end"}}>
							<Space>
								<Button onClick={() => {
									setSelectedSeriesTitle("");
									setSelectedSeriesId(-1);
									setIsOpenSereisCreate(false);
								}}>취소</Button>
								<Button type="primary" onClick={() => {
									setIsOpenSereisCreate(false);
									setIsOpenSeriesAddingBtn(false);
								}}>선택하기</Button>
							</Space>
						</div>
					</Space>
				}
			</ContainerSpace>
		</div >
	);
};

export default PostWriteSetting;
