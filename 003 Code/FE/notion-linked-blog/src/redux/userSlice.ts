import {PayloadAction, createSlice} from "@reduxjs/toolkit";
import {ModifyingSocialInfo, ModifyingBasicInfo, User} from "@/apis/user";

export interface UserState {
	user: User;
}

const initialState: UserState = {
	user: null,
};

const userSlice = createSlice({
	name: "user",
	initialState,
	reducers: {
		login: (state, action: PayloadAction<User>) => {
			state.user = action.payload;
		},
		logout: state => {
			state.user = null;
		},
		modifyBlogTitle: (state, action: PayloadAction<string>) => {
			state.user.blogTitle = action.payload;
		},
		modifySocialInfo: (state, action: PayloadAction<ModifyingSocialInfo>) => {
			state.user.githubLink = action.payload.githubLink;
			state.user.instagramLink = action.payload.instagramLink;
		},
		modifyBasicInfo: (state, action: PayloadAction<ModifyingBasicInfo>) => {
			state.user.username = action.payload.username;
			state.user.introduction = action.payload.introduction;
		},
		modifyProfileImage: (state, action: PayloadAction<string>) => {
			state.user.profile = action.payload;
		},
	},
});

const {actions, reducer: userReducer} = userSlice;

export const {
	login,
	logout,
	modifyBlogTitle,
	modifySocialInfo,
	modifyBasicInfo,
	modifyProfileImage,
} = actions;

export default userReducer;
