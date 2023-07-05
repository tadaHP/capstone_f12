import {CSSProperties, useEffect, useMemo, useState} from "react";
import {Button, Form, Space, Typography} from "antd";
import {useDispatch, useSelector} from "react-redux";
import styled from "styled-components";
import {GithubOutlined, InstagramOutlined} from "@ant-design/icons";
import {modifySocialInfoAPI} from "@/apis/user";
import {RootState} from "@/redux/store";
import {UserState, modifySocialInfo} from "@/redux/userSlice";
import {Container, EditBtn, RowContainer, SpaceBetweenContainer, StyledInput, StyledTitle} from "../Common";

const {Text} = Typography;

const AlignItemsDiv = styled.div`
	display: flex;
	align-items: center;
`;

export default function SocialInfo() {
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const [isEdit, setIsEdit] = useState(false);
	const [githubLink, setGithubLink] = useState("");
	const [instagramLink, setInstagramLink] = useState("");
	const [error, setError] = useState(false);
	const errorMsg = "네트워크 에러가 발생했습니다. 잠시 후에 다시 시도해주세요.";
	const dispatch = useDispatch();

	useEffect(() => {
		setGithubLink(user?.githubLink);
		setInstagramLink(user?.instagramLink);
	}, []);

	const inputItemStyle: CSSProperties = useMemo(() => ({
		flex: "1",
	}), []);

	const submitBtnItemStyle: CSSProperties = useMemo(() => ({
		margin: 0,
	}), []);

	const formStyle: CSSProperties = useMemo(() => ({
		width: "100%",
		alignItems: "center",
	}), []);

	const iconStyle: CSSProperties = useMemo(() => ({
		fontSize: "1rem",
		marginRight: "0.5rem",
	}), []);

	const handleEditBtn = () => {
		setGithubLink(user.githubLink);
		setInstagramLink(user.instagramLink);
		setIsEdit(true);
	};

	const handleSubmitBtn = async () => {
		try {
			await modifySocialInfoAPI({githubLink, instagramLink}, user.id);
			dispatch(modifySocialInfo({githubLink, instagramLink}));
			setIsEdit(false);
			setError(false);
		} catch (e) {
			setError(true);
		}
	};

	const handleGithubLinkChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setGithubLink(e.target.value);
	};

	const handleInstagramLinkChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setInstagramLink(e.target.value);
	};

	return (
		<>
			<Container>
				<RowContainer>
					<StyledTitle level={4}>
						소셜 정보
					</StyledTitle>
					<SpaceBetweenContainer>
						{isEdit ?
							<Form layout="inline" style={formStyle}>
								<Form.Item style={inputItemStyle}>
									<AlignItemsDiv>
										<GithubOutlined style={iconStyle} />
										<StyledInput
											value={githubLink}
											onChange={handleGithubLinkChange} />
									</AlignItemsDiv>
									<AlignItemsDiv>
										<InstagramOutlined style={iconStyle} />
										<StyledInput
											value={instagramLink}
											onChange={handleInstagramLinkChange} />
									</AlignItemsDiv>
									{error && <Typography.Text type="danger">{errorMsg}</Typography.Text>}
								</Form.Item>
								<Form.Item style={submitBtnItemStyle}>
									<Button type="primary" onClick={handleSubmitBtn}>저장</Button>
								</Form.Item>
							</Form> :
							<>
								<Space direction="vertical">
									<Typography.Text>
										<GithubOutlined style={iconStyle} />{user?.githubLink}
									</Typography.Text>
									<Typography.Text>
										<InstagramOutlined style={iconStyle} />{user?.instagramLink}
									</Typography.Text>
								</Space>
								<EditBtn onClick={handleEditBtn}>수정</EditBtn>
							</>
						}
					</SpaceBetweenContainer>
				</RowContainer>
				<div>
					<Text type="secondary">
						포스트 및 블로그에서 보여지는 프로필에 공개되는 소셜 정보입니다.
					</Text>
				</div>
			</Container>
		</>
	);
}
