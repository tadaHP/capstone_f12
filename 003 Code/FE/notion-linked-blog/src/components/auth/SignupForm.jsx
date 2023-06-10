import React, {useEffect, useMemo, useState} from "react";
import {Button, Col, Form, Input, Row, Statistic, Typography} from "antd";
import {requestSignupAPI, requestVerificationCodeAPI, requestVerifyCodeAPI} from "@/apis/signup";
import {StyledDiv, StyledSpace, StyledText} from "@/components/auth/AuthForm";
import styled from "styled-components";
import {handleInput} from "@/components/auth/common";

const {Text} = Typography;

const {Countdown} = Statistic;

const StyledButton = styled(Button)`
  margin-left: 0.5rem;
`;

const StyledCol = styled(Col)`
  margin-top: 0.5rem;
`;

export default function SignupForm({switchForm}) {
	const [isRequestSendVerifyCode, setIsRequestSendVerifyCode] = useState(false);
	const deadline = useMemo(() => Date.now() + 1000 * 60 * 5, [isRequestSendVerifyCode]);
	const [form] = Form.useForm();
	const [email, onChangeEmail] = handleInput("");
	const [verificationCode, onChangeVerificationCode] = handleInput("");
	const [submitting, setSubmitting] = useState(false);
	const [startCountDown, setStartCountdown] = useState(false);
	const [requestCode, setRequestCode] = useState(false);
	const [resendLoading, setResendLoading] = useState(false);
	const [isVerified, setIsVerified] = useState(false);
	const [username, onChangeUsername] = handleInput("");
	const [password, onChangePassword] = handleInput("");
	const [signupLoading, setSignupLoading] = useState(false);
	const [isSignup, setIsSignup] = useState(false);
	const [isCorrectVerificationCode, setIsCorrectVerificationCode] = useState(true);

	const handleSignup = async () => {
		setSignupLoading(true);
		try {
			await form.validateFields();
			await requestSignupAPI({email, username, password});
			setIsSignup(true);
		} catch (e) {
			console.log("회원가입 도중 에러가 발생했습니다.", e);
		} finally {
			setSignupLoading(false);
		}
	};

	const handleRequestCode = async () => {
		try {
			await requestVerificationCodeAPI(email);
			setRequestCode(true);
			setStartCountdown(true);
			setIsRequestSendVerifyCode(true);
		} catch (e) {
			console.log("인증 코드 요청 관련 에러", e);
		}
	};

	const handleVerifyCode = async () => {
		setStartCountdown(false);
		try {
			await requestVerifyCodeAPI(verificationCode);
			setIsVerified(true);
			setIsRequestSendVerifyCode(false);
		} catch (e) {
			setStartCountdown(true);
			setIsCorrectVerificationCode(false);
			console.log("인증 코드 검증 에러", e);
		}
	};

	const handleSubmit = async () => {
		setSubmitting(true);
		try {
			await form.validateFields();
			(!requestCode ? await handleRequestCode() : await handleVerifyCode());
		} catch (e) {
			console.log("유효성 검증 에러", e);
		} finally {
			setSubmitting(false);
		}
	};

	const resendCode = async () => {
		setIsRequestSendVerifyCode(true);
		setResendLoading(true);
		setStartCountdown(false);
		await handleRequestCode();
		setResendLoading(false);
		setStartCountdown(true);
		setIsRequestSendVerifyCode(false);
	};

	useEffect(() => {
		setIsCorrectVerificationCode(true);
	}, [verificationCode]);
	return (
		<StyledSpace direction="vertical" size="large">
			<Text>이메일로 회원가입</Text>
			<Form
				form={form}
				labelCol={{span: 6}}>
				<Form.Item
					label="이메일"
					name="email"
					rules={[{required: true, pattern: /^[A-Za-z0-9]([-_.]?[A-Za-z0-9])*@[A-Za-z0-9]([-_.]?[A-Za-z0-9])*\.[A-Za-z]{2,3}$/i, message: "이메일 형식에 맞지 않는 메일 주소입니다. 다시 입력해 주세요."}]}
				>
					<Input onChange={onChangeEmail} value={email} placeholder="인증 코드를 받을 이메일을 입력하세요" disabled={requestCode}/>
				</Form.Item>
				{!isVerified && requestCode && (
					<Form.Item
						label="인증 코드"
						name="verificationCode"
						rules={[{required: true, pattern: /\S/g, message: "인증 코드를 입력해 주세요"}]}
						validateStatus={!isCorrectVerificationCode ? "error" : null}
						help={!isCorrectVerificationCode ? "올바른 인증코드를 입력해주세요" : null}
					>
						<Input onChange={onChangeVerificationCode} value={verificationCode} placeholder="전송된 인증 코드를 입력해 주세요"/>
					</Form.Item>
				)}
				{isVerified && (
					<>
						<Form.Item
							label="이름"
							name="username"
							rules={[{required: true, pattern: /\S/g, message: "이름은 필수 입력사항입니다"}]}
						>
							<Input onChange={onChangeUsername} value={username}/>
						</Form.Item>
						<Form.Item
							label="비밀번호"
							name="password"
							rules={[{required: true, pattern: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/, message: "비밀번호는 8자로 영문 대 소문자, 숫자, 특수기호를 조합해서 사용하세요."}]}
						>
							<Input.Password onChange={onChangePassword} value={password}/>
						</Form.Item>
						<Form.Item
							label="비밀번호확인"
							name="passwordForConfirm"
							dependencies={["password"]}
							rules={[
								{required: true, pattern: /\S/g, message: "비밀번호가 일치하지 않습니다."}, ({getFieldValue}) => ({
									validator(_, value) {
										if (!value || getFieldValue("password") === value) {
											return Promise.resolve();
										}
										return Promise.reject(new Error("비밀번호가 일치하지 않습니다."));
									},
								}),
							]}
						>
							<Input.Password/>
						</Form.Item>
						{isSignup && (
							<Row justify="center">
								<Text>회원가입이 완료되셨습니다🎉 로그인 창에서 로그인 해주세요!</Text>
							</Row>
						)}
						{!isSignup && (
							<Row justify="end">
								<Button type="primary" onClick={handleSignup} loading={signupLoading}>회원가입</Button>
							</Row>
						)}
					</>
				)}
				{!isVerified && (
					<>
						<Form.Item>
							<Row justify="end" align="middle">
								<Col>
									{startCountDown && <Countdown valueStyle={{fontSize: "1rem"}} value={deadline} format="mm:ss"/>}
								</Col>
								<Col>
									<StyledButton type="primary" htmlType="submit" onClick={handleSubmit} loading={submitting}>
										{!requestCode ? "인증 코드 발송" : "검증 요청"}
									</StyledButton>
								</Col>
							</Row>
							<Row justify="end">
								<StyledCol>
									{requestCode && <Button onClick={resendCode} loading={resendLoading}>재전송</Button>}
								</StyledCol>
							</Row>
						</Form.Item>
					</>
				)}
			</Form>
			<StyledDiv>
				<StyledText>
					<Button type="link" onClick={switchForm}>로그인</Button>
				</StyledText>
			</StyledDiv>
		</StyledSpace>
	);
}
