import {faker} from "@faker-js/faker";
import convertKRTimeStyle from "@/utils/time";

import {apiClient} from "./apiClient";

let id = 1;

export async function loadPostAPI(pageNumber) {
	try {
		const resp = await apiClient.get(`/posts/newest/${pageNumber}`);
		const posts = Array(20).fill(null)
			.map(() => ({
				"id": id++,
				"thumbnail": faker.image.url(),
				"title": faker.word.verb(10),
				"content": faker.lorem.paragraph(),
				"createdAt": convertKRTimeStyle(faker.date.anytime()),
				"countOfComments": faker.number.int(50),
				"author": faker.person.firstName(),
				"likes": faker.number.int(1000),
				"avatar": faker.image.avatar(),
			}));

		id += 20;
		return posts;
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
