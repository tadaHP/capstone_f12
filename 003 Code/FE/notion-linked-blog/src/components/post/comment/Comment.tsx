import Image from "next/image";
import {Button, Form, Input, Space, Typography} from "antd";
import {MinusSquareOutlined, PlusSquareOutlined} from "@ant-design/icons";
import styled from "styled-components";
import {useSelector} from "react-redux";
import {UserState} from "@/redux/userSlice";
import {RootState} from "@/redux/store";
import {CSSProperties, useMemo, useState} from "react";
import {requestDeleteCommentAPI, requestEditingCommentAPI} from "@/apis/comment";
import ReplyForm from "./ReplyForm";
import Reply from "./Reply";

const {TextArea} = Input;

const Container = styled.div`
  border-top: 0.8px solid rgb(205, 205, 205);
  padding: 1.5rem;

  @media screen and (max-width: 768px) {
    padding: 1rem;
  }
`;

const StyledParagraph = styled(Typography.Paragraph)`
  font-size: 1.125rem;

  @media screen and (max-width: 768px) {
    font-size: 1rem;
  }
`;

const StyledText = styled(Typography.Text)`
  font-size: 1rem;

  @media screen and (max-width: 768px) {
    font-size: 0.875rem;
  }

  :hover {
    cursor: pointer;
  }
`;

const CommentWriterDetail = styled.div`
  display: flex;
  flex-direction: row;
  margin-bottom: 1.5rem;
`;

const CommentWriterAvatar = styled(Image)`
  @media screen and (max-width: 768px) {
    width: 40px;
    height: 40px;
  }
`;

const CommentWritingInfo = styled.div`
  display: flex;
  flex-direction: column;
  margin-left: 1rem;
`;

const CommentWriter = styled(Typography.Text)`
  font-size: 1rem;
  font-weight: bold;

  @media screen and (max-width: 768px) {
    font-size: 0.875rem;
  }
`;

const CommentModifier = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-left: auto;

  span {
    color: #868E96;
    font-size: 0.875rem;
    cursor: pointer;
    margin-left: 0.5rem;
  }
`;

const FlexEndBtnSpace = styled(Space)`
	width: 100%;
	justify-content: flex-end;
`;

const MarginLeftBtn = styled(Button)`
	margin-left: 24px;
	width: 100%;
`;

export default function Comment({values, postId, handleDeleting, handleEditing}) {
	const {comment, commentId, createdAt, author, authorId, authorProfileLink, children} = values;
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const [replies, setReplies] = useState(children || []);
	const [isOpenEditForm, setIsOpenEditForm] = useState(false);
	const [isOpenReplyForm, setIsOpenReplyForm] = useState(false);
	const [isOpenSecondReplyForm, setIsOpenSecondReplyForm] = useState(false);
	const [isOpenReplyList, setIsOpenReplyList] = useState(false);

	const textAreaStyle: CSSProperties = useMemo(() => ({
		fontSize: "1rem",
		resize: "none",
		padding: "1rem",
	}), []);

	const handleEditCancel = () => {
		setIsOpenEditForm(false);
	};

	const handleReplyEditing = async (replyCommentId: number, value: string) => {
		await requestEditingCommentAPI(replyCommentId, value);
		setReplies(prev =>
			prev.map(reply => (reply.commentId === replyCommentId ? {...reply, comment: value} : reply)),
		);
	};

	const handleReplyDeleting = async (replyId: number) => {
		await requestDeleteCommentAPI(replyId);
		setReplies(prev => prev.filter(reply => reply.commentId !== replyId));
	};

	return (
		<Container>
			<CommentWriterDetail>
				<CommentWriterAvatar
					src={authorProfileLink}
					width={54}
					height={54}
					alt="CommentWriterAvatar"
				/>
				<CommentWritingInfo>
					<CommentWriter>{author}</CommentWriter>
					<Typography.Text>{createdAt}</Typography.Text>
				</CommentWritingInfo>
				{user?.id === authorId &&
					<CommentModifier>
						{!isOpenEditForm && <span onClick={() => setIsOpenEditForm(true)}>수정</span>}
						<span onClick={() => handleDeleting(commentId)}>삭제</span>
					</CommentModifier>
				}
			</CommentWriterDetail>
			{!isOpenEditForm ?
				<StyledParagraph>{comment}</StyledParagraph> :
				<Form
					onFinish={({value}) => {
						handleEditing(commentId, value);
						setIsOpenEditForm(false);
					}}>
					<Form.Item
						name="value"
						initialValue={comment}
						rules={[{required: true, message: "댓글을 작성해주세요!"}]}>
						<TextArea style={textAreaStyle} rows={3} placeholder="댓글을 작성하세요" />
					</Form.Item>
					<Form.Item>
						<FlexEndBtnSpace>
							<Button onClick={handleEditCancel}>취소</Button>
							<Button type="primary" htmlType="submit">수정하기</Button>
						</FlexEndBtnSpace>
					</Form.Item>
				</Form>
			}
			{replies && replies.length > 0 &&
				<StyledText strong onClick={() => {
					if (isOpenReplyList) {
						setIsOpenReplyList(false);
					} else {
						setIsOpenReplyList(true);
					}
				}}>{!isOpenReplyList ?
						<>
							<PlusSquareOutlined /> {replies.length}개의 답글
						</> :
						<>
							<MinusSquareOutlined /> 숨기기
						</>}
				</StyledText>
			}
			{(!replies || replies.length === 0) &&
				<StyledText strong onClick={() => {
					if (isOpenReplyForm) {
						setIsOpenReplyForm(false);
					} else {
						setIsOpenReplyForm(true);
					}
				}}>{!isOpenReplyForm ?
						<>
							<PlusSquareOutlined /> 답글 달기
						</> :
						<>
							<MinusSquareOutlined /> 숨기기
						</>}
				</StyledText>
			}
			{replies && replies.length > 0 && isOpenReplyList &&
				<>
					{replies.map(reply => (
						<Reply
							key={reply.commentId}
							values={reply}
							postId={postId}
							handleDeleting={handleReplyDeleting}
							handleEditing={handleReplyEditing} />
					))}
					{!isOpenSecondReplyForm &&
						<MarginLeftBtn type="primary" onClick={() => setIsOpenSecondReplyForm(true)}>답글 달기</MarginLeftBtn>}
					{isOpenSecondReplyForm &&
						<ReplyForm
							postId={postId}
							parentCommentId={commentId}
							setIsOpenReplyForm={setIsOpenSecondReplyForm}
							setReplies={setReplies} />}
				</>
			}
			{isOpenReplyForm &&
				<ReplyForm
					postId={postId}
					parentCommentId={commentId}
					setIsOpenReplyForm={setIsOpenReplyForm}
					setReplies={setReplies} />
			}
		</Container>
	);
}
