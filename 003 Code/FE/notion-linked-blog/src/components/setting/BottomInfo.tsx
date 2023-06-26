import {Space} from "antd";
import styled from "styled-components";
import BlogInfo from "./bottom/BlogInfo";
import SocialInfo from "./bottom/SocialInfo";
import Signout from "./bottom/Signout";
import ButtomInfoItem from "./bottom/BottomInfoItem";

const StyledSpace = styled(Space)`
	width: 100%;
	margin-top: 64px;

	@media screen and (max-width: 768px) {
		margin: 0;
	}
`;

const infos = {
	"블로그 제목": <BlogInfo />,
	"소셜 정보": <SocialInfo />,
	"회원 탈퇴": <Signout />,
};

const description = [
	"개인 페이지의 좌측 상단에 나타나는 페이지 제목입니다.",
	"포스트 및 블로그에서 보여지는 프로필에 공개되는 소셜 정보입니다.",
	"탈퇴 시 작성하신 포스트 및 댓글이 모두 삭제되며 복구되지 않습니다.",
];

export default function BottomInfo() {
	return (
		<StyledSpace direction="vertical">
			{Object.entries(infos).map(([title, component], idx) => (
				<ButtomInfoItem title={title} description={description[idx]}>
					{component}
				</ButtomInfoItem>
			))}
		</StyledSpace>
	);
}
