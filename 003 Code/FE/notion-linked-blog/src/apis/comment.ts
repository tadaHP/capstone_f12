import apiClient from "@/apis/apiClient";
import convertKRTimeStyle from "@/utils/time";

export interface CommentGetResponse {
	comment: string,
	commentId: number,
	createdAt: Date,
	author: string,
	authorId: number;
	authorProfileLink?: string,
	children?: CommentGetResponse[],
	parentId?: number;
}

export const requestGetCommentAPI = async (postId: number | string)
	: Promise<CommentGetResponse[]> => {
	try {
		const resp = await apiClient.get(`/api/posts/${postId}/comments`);

		for (const comment of resp.data) {
			comment.createdAt = convertKRTimeStyle(comment.createdAt);

			if (comment.children) {
				for (const child of comment.children) {
					child.createdAt = convertKRTimeStyle(child.createdAt);
				}
			}
		}

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export interface CommentPost {
	comment: string;
	depth: number;
	parentCommentId?: number;
}

export const requestAddCommentAPI = async (body: CommentPost, postId: number | string)
	: Promise<CommentGetResponse> => {
	try {
		const resp = await apiClient.post(`/api/comments/${postId}`, body);

		resp.data.createdAt = convertKRTimeStyle(resp.data.createdAt);

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export const requestDeleteCommentAPI = async (commentId: number) => {
	await apiClient.delete(`/api/comments/${commentId}`);
};

export const requestEditingCommentAPI = async (commentId: number, comment: string) => {
	await apiClient.put(`/api/comments/${commentId}`, {comment});
};
