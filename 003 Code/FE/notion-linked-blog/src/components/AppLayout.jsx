import {Button, Col, Dropdown, Layout, Modal, Row, Space, Typography} from "antd";
import {useState} from "react";
import LoginForm from "@/components/auth/LoginForm";
import SignupForm from "@/components/auth/SignupForm";
import styled from "styled-components";
import {useAppDispatch, useAppSelector} from "@/hooks/hooks";
import {DownOutlined, UserOutlined} from "@ant-design/icons";
import {logoutAPI} from "@/apis/user";
import {logout} from "@/redux/userSlice";
import PostCard from "@/components/post/PostCard";

const {Header, Content} = Layout;

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

const StyledLayout = styled(Layout)`
  display: flex;
  justify-content: center;
  width: 100%;
`;

const StyledHeader = styled(Header)`
  display: flex;
  justify-content: center;
  padding: 0
`;

const StyledHeaderRow = styled(Row)`
  display: flex;
  width: 100%;
  max-width: 1728px;
  justify-content: space-between;
`;

const StyledContent = styled(Content)`
  display: flex;
  justify-content: center;
  margin: 32px 0;
`;

const StyledContentRow = styled(Row)`
  max-width: 1760px;
`;

export default function AppLayout({mainPosts}) {
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
		<StyledLayout>
			<StyledHeader>
				<StyledHeaderRow>
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
							{existAccount ?
								<LoginForm switchForm={switchForm} setIsModalOpen={setIsModalOpen}/> :
								<SignupForm switchForm={switchForm}/>
							}
						</Modal>
					</Col>
				</StyledHeaderRow>
			</StyledHeader>
			<StyledContent>
				<StyledContentRow gutter={[32, 32]} justify="center">
					{mainPosts.map(post => (
						<Col key={post.id}>
							<PostCard key={post.id} post={post}/>
						</Col>
					))}
				</StyledContentRow>
			</StyledContent>
		</StyledLayout>
	);
}
