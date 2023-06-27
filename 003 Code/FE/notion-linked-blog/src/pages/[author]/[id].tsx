import {useState, useEffect} from "react";
import {useRouter} from "next/router";
import {Space} from "antd";
import {getPostByIdAPI} from "@/apis/post";
import AppLayout from "@/components/common/AppLayout";
import PostViewer from "@/components/post/PostViewer";
import CommentContainer from "@/components/post/comment/CommentContainer";
import convertKRTimeStyle from "@/utils/time";

export default function Page() {
	const router = useRouter();
	const [post, setPost] = useState({});

	useEffect(() => {
		if (router.isReady) {
			(async function() {
				const params = router.query;

				const receivedPost = await getPostByIdAPI(params.id as string);

				receivedPost.createdAt = convertKRTimeStyle(receivedPost.createdAt);

				setPost(receivedPost);
			})();
		}
	}, [router.isReady]);

	return (
		<AppLayout>
			<Space direction="vertical" align="center">
				<PostViewer post={post} />
				<CommentContainer />
			</Space>
		</AppLayout>
	);
}
