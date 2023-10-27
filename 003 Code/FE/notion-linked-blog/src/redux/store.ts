import {combineReducers, configureStore} from "@reduxjs/toolkit";
import {HYDRATE, createWrapper} from "next-redux-wrapper";
import userReducer from "@/redux/userSlice";
import notionReducer from "./notionSlice";

const rootReducer = (state, action) => {
	switch (action.type) {
		case HYDRATE:
			return action.payload;
		default: {
			const combineReducer = combineReducers({
				user: userReducer,
				notion: notionReducer,
			});

			return combineReducer(state, action);
		}
	}
};

const store = configureStore({reducer: rootReducer});

const wrapper = createWrapper(() => store);

export type RootState = ReturnType<typeof store.getState>;

export default wrapper;
