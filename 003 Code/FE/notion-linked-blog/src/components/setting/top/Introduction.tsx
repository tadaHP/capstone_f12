import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";
import {Space, Typography} from "antd";
import styled from "styled-components";

import {useSelector} from "react-redux";

const {Title, Paragraph} = Typography;

const StyledTitle = styled(Title)`
	margin: 0;
`;

const StyledSpace = styled(Space)`
	height: 100%;
	padding-left: 24px;
	flex-direction: column;
	justify-content: start;;
	align-items: start;
	border-left: 0.8px solid rgb(205, 205, 205);
`;

export default function Introduction() {
	const {user} = useSelector<RootState, UserState>(state => state.user);

	return (
		<StyledSpace>
			<StyledTitle>{user?.username}</StyledTitle>
			<Paragraph>{user?.introduction}</Paragraph>
		</StyledSpace>
	);
}
