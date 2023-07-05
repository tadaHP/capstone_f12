import {CSSProperties, useEffect, useMemo, useState} from "react";
import {Button, Form, Space, Typography} from "antd";
import styled from "styled-components";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "@/redux/store";
import {UserState, modifyBasicInfo} from "@/redux/userSlice";
import {modifyBasicInfoAPI} from "@/apis/user";
import {EditBtn, StyledInput} from "../Common";

const {Title, Paragraph} = Typography;

const StyledTitle = styled(Title)`
	margin: 0;
`;

const BasicInfoSpace = styled(Space)`
  width: 100%;
  height: 100%;
  padding-left: 1.5rem;
  flex-direction: column;
  justify-content: start;;
  align-items: start;
  border-left: 0.8px solid rgb(205, 205, 205);

  @media screen and (max-width: 768px) {
    border-left: none;
  }
`;

const EditingBasicInfoSpace = styled(Space)`
	width: 100%;

	.ant-form-item {
		margin-bottom: 0px;
	}
`;

const Container = styled.div`
	width: 100%;
`;

export default function Introduction() {
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const [isEdit, setIsEdit] = useState(false);
	const [username, setUsername] = useState("");
	const [introduction, setIntroduction] = useState("");
	const [error, setError] = useState(false);
	const dispatch = useDispatch();
	const [isBlankUsername, setIsBlankUsername] = useState(false);

	useEffect(() => {
		setUsername(user?.username);
		setIntroduction(user?.introduction);
	}, []);

	const submitBtnItemStyle: CSSProperties = useMemo(() => ({
		display: "flex",
		justifyContent: "end",
	}), []);

	const formStyle: CSSProperties = useMemo(() => ({
		width: "100%",
		alignItems: "center",
	}), []);

	const handleEditBtn = () => {
		setUsername(user.username);
		setIntroduction(user.introduction);
		setIsEdit(true);
	};

	const onFinish = async () => {
		try {
			if (username === "") {
				setIsBlankUsername(true);
				return;
			}

			setIsBlankUsername(false);
			await modifyBasicInfoAPI({username, introduction}, user.id);
			dispatch(modifyBasicInfo({username, introduction}));
			setIsEdit(false);
			setError(false);
		} catch (e) {
			setError(true);
		}
	};

	const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setUsername(e.target.value);
	};

	const handleIntroductionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setIntroduction(e.target.value);
	};

	return (
		<Container>
			{isEdit ?
				<>
					<Form layout="vertical" onFinish={onFinish} style={formStyle}>
						<EditingBasicInfoSpace direction="vertical">
							<Form.Item>
								<StyledInput type="large"
									value={username}
									onChange={handleUsernameChange}
									placeholder="이름" />
							</Form.Item>
							{isBlankUsername && <Typography.Text type="danger">이름은 필수입니다.</Typography.Text>}
							<Form.Item>
								<StyledInput
									value={introduction}
									onChange={handleIntroductionChange}
									placeholder="한 줄 소개" />
							</Form.Item>
							<Form.Item style={submitBtnItemStyle}>
								<Button type="primary" htmlType="submit">저장</Button>
							</Form.Item>
						</EditingBasicInfoSpace>
					</Form>
					{error && <Typography.Text type="danger">
						네트워크 에러가 발생했습니다. 잠시 후에 다시 시도해주세요.
					</Typography.Text>}
				</> :
				<BasicInfoSpace>
					<StyledTitle>{user?.username}</StyledTitle>
					<Paragraph>{user?.introduction}</Paragraph>
					<EditBtn onClick={handleEditBtn}>수정</EditBtn>
				</BasicInfoSpace>
			}
		</Container >
	);
}
