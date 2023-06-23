import {signoutAPI} from "@/apis/user";
import {RootState} from "@/redux/store";
import {UserState, logout} from "@/redux/userSlice";
import {Button, Modal} from "antd";
import {useRouter} from "next/router";
import {useState} from "react";
import {useDispatch, useSelector} from "react-redux";

export default function Signout() {
	const {user} = useSelector<RootState, UserState>(state => state.user);
	const [loading, setLoading] = useState(false);
	// TODO: 예외 처리 작업
	// const [error, setError] = useState("");
	const router = useRouter();
	const dispatch = useDispatch();
	const [isModalOpen, setIsModalOpen] = useState(false);

	const openModal = () => {
		setIsModalOpen(true);
	};

	const handleOk = async () => {
		try {
			setLoading(true);
			await signoutAPI(user.id);
			dispatch(logout());
			setIsModalOpen(false);
			await router.replace("/");
		} catch (e) {
			// setError(e.message);
		} finally {
			setLoading(false);
		}
	};

	const handleCancel = () => {
		setIsModalOpen(false);
	};

	return (
		<>
			<Button type="primary" danger onClick={openModal} loading={loading}>
				회원 탈퇴
			</Button>
			<Modal title="회원 탈퇴" open={isModalOpen} closable={false}
				footer={[
					<Button onClick={handleCancel}>취소</Button>,
					<Button key="submit" type="primary" danger loading={loading} onClick={handleOk}>
						확인
					</Button>,
				]}>
				정말 탈퇴하시겠습니까?
			</Modal>
		</>
	);
}
