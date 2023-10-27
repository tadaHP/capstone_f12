import {Space, Typography} from "antd";
import Link from "next/link";
import styled from "styled-components";

const PostsTabTitle = styled(Typography.Title)`
	width: 128px;
 	height: 48px;
  text-align: center;
	border-bottom: ${props => props.title === "글" && "3px solid black"};
`;

const SeriesTabTitle = styled(Typography.Title)`
	width: 128px;
 	height: 48px;
  text-align: center;
	border-bottom: ${props => props.title === "시리즈" && "3px solid black"};
`;

export default function Navigation({username, selected}) {
	return (
		<div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
			<Space align="center">
				<Link href={`/${username}`}>
					<PostsTabTitle level={4} title={selected}>글</PostsTabTitle>
				</Link>
				<Link href={`/${username}/series`}>
					<SeriesTabTitle level={4} title={selected}>시리즈</SeriesTabTitle>
				</Link>
			</Space>
		</div >
	);
}
