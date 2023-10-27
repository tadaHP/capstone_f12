import apiClient from "@/apis/apiClient";

export interface SignupUser {
	email: string;
	username: string;
	password: string;
	introduction?: string;
	blogTitle?: string;
	githubLink?: string;
	instagramLink?: string;
}

export const requestVerificationCodeAPI = async (email: string) => {
	try {
		await apiClient.post("/api/email", email, {
			headers: {
				"Content-Type": "text/plain",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 이메일 중복 또는 이메일 형식 문제임
		throw e;
	}
};

export const requestVerifyCodeAPI = async (code: string) => {
	try {
		await apiClient.post("/api/email/code", code, {
			headers: {
				"Content-Type": "text/plain",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 잘못된 인증 코드 입력임
		throw e;
	}
};

export const requestSignupAPI = async (user: SignupUser) => {
	try {
		await apiClient.post("/api/users/email/signup", user, {
			headers: {
				"Content-Type": "application/json",
			},
		});
	} catch (e) {
		// 여기서 예외 발생 시 이메일 검증이 되지 않은 이메일로 시도했거나 폼에 데이터가 빈 경우, 이메일 형식이 맞지 않는 경우임
		// 401 발생 시 이메일 인증 만료
		throw e;
	}
};
