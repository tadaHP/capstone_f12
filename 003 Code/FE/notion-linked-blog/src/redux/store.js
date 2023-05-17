import {combineReducers, configureStore} from "@reduxjs/toolkit";
import userReducer from "@/redux/userSlice";
import postReducer from "@/redux/postSlice";

const rootReducer = combineReducers({
	user: userReducer,
	post: postReducer,
});

const store = configureStore({reducer: rootReducer});

export default store;
