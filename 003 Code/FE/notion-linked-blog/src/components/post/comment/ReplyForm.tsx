import {Button, Form, Input, Space} from "antd";
import {CSSProperties, useMemo, useState} from "react";
import styled from "styled-components";
import {CommentPost, requestAddCommentAPI} from "@/apis/comment";

const {TextArea} = Input;

const StyledFormItem = styled(Form.Item)`
  text-align: right;
`;

const FlexEndBtnSpace = styled(Space)`
	width: 100%;
	justify-content: flex-end;
`;

interface ReplyFormProps {
	postId: number;
	parentCommentId: number;
	setIsOpenReplyForm: (value: boolean) => void;
	setReplies?: any;
}

export default function ReplyForm(
	{postId, parentCommentId, setIsOpenReplyForm, setReplies}: ReplyFormProps) {
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
			depth: 1,
			parentCommentId,
		};

		try {
			setLoading(true);
			const comment = await requestAddCommentAPI(reqBody, postId);

			form.resetFields();
			setIsOpenReplyForm(false);
			setReplies(prev => prev.concat(comment));
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

	const handleReplyCancel = () => {
		setIsOpenReplyForm(false);
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
				<FlexEndBtnSpace>
					<Button onClick={handleReplyCancel}>취소</Button>
					<Button type="primary" htmlType="submit" loading={loading}>댓글 작성</Button>
				</FlexEndBtnSpace>
			</StyledFormItem>
		</Form>
	);
}
