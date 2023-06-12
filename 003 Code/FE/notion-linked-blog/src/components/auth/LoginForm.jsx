import {Button, Form, Input, Typography} from "antd";
import styled from "styled-components";
import {StyledDiv, StyledSpace, StyledText} from "@/components/auth/AuthForm";
import {useEffect, useState} from "react";
import handleInput from "@/components/auth/common";
import {loginByEmailAPI} from "@/apis/user";
import {useAppDispatch} from "@/hooks/hooks";
import {login} from "@/redux/userSlice";

const {Text} = Typography;

const StyledFormItem = styled(Form.Item)`
  display: flex;
  justify-content: flex-end;
`;

export default function LoginForm({switchForm, setIsModalOpen}) {
	const [email, onChangeEmail] = handleInput("");
	const [password, onChangePassword] = handleInput("");
	const [loading, setLoading] = useState(false);
	const dispatch = useAppDispatch();
	const [err, setErr] = useState("");
	const handleSubmit = async () => {
		setLoading(true);
		try {
			const response = await loginByEmailAPI({email, password});

			dispatch(login(response.data.user));
			setIsModalOpen(false);
			setErr("");
		} catch (e) {
			if (email.length > 0 && password.length > 0) {
				setErr("이메일 또는 비밀번호를 잘못 입력했습니다.");
			}
			console.log("로그인 실패", e);
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		setErr("");
	}, [email, password]);

	const ErrorDiv = styled.div`
		text-align: center;
		color: red;
	`;

	return (
		<StyledSpace direction="vertical" size="large">
			<Text>이메일로 로그인</Text>
			<Form
				labelCol={{span: 5}}>
				<Form.Item
					label="이메일"
					name="email"
					rules={[{required: true, message: "이메일을 입력해 주세요."}]}
				>
					<Input onChange={onChangeEmail} placeholder="이메일을 입력하세요" value={email}/>
				</Form.Item>
				<Form.Item
					label="비밀번호"
					name="password"
					rules={[{required: true, message: "비밀번호를 입력해 주세요"}]}
				>
					<Input.Password onChange={onChangePassword} placeholder="비밀번호를 입력하세요" value={password}/>
				</Form.Item>
				<ErrorDiv>{err}</ErrorDiv>
				<StyledFormItem>
					<Button type="primary" htmlType="submit" onClick={handleSubmit} loading={loading}>로그인</Button>
				</StyledFormItem>
			</Form>
			<StyledDiv>
				<StyledText>
					아직 계정이 없으신가요?<Button type="link" onClick={switchForm}>회원가입</Button>
				</StyledText>
			</StyledDiv>
		</StyledSpace>
	);
}
