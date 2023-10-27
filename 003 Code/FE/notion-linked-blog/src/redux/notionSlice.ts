import {PayloadAction, createSlice} from "@reduxjs/toolkit";

export interface NotionState {
	loading: boolean;
	error: boolean
}

const initialState: NotionState = {
	loading: false,
	error: false,
};

const NotionSlice = createSlice({
	name: "notion",
	initialState,
	reducers: {
		setLoading: (state, action: PayloadAction<boolean>) => {
			state.loading = action.payload;
		},
		setError: (state, action: PayloadAction<boolean>) => {
			state.error = action.payload;
		},
	},
});

const {actions, reducer: notionReducer} = NotionSlice;

export const {
	setLoading,
	setError,
} = actions;

export default notionReducer;
