import apiClient from "@/apis/apiClient";

export interface User {
	id: number;
	username: string;
	email: string;
	profile: string;
	introduction: string;
	blogTitle: string;
	githubLink: string;
	instagramLink: string;
	notionCertificate: boolean;
}

export const checkLoginStatus = async () => {
	try {
		return await apiClient.get("/api/users/login-status");
	} catch (e) {
		throw e;
	}
};

export const loginByEmailAPI = async userDetails => {
	try {
		return await apiClient.post("/api/login/email", userDetails, {
			headers: {
				"Content-Type": "application/json",
			},
		});
	} catch (e) {
		throw e;
	}
};

export const logoutAPI = async () => {
	try {
		await apiClient.post("/api/logout");
	} catch (e) {
		throw e;
	}
};

export const signoutAPI = async (id: number) => {
	let errorMsg;

	try {
		await apiClient.delete(`/api/users/${id}`);
	} catch (e) {
		switch (e.response.status) {
			case 401:
				errorMsg = "현재 로그인한 사용자가 아닌 사용자의 정보로 요청했습니다";
				break;
			case 404:
				errorMsg = "존재하지 않는 자원에 접근하였습니다.";
				break;
			case 500:
				errorMsg = "서버 에러입니다.";
				break;
			default:
				errorMsg = e.response.status;
				break;
		}
		throw new Error(errorMsg);
	}
};

export const modifyBlogTitleAPI = async (blogTitle: string, id: number) => {
	try {
		await apiClient.put(`/api/users/blogTitle/${id}`, {blogTitle});
	} catch (e) {
		throw new Error(e);
	}
};

export interface ModifyingSocialInfo {
	githubLink: string;
	instagramLink: string;
}

export const modifySocialInfoAPI = async (
	{githubLink, instagramLink}: ModifyingSocialInfo, id: number,
) => {
	try {
		await apiClient.put(`/api/users/social/${id}`, {githubLink, instagramLink});
	} catch (e) {
		throw new Error(e);
	}
};

export interface ModifyingBasicInfo {
	username: string;
	introduction: string;
}

export const modifyBasicInfoAPI = async (
	{username, introduction}: ModifyingBasicInfo, id: number,
) => {
	try {
		await apiClient.put(`/api/users/basic/${id}`, {username, introduction});
	} catch (e) {
		throw new Error(e);
	}
};

export const modifyProfileImageAPI = async (profile: FormData, id: number) => {
	try {
		const resp = await apiClient.put(`/api/users/profileImage/${id}`, profile, {
			headers: {
				"Content-Type": "multipart/form-data",
			},
		});

		return resp.data;
	} catch (e) {
		throw new Error(e);
	}
};

export const getProfileImageAPI = async (id: number) => {
	try {
		const resp = await apiClient.get(`/api/users/profile/${id}`);

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export const deleteProfileImageAPI = async (id: number) => {
	try {
		const resp = await apiClient.delete(`/api/users/profileImage/${id}`);

		return resp.data;
	} catch (e) {
		throw new Error(e);
	}
};

export const getUser = async (id: number) => {
	try {
		return (await apiClient.get(`/api/users/${id}`)).data;
	} catch (e) {
		throw e;
	}
};
