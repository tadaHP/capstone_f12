import {Input, Typography} from "antd";
import styled from "styled-components";

const {Title} = Typography;

export const Container = styled.div`
	display: flex;
	flex-direction: column;
	width: 100%;
	padding: 1rem 0;
	border-top: 0.8px solid rgb(205, 205, 205);
	gap: 0.5rem;
`;

export const RowContainer = styled.div`
	width: 100%;
	display: flex;
	flex-direction: row;
`;

export const SpaceBetweenContainer = styled.div`
	width: 100%;
	display: flex;
	justify-content: space-between;
`;

export const StyledTitle = styled(Title)`
	width: 150px;
	margin: 0;
`;

export const EditBtn = styled.button`
	border: none;
	background: none;
	padding: 0;
	height: fit-content;
	cursor: pointer;
	text-decoration: underline;
	font-size: 1rem;
	color: rgb(22, 119, 255);
	transition: color 100ms linear;

	:hover {
		color: rgb(64, 150, 255);
	}
`;

export interface StyledInputProps {
	type?: "normal" | "large";
}

export const StyledInput = styled(Input) <StyledInputProps>`
	width: 100%;
	padding: 0.5rem;
	font-size: ${props => (props.type === "large" ? "1.5rem" : "1rem")};
	font-weight: ${props => props.type === "large" && "600"};
`;
