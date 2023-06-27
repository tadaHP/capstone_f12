import {Space} from "antd";
import styled from "styled-components";
import Profile from "./top/Profile";
import Introduction from "./top/Introduction";

const StyledSpace = styled(Space)`
	width: 100%;

	& .ant-space-item {
		height: 100%;
	}

	@media screen and (max-width: 768px){
		flex-direction: column;
	}
`;

export default function TopInfo() {
	return (
		<StyledSpace>
			<Profile />
			<Introduction />
		</StyledSpace>
	);
}
