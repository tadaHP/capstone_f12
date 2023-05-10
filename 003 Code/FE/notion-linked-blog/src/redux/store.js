import counterReducer from "@/redux/counterSlice";
import {configureStore} from "@reduxjs/toolkit";
import userReducer from "@/redux/userSlice";

const store = configureStore({reducer: {user: userReducer}});

export default store;
