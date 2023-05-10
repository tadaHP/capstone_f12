import {createSlice} from "@reduxjs/toolkit";

const initialState = {
	user: null,
};

const userSlice = createSlice({
	name: "user",
	initialState,
	reducers: {
		login: (state, action) => {
			state.user = action.payload;
		},
		logout: state => {
			state.user = null;
		},
	},
});

const {actions, reducer: userReducer} = userSlice;

export const {login, logout} = actions;

export default userReducer;
