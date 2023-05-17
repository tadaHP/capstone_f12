import {Card, Typography} from "antd";
import {HeartFilled} from "@ant-design/icons";

const {Meta} = Card;

export default function PostCard({post}) {
	return (
		<Card
			hoverable
			bordered={false}
			style={{width: "320px", minWidth: "320px"}}
			cover={<img height="170px" alt="example" src={post.thumbnail}/>}
			actions={
				[
					<Typography.Text>by
						<Typography.Text strong={true}> {post.author}</Typography.Text>
					</Typography.Text>, <Typography.Text><HeartFilled/> {post.likes}</Typography.Text>,
				]}
		>
			<Meta
				title={post.title}
				description={(
					<>
						<Typography.Paragraph
							ellipsis={{rows: 3}}>{post.content}</Typography.Paragraph>
						<Typography.Text>{post.createdAt}</Typography.Text>
						<Typography.Text> · </Typography.Text>
						<Typography.Text>{post.countOfComments}개의 댓글</Typography.Text>
					</>
				)}
				style={{width: "100%"}}/>
		</Card>
	);
}
