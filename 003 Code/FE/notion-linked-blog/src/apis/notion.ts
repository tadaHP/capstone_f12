import {ParsedUrlQuery} from "querystring";
import apiClient from "./apiClient";

export const getNotionOAuthPage = async () => {
	try {
		return (await apiClient.get("/api/notion/startAuth")).data.authUrl;
	} catch (e) {
		throw e;
	}
};

export const createAccessToken = async (query: ParsedUrlQuery) => {
	try {
		const {code = "", state = "", error = ""} = query;
		const queryParams = new URLSearchParams();

		code && queryParams.append("code", code as string);
		state && queryParams.append("state", state as string);
		error && queryParams.append("error", error as string);

		const url = `/api/notion/auth?${queryParams.toString()}`;

		await apiClient.get(url);
	} catch (e) {
		throw e;
	}
};

export const removeAccessToken = async () => {
	try {
		apiClient.delete("/api/notion");
	} catch (e) {
		throw e;
	}
};

export const addSingleNotionPost = async (path: string) => {
	try {
		await apiClient.post(`/api/notion/single`, {path});
	} catch (e) {
		throw e;
	}
};

export const addMultiNotionPost = async (path: string) => {
	try {
		await apiClient.post(`/api/notion/multi`, {path});
	} catch (e) {
		throw e;
	}
};
