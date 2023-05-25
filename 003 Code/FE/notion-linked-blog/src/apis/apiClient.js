import axios from "axios";

const {SERVER} = process.env;

export const apiClient = axios.create({
	baseURL: `${SERVER}`,
	withCredentials: true,
});
