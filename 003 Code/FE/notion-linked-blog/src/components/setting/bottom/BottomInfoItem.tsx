import {Space, Typography} from "antd";
import styled from "styled-components";
import SettingSubtitle from "./SettingSubtitle";

const StyledSpace = styled(Space)`
	width: 100%;
	padding: 16px 0;
	border-top: 0.8px solid rgb(205, 205, 205);
`;

export default function ButtomInfoItem({children, title, description}) {
	return (
		<StyledSpace direction="vertical">
			<Space>
				<SettingSubtitle title={title} />
				{children}
			</Space>
			<Typography.Text type="secondary">
				{description}
			</Typography.Text>
		</StyledSpace>
	);
}
