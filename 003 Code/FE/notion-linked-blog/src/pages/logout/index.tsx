import {logoutAPI} from "@/apis/user";
import {logout} from "@/redux/userSlice";
import {useRouter} from "next/router";
import {useEffect} from "react";
import {useDispatch} from "react-redux";

const Logout = () => {
	const dispatch = useDispatch();
	const router = useRouter();

	useEffect(() => {
		(async function() {
			await logoutAPI();
			dispatch(logout());
			await router.replace("/");
		})();
	}, []);
};

export default Logout;
