import styled from "styled-components";
import Profile from "./top/Profile";
import Introduction from "./top/Introduction";

const Container = styled.section`
	display: flex;

	@media screen and (max-width: 768px){
		flex-direction: column;
	}
`;

export default function TopInfo() {
	return (
		<Container>
			<Profile />
			<Introduction />
		</Container>
	);
}
