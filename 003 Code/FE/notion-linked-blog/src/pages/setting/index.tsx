import styled from "styled-components";
import AppLayout from "@/components/common/AppLayout";
import TopInfo from "@/components/setting/TopInfo";
import BottomInfo from "@/components/setting/BottomInfo";
import {Space} from "antd";

const StyledSpace = styled(Space)`
	width: 768px;
	margin: 0 42.8px;
	margin-top: 48px;

	@media screen and (max-width: 768px) {
		flex-direction: column;
		width: 100%;
	}
`;

export default function Setting() {
	return (
		<AppLayout>
			<StyledSpace direction="vertical">
				<TopInfo />
				<BottomInfo />
			</StyledSpace>
		</AppLayout>
	);
}
