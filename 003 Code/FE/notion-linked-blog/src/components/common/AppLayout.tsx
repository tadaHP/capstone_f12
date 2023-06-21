import {Layout} from "antd";

import styled from "styled-components";

const {Content} = Layout;

const StyledLayout = styled(Layout)`
	display: flex;
	flex-direction: row;
	justify-content: center;
	width: 100%;
`;

const StyledContent = styled(Content)`
	display: flex;
	justify-content: center;
	margin: 0 64px;
`;

export default function AppLayout({children}) {
	return (
		<StyledLayout>
			<StyledContent>
				{children}
			</StyledContent>
		</StyledLayout>
	);
}
