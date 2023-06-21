import {MenuItemHrefs} from "@/types";
import {Typography} from "antd";
import Link from "next/link";

const {Text} = Typography;

interface ItemProps {
	index: number;
	value: string;
}

export default function MenuItem({index, value}: ItemProps) {
	return (
		<Link href={MenuItemHrefs[index]}><Text>{value}</Text></Link>
	);
}
