import {useEffect} from "react";
import {useRouter} from "next/router";
import {createAccessToken} from "@/apis/notion";
import {useDispatch} from "react-redux";
import {setError, setLoading} from "@/redux/notionSlice";
import {modifyNotionCertificate} from "@/redux/userSlice";

export default function OAuthCode() {
	const dispatch = useDispatch();
	const router = useRouter();

	useEffect(() => {
		const fetch = async () => {
			try {
				const query = router.query;

				await createAccessToken(query);
				dispatch(modifyNotionCertificate(true));
			} catch (e) {
				dispatch(setError(true));
			} finally {
				dispatch(setLoading(false));
			}
		};

		if (router.isReady) {
			fetch();
			router.replace("/setting");
		}
	}, [router.isReady]);

	return <div />;
}
