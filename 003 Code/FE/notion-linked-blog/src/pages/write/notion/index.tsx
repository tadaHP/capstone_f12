import {addMultiNotionPost, addSingleNotionPost} from "@/apis/notion";
import {Button, Form, Input, Radio, RadioChangeEvent, Space, Typography} from "antd";
import {useRouter} from "next/router";
import {useState} from "react";

export default function WritingWithNotion() {
	const [isSingle, setIsSingle] = useState(0);
	const [isLoading, setIsLoading] = useState(false);
	const [isAlreadyLinked, setIsAlreadyLinked] = useState(false);
	const [isNetworkError, setIsNetworkError] = useState(false);
	const [isNotLinkedNotion, setIsNotLinkedNotion] = useState(false);
	const [path, setPath] = useState("");
	const router = useRouter();

	const handleFinish = async () => {
		try {
			setIsAlreadyLinked(false);
			setIsNetworkError(false);
			setIsNotLinkedNotion(false);
			setIsLoading(true);
			isSingle === 0 ? await addSingleNotionPost(path) : await addMultiNotionPost(path);

			router.replace("/");
		} catch (e) {
			if (e.response.data.errorCode === 400) {
				setIsAlreadyLinked(true);
			} else if (e.response.data.errorCode === 500) {
				setIsNetworkError(true);
			} else if (e.response.data.errorCode === 1000) {
				setIsNotLinkedNotion(true);
			}
		} finally {
			setIsLoading(false);
		}
	};

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setPath(e.target.value);
	};

	const handleRadioChange = (e: RadioChangeEvent) => {
		setIsSingle(e.target.value);
	};

	return (
		<div style={{display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", marginTop: 100}}>
			<Form
				onFinish={handleFinish}
				style={{width: 768}}>
				<Form.Item>
					<Input value={path} onChange={handleInputChange} placeholder="연동할 노션 페이지의 링크를 입력해주세요" />
					{isAlreadyLinked && <Typography.Text strong style={{color: "#ff0000"}}>이미 연동된 페이지입니다!</Typography.Text>}
					{isNetworkError && <Typography.Text strong style={{color: "#ff0000"}}>네트워크 에러입니다!</Typography.Text>}
					{isNotLinkedNotion && <Typography.Text strong style={{color: "#ff0000"}}>설정 탭에서 노션 연동을 먼저 해주세요!</Typography.Text>}
				</Form.Item>
				<Form.Item>
					<Radio.Group onChange={handleRadioChange} value={isSingle}>
						<Radio value={0}>단일 페이지</Radio>
						<Radio value={1}>다중 페이지</Radio>
					</Radio.Group>
				</Form.Item>
				<Typography.Paragraph>
					* 단일 페이지는 페이지 하나를 불러옵니다. <br />
					* 다중 페이지는 해당 페이지 및 하위 페이지를 모두 불러옵니다.
					상위 페이지는 시리즈로, 하위 페이지들은 포스트로 추가됩니다. 부모 페이지에 존재하는 데이터는 page 블록과 제목만 등록처리 됩니다.
					* 다중 페이지의 하위 페이지 그 이하의 페이지들은 무시됩니다.
				</Typography.Paragraph>
				<Form.Item style={{display: "flex", justifyContent: "flex-end"}}>
					<Space>
						<Button onClick={() => router.replace("/")}>나가기</Button>
						<Button type="primary" htmlType="submit" loading={isLoading}>연동하기</Button>
					</Space>
				</Form.Item>
			</Form>
		</div>
	);
}
