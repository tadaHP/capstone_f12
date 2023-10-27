import {useEffect, useState} from "react";
import {Spin, Typography} from "antd";
import styled from "styled-components";
import {CommentGetResponse, requestDeleteCommentAPI, requestEditingCommentAPI, requestGetCommentAPI} from "@/apis/comment";

import CommentForm from "./CommentForm";
import Comment from "./Comment";

const StyledSection = styled.section`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 768px;

  @media screen and (max-width: 768px) {
    width: 100vw;
    padding: 0 1rem;
  }
`;

const StyledSpin = styled(Spin)`
  margin: 64px 0;
`;

interface CommentContainerProps {
	postId: number;
}

export default function CommentContainer({postId}: CommentContainerProps) {
	const [loading, setLoading] = useState(false);
	const [comments, setComments] = useState<CommentGetResponse[]>([]);

	const handleComments = (comment: CommentGetResponse) => {
		setComments(prev => prev.concat(comment));
	};

	const handleClickDeletingBtn = async (commentId: number) => {
		await requestDeleteCommentAPI(commentId);
		setComments(prev => prev.filter(comment => comment.commentId !== commentId));
	};

	const handleClickEditingBtn = async (commentId: number, comment: string) => {
		await requestEditingCommentAPI(commentId, comment);
		setComments(prev =>
			prev.map(cmt => (cmt.commentId === commentId ? {...cmt, comment} : cmt)),
		);
	};

	useEffect(() => {
		async function getComments() {
			try {
				setLoading(true);
				const receivedComments = await requestGetCommentAPI(postId);

				setComments(receivedComments);
			} catch (e) {
				throw new Error(e);
			} finally {
				setLoading(false);
			}
		}

		if (postId) {
			getComments();
		}
	}, [postId]);

	return (
		<>
			{loading ?
				<StyledSpin size="large" tip="Loading" /> :
				<StyledSection>
					<Typography.Title level={4}>{comments.length}개의 댓글</Typography.Title>
					<CommentForm postId={postId} handleComments={handleComments} />
					{comments.map(comment => (
						<Comment
							key={comment.commentId}
							values={comment}
							postId={postId}
							handleDeleting={handleClickDeletingBtn}
							handleEditing={handleClickEditingBtn} />
					))}
				</StyledSection>}
		</>
	);
}
