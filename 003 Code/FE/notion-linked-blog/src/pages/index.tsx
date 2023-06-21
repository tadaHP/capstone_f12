import AppLayout from "@/components/common/AppLayout";
import MainPosts from "@/components/post/MainPosts";
import Head from "next/head";

export default function Home() {
	return (
		<>
			<Head>
				<title>노션 연동 블로그 서비스</title>
			</Head>
			<AppLayout>
				<MainPosts />
			</AppLayout>
		</>
	);
}
