import {apiClient} from "@/apis/apiClient";

export const requestSubmitPostAPI = async post => {
	try {
		await apiClient.post("/posts", post, {
			headers: {
				"Content-Type": "multipart/form-data",
			},
		});
	} catch (e) {
		// 400 : Bad Request
		// 401 : Unauthorized
		throw e;
	}
};