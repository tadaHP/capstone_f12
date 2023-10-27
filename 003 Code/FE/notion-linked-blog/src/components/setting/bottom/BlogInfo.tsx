import {CSSProperties, useEffect, useMemo, useState} from "react";
import {Button, Form, Typography} from "antd";
import styled from "styled-components";
import {RootState} from "@/redux/store";
import {UserState, modifyBlogTitle} from "@/redux/userSlice";
import {modifyBlogTitleAPI} from "@/apis/user";
import {useSelector, useDispatch} from "react-redux";
import {Container, EditBtn, RowContainer, SpaceBetweenContainer, StyledInput, StyledTitle} from "../Common";

const {Text} = Typography;

const StyledText = styled(Text)`
	font-size: 1rem;
`;

export default function BlogInfo() {
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const [isEdit, setIsEdit] = useState(false);
	const [title, setTitle] = useState("");
	const [error, setError] = useState(false);
	const errorMsg = "네트워크 에러가 발생했습니다. 잠시 후에 다시 시도해주세요.";
	const dispatch = useDispatch();

	useEffect(() => {
		setTitle(user?.blogTitle);
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

	const handleEditBtn = () => {
		setTitle(user.blogTitle);
		setIsEdit(true);
	};

	const handleSubmitBtn = async () => {
		try {
			await modifyBlogTitleAPI(title, user.id);
			dispatch(modifyBlogTitle(title));
			setIsEdit(false);
			setError(false);
		} catch (e) {
			setError(true);
		}
	};

	const handleBlogTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setTitle(e.target.value);
	};

	return (
		<>
			<Container>
				<RowContainer>
					<StyledTitle level={4}>
						블로그 제목
					</StyledTitle>
					<SpaceBetweenContainer>
						{isEdit ?
							<Form layout="inline" style={formStyle}>
								<Form.Item style={inputItemStyle}>
									<StyledInput value={title} onChange={handleBlogTitleChange} />
									{error && <Typography.Text type="danger">{errorMsg}</Typography.Text>}
								</Form.Item>
								<Form.Item style={submitBtnItemStyle}>
									<Button type="primary" onClick={handleSubmitBtn}>저장</Button>
								</Form.Item>
							</Form> :
							<>
								<StyledText>{user?.blogTitle}</StyledText>
								<EditBtn onClick={handleEditBtn}>수정</EditBtn>
							</>
						}
					</SpaceBetweenContainer>
				</RowContainer>
				<div>
					<Text type="secondary">
						개인 페이지의 좌측 상단에 나타나는 페이지 제목입니다.
					</Text>
				</div>
			</Container>
		</>
	);
}
