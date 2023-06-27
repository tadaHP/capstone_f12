import {Space, Typography} from "antd";
import {GithubOutlined, InstagramOutlined} from "@ant-design/icons";
import {useSelector} from "react-redux";
import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";

export default function SocialInfo() {
	const {user} = useSelector<RootState, UserState>(state => state.user);

	return (
		<Space direction="vertical">
			<Typography.Text><GithubOutlined /> {user?.githubLink}</Typography.Text>
			<Typography.Text><InstagramOutlined /> {user?.instagramLink}</Typography.Text>
		</Space>
	);
}
