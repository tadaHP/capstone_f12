import {createSlice} from "@reduxjs/toolkit";
import {faker} from "@faker-js/faker";
import convertKRTimeStyle from "@/utils/time";

let id = 1;

const initialState = {
	mainPosts: Array(20).fill(null)
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
		})),
};

const postSlice = createSlice({
	name: "post",
	initialState,
	reducers: {
		/* TODO: 포스트 관련 액션 처리 */
		addPost: (state, action) => {
		},
		removePost: (state, action) => {
		},
		loadPost: (state, action) => {
		},
	},
});

const {actions, reducer: postReducer} = postSlice;

export const {addPost, loadPost, removePost} = actions;

export default postReducer;
