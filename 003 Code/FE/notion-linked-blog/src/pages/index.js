import {Button, Col, Dropdown, Layout, Modal, Row, Space, Typography} from "antd";
import {useState} from "react";
import LoginForm from "@/components/auth/LoginForm";
import SignupForm from "@/components/auth/SignupForm";
import styled from "styled-components";
import {useAppDispatch, useAppSelector} from "@/hooks/hooks";
import {DownOutlined, UserOutlined} from "@ant-design/icons";
import {logoutAPI} from "@/apis/user";
import {logout} from "@/redux/userSlice";

const {Header} = Layout;

const {Text} = Typography;

const StyledText = styled(Text)`
  color: #FFF;
`;

const StyledSpace = styled(Space)`
  cursor: pointer;
`;

const StyledUserOutlined = styled(UserOutlined)`
  color: #FFF;
  cursor: pointer;
`;

const StyledDownOutlined = styled(DownOutlined)`
  color: #FFF;
  cursor: pointer;
`;

export default function Home() {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [existAccount, setExistAccount] = useState(true);
	const {user} = useAppSelector(state => state.user);
	const dispatch = useAppDispatch();
	const showModal = () => {
		setIsModalOpen(true);
	};

	const handleCancel = () => {
		setIsModalOpen(false);
	};

	const switchForm = () => {
		setExistAccount(!existAccount);
	};
	const handleLogout = async () => {
		await logoutAPI();
		dispatch(logout());
	};

	const items = [
		{
			label: <Text>내 정보</Text>,
			key: "0",
		}, {
			label: <Text>설정</Text>,
			key: "1",
		}, {
			label: <Text onClick={handleLogout}>로그아웃</Text>,
			key: "2",
		},
	];

	return (
		<Layout>
			<Header>
				<Row justify="space-between">
					<Col>
						<StyledText>Logo</StyledText>
					</Col>
					<Col>
						{!user ?
							(<Button type="primary" onClick={showModal}>로그인</Button>) :
							(
								<Dropdown menu={{items}} trigger={["click"]}>
									<StyledSpace>
										<StyledUserOutlined/>
										<StyledDownOutlined/>
									</StyledSpace>
								</Dropdown>
							)}
						<Modal title={existAccount ? "로그인" : "회원가입"} open={isModalOpen} footer={null} onCancel={handleCancel}>
							{existAccount ? <LoginForm switchForm={switchForm} setIsModalOpen={setIsModalOpen}/> : <SignupForm switchForm={switchForm}/>}
						</Modal>
					</Col>
				</Row>
			</Header>
		</Layout>
	);
}
