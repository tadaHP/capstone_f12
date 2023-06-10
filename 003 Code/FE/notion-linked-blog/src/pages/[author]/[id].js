import {getPostByIdAPI} from "@/apis/post";
import AppLayout from "@/components/common/AppLayout";
import PostViewer from "@/components/post/PostViewer";
import {useRouter} from "next/router";
import {useState, useEffect} from "react";
import convertKRTimeStyle from "@/utils/time";

export default function Page() {
	const router = useRouter();
	const [post, setPost] = useState({});

	useEffect(() => {
		if (router.isReady) {
			(async function() {
				const params = router.query;

				const receivedPost = await getPostByIdAPI(params.id);

				receivedPost.createdAt = convertKRTimeStyle(receivedPost.createdAt);

				setPost(receivedPost);
			})();
		}
	}, [router.isReady, post]);

	return (
		<AppLayout>
			<PostViewer post={post} />
		</AppLayout>
	);
}
