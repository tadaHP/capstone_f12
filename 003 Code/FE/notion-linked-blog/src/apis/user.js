import {apiClient} from "@/apis/apiClient";

export const checkLoginStatus = async () => {
	try {
		return await apiClient.get("/users/login-status");
	} catch (e) {
		throw e;
	}
};

export const loginByEmailAPI = async userDetails => {
	try {
		return await apiClient.post("/login/email", userDetails, {
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
		await apiClient.post("/logout");
	} catch (e) {
		throw e;
	}
};
