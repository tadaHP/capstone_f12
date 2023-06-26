import {Button, Form, Input} from "antd";
import {CSSProperties, useMemo} from "react";
import styled from "styled-components";

const {TextArea} = Input;

const StyledFormItem = styled(Form.Item)`
	text-align: right;
`;

export default function CommentForm() {
	const textAreaStyle: CSSProperties = useMemo(() => ({
		fontSize: "1rem",
		resize: "none",
		padding: "1rem",
	}), []);

	return (
		<Form>
			<Form.Item
				name="comment">
				<TextArea style={textAreaStyle} rows={3} placeholder="댓글을 작성하세요" />
			</Form.Item>
			<StyledFormItem>
				<Button type="primary" htmlType="submit">
					댓글 작성
				</Button>
			</StyledFormItem>
		</Form>
	);
}
