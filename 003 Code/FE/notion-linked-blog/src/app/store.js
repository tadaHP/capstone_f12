import counterReducer from "@/features/counter/counterSlice";
import {configureStore} from "@reduxjs/toolkit";

const store = configureStore({reducer: {counter: counterReducer}});

export default store;
