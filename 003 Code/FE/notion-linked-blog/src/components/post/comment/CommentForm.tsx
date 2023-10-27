import {Button, Form, Input} from "antd";
import {CSSProperties, useMemo, useState} from "react";
import styled from "styled-components";
import {CommentGetResponse, CommentPost, requestAddCommentAPI} from "@/apis/comment";

const {TextArea} = Input;

const StyledFormItem = styled(Form.Item)`
  text-align: right;
`;

interface CommentFormProps {
	postId: number;
	handleComments: (value: CommentGetResponse) => void;
}

export default function CommentForm({postId, handleComments}: CommentFormProps) {
	const [loading, setLoading] = useState(false);
	const [form] = Form.useForm();

	const textAreaStyle: CSSProperties = useMemo(() => ({
		fontSize: "1rem",
		resize: "none",
		padding: "1rem",
	}), []);

	const handleFinish = async (values: any) => {
		const reqBody: CommentPost = {
			comment: values.comment,
			depth: 0,
		};

		try {
			setLoading(true);
			const comment = await requestAddCommentAPI(reqBody, postId);

			handleComments(comment);
			form.resetFields();
		} catch (e) {
			if (e?.response?.status !== undefined) {
				if (e.response.status === 401) {
					// eslint-disable-next-line no-alert
					alert("로그인 후 이용해 주세요!");
				} else {
					console.error(e);
				}
			} else {
				console.error(e);
			}
		} finally {
			setLoading(false);
		}
	};

	return (
		<Form
			form={form}
			onFinish={handleFinish}>
			<Form.Item
				name="comment"
				rules={[{required: true, message: "댓글을 작성해주세요!"}]}>
				<TextArea style={textAreaStyle} rows={3} placeholder="댓글을 작성하세요" />
			</Form.Item>
			<StyledFormItem>
				<Button type="primary" htmlType="submit" loading={loading}>
					댓글 작성
				</Button>
			</StyledFormItem>
		</Form>
	);
}
