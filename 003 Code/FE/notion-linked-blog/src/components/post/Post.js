import styled from "styled-components";
import {Button, Space} from "antd";

export const CoverDiv = styled.div`
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
`;

export const UploaderDiv = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const WriteDiv = styled.div`
	display: flex;
	flex-direction: column;
	margin: 30px;
`;

export const TempButton = styled(Button)`
	margin: 10px;
`;

export const ButtonSpace = styled(Space)`
	display: flex;
	justify-content: space-between;
  width: 50%;
`;
