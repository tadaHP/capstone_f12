import {Typography} from "antd";
import styled from "styled-components";

const {Title} = Typography;

type SettingSubtitleProps = {
	title: string
}

const StyledTitle = styled(Title)`
	width: 150px;
	margin: 0;
`;

export default function SettingSubtitle({title}: SettingSubtitleProps) {
	return (
		<StyledTitle level={4}>
			{title}
		</StyledTitle>
	);
}
