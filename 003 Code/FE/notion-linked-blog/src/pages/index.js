import AppLayout from "@/components/AppLayout";
import Head from "next/head";
import {useAppSelector} from "@/hooks/hooks";

export default function Home() {
	const {mainPosts} = useAppSelector(state => state.post);

	return (
		<>
			<Head>
				<title>노션 연동 블로그 서비스</title>
			</Head>
			<AppLayout mainPosts={mainPosts}/>
		</>
	);
}
