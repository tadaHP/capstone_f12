import styled from "styled-components";
import {Space, Typography} from "antd";

const {Text} = Typography;

export const StyledSpace = styled(Space)`
  width: 100%;
`;

export const StyledDiv = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const StyledText = styled(Text)`
  margin-left: auto;
`;
