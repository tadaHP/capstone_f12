import {Layout} from "antd";

import styled from "styled-components";

const {Content} = Layout;

const StyledContent = styled(Content)`
	display: flex;
	justify-content: center;
	margin: 0 64px;
`;

export default function AppLayout({children}) {
	return (
		<Layout>
			<StyledContent>
				{children}
			</StyledContent>
		</Layout>
	);
}
