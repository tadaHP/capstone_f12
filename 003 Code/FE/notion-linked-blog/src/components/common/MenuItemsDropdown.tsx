import Link from "next/link";
import {Dropdown, Space, Typography} from "antd";
import {DownOutlined, UserOutlined} from "@ant-design/icons";
import styled from "styled-components";

import data from "./menuItemData.json";

const {Text} = Typography;

const StyledSpace = styled(Space)`
  cursor: pointer;
`;

const StyledUserOutlined = styled(UserOutlined)`
  cursor: pointer;
`;

const StyledDownOutlined = styled(DownOutlined)`
  cursor: pointer;
`;

export default function MenuItemsDropdown() {
	const items = Object.entries(data).map(([text, href]) => ({
		label: <Link href={href}><Text>{text}</Text></Link>,
		key: href,
	}));

	return (
		<Dropdown menu={{items}} trigger={["click"]}>
			<StyledSpace>
				<StyledUserOutlined />
				<StyledDownOutlined />
			</StyledSpace>
		</Dropdown >
	);
}
