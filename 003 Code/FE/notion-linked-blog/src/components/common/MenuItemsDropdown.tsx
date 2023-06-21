import {Dropdown, Space} from "antd";
import {DownOutlined, UserOutlined} from "@ant-design/icons";
import styled from "styled-components";

import {MenuItemNames} from "@/types";
import MenuItem from "./MenuItem";

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
	const menuItemValues = Object.values(MenuItemNames);

	const items = menuItemValues.map((menuItemValue, index) => ({
		label: <MenuItem index={index} value={menuItemValue} />,
		key: index,
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
