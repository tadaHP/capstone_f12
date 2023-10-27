import styled from "styled-components";
import {Button, Input, Space} from "antd";

export const CoverDiv = styled.div`
  display: flex;
  height: 425px;
  justify-content: center;
  align-items: flex-start;
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

export const StyledInput = styled(Input)`
    font-size: 3rem;
	`;
