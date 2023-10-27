import apiClient from "./apiClient";

export interface Post {
	postId: number;
	title: string;
	content: string;
	viewCount: number;
	likes: number;
	requestThumbnailLink: string;
	description: string;
	createdAt: string;
	countOfComments: number;
	author: string;
	avatar: string;
}

export async function getThumbnailAPI(url: string) {
	try {
		const resp = await apiClient.get(url, {
			responseType: "blob",
		});

		return resp;
	} catch (e) {
	}
}

export async function getPostByIdAPI(postId: string): Promise<Post> {
	try {
		const resp = await apiClient.get(`/api/posts/${postId}`);

		return resp.data;
	} catch (e) {
	}
}

export async function loadPostAPI(pageNumber: number): Promise<Post[]> {
	try {
		const resp = await apiClient.get(`/api/posts/newest/${pageNumber}`);

		return resp.data.posts;
	} catch (e) {
		let errorMsg;

		switch (e.response.status) {
			case 400:
				errorMsg = "잘못된 입력을 서버로 전송했습니다.";
				break;
			case 401:
				errorMsg = "권한이 없습니다.";
				break;
			default:
				errorMsg = "서버 오류입니다.";
				break;
		}

		throw new Error(errorMsg);
	}
}

export const requestSubmitPostAPI = async post => {
	try {
		await apiClient.post("/api/posts", post, {
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

export const requestDeletePostAPI = async postId => {
	try {
		await apiClient.delete(`/api/posts/${postId}`);
	} catch (e) {
		// 400 : Bad Request
		// 401 : 작성자와 삭제시도자 불일치
		// 404 : 포스트 데이터 미존재
		// 415 : Unsupported Media Type
		throw e;
	}
};

export const requestUpdatePostAPI = async post => {
	const data = {
		"title": post.title,
		"content": post.content,
	};

	try {
		await apiClient.put(`/api/posts/${post.postId}`, data, {
			headers: {
				"Content-Type": "application/json",
			},
		});
	} catch (e) {
		// 400 : Bad Request
		// 401 : 회원 미로그인
		// 404 : 포스트 데이터 미존재
		// 415 : Unsupported Media Type
		throw e;
	}
};
