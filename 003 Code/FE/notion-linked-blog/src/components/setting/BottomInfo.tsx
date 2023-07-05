import {Space} from "antd";
import styled from "styled-components";
import BlogInfo from "./bottom/BlogInfo";
import SocialInfo from "./bottom/SocialInfo";
import Signout from "./bottom/Signout";

const StyledSpace = styled(Space)`
	width: 100%;
	margin-top: 64px;

	@media screen and (max-width: 768px) {
		margin: 0;
	}
`;

export default function BottomInfo() {
	return (
		<StyledSpace direction="vertical">
			<BlogInfo />
			<SocialInfo />
			<Signout />
		</StyledSpace>
	);
}
