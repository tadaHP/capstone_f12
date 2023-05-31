import {combineReducers, configureStore} from "@reduxjs/toolkit";
import {HYDRATE, createWrapper} from "next-redux-wrapper";
import userReducer from "@/redux/userSlice";

const rootReducer = (state, action) => {
	switch (action.type) {
		case HYDRATE:
			return action.payload;
		default: {
			const combineReducer = combineReducers({
				user: userReducer,
			});

			return combineReducer(state, action);
		}
	}
};

const createStore = () => configureStore({reducer: rootReducer});

const wrapper = createWrapper(createStore);

export default wrapper;
