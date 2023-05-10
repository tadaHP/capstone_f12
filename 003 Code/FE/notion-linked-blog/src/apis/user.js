import {apiClient} from "@/apis/apiClient";

export const loginByEmailAPI = async userDetails => {
	try {
		return await apiClient.post("/login/email", userDetails, {
			withCredentials: true,
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
		await apiClient.post("/logout", null, {withCredentials: true});
	} catch (e) {
		throw e;
	}
};
