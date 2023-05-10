import {apiClient} from "@/apis/apiClient";

export const requestVerificationCodeAPI = async email => {
	try {
		await apiClient.post("/email", email, {
			withCredentials: true,
			headers: {
				"Content-Type": "text/plain",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 이메일 중복 또는 이메일 형식 문제임
		throw e;
	}
};

export const requestVerifyCodeAPI = async code => {
	try {
		await apiClient.post("/email/code", code, {
			withCredentials: true,
			headers: {
				"Content-Type": "text/plain",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 잘못된 인증 코드 입력임
		throw e;
	}
};

export const requestSignupAPI = async user => {
	try {
		await apiClient.post("/users/email/signup", user, {
			withCredentials: true,
			headers: {
				"Content-Type": "application/json",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 이메일 검증이 되지 않은 이메일로 시도했거나 폼에 데이터가 빈 경우, 이메일 형식이 맞지 않는 경우임
		throw e;
	}
};
