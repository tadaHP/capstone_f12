import {combineReducers, configureStore} from "@reduxjs/toolkit";
import {HYDRATE, createWrapper} from "next-redux-wrapper";
import userReducer from "@/redux/userSlice";
import postReducer from "@/redux/postSlice";

const rootReducer = (state, action) => {
	switch (action.type) {
		case HYDRATE:
			return action.payload;
		default: {
			const combineReducer = combineReducers({
				user: userReducer,
				post: postReducer,
			});

			return combineReducer(state, action);
		}
	}
};

const createStore = () => configureStore({reducer: rootReducer});

const wrapper = createWrapper(createStore);

export default wrapper;
