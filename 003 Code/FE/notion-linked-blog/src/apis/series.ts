import apiClient from "./apiClient";

export const getPostsInSeries = async (seriesId: number) => {
	try {
		const resp = await apiClient.get(`/api/series/detail/asc/${seriesId}/0`);

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export const addSeries = async (userId: number, seriesTitle: string) => {
	try {
		const resp = await apiClient.post(`/api/series`, {userId, seriesTitle});

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export const editSeriesTitle = async (userId: number, seriesId: number, title: string) => {
	try {
		const resp = await apiClient.put(`/api/series/${seriesId}/title`, {userId, title});

		return resp.data;
	} catch (e) {
		throw e;
	}
};

export const deleteSeries = async (userId: number, seriesId: number) => {
	try {
		const resp = await apiClient.delete(`/api/series`, {data: {userId, seriesId}});

		return resp.data;
	} catch (e) {
		throw e;
	}
};
