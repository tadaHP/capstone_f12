import axios from "axios";

const {SERVER} = process.env;

export default axios.create({
	baseURL: `${SERVER}`,
	withCredentials: true,
});
