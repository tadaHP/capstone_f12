import {Switch, Typography} from "antd";
import styled from "styled-components";
import {RootState} from "@/redux/store";
import {UserState, modifyNotionCertificate} from "@/redux/userSlice";
import {getNotionOAuthPage, removeAccessToken} from "@/apis/notion";
import {useSelector, useDispatch} from "react-redux";
import {useRouter} from "next/router";
import {NotionState, setError, setLoading} from "@/redux/notionSlice";
import {Container, RowContainer, StyledTitle} from "../Common";

const {Text} = Typography;

const AlignItemsDiv = styled.div`
	display: flex;
	align-items: start;
`;

export default function NotionLinking() {
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const {loading, error} = useSelector<RootState, NotionState>(state => state.notion);
	const errorMsg = "네트워크 에러가 발생했습니다. 잠시 후에 다시 시도해주세요.";
	const dispatch = useDispatch();
	const router = useRouter();

	const handleSwitchOnClick = async () => {
		try {
			dispatch(setLoading(true));
			dispatch(setError(false));

			// 연동 취소 로직
			if (user?.notionCertificate) {
				try {
					await removeAccessToken();
					dispatch(modifyNotionCertificate(false));
					dispatch(setLoading(false));
					return;
				} catch (e) {
					dispatch(setError(true));
				} finally {
					dispatch(setLoading(false));
				}
			}

			const url = await getNotionOAuthPage();

			router.replace(url);
		} catch (e) {
			dispatch(setError(true));
		}
	};

	return (
		<>
			<Container>
				<RowContainer>
					<StyledTitle level={4}>
						노션 연동
					</StyledTitle>
					<AlignItemsDiv>
						<Switch loading={loading} checked={user?.notionCertificate} onClick={handleSwitchOnClick} />
						{error && <Typography.Text type="danger">{errorMsg}</Typography.Text>}
					</AlignItemsDiv>
				</RowContainer>
				<div>
					<Text type="secondary">
						노션 연동 여부를 나타냅니다. 개인 노션에 접근하기 위한 권한을 얻는 절차입니다. <br />
						페이지 선택은 이용에 아무런 영향이 없으므로 액세스 허용만 하시면 됩니다.
					</Text>
				</div>
			</Container>
		</>
	);
}
