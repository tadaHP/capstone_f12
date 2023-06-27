import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";
import {Typography} from "antd";
import {useSelector} from "react-redux";

export default function BlogInfo() {
	const {user} = useSelector<RootState, UserState>(state => state.user);

	return (
		<Typography.Text>{user?.blogTitle}</Typography.Text>
	);
}
