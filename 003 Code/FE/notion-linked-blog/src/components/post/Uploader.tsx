import React, {useEffect, useState} from "react";
import {Button} from "antd";
import styled from "styled-components";

const Wrapper = styled.div`
display: flex;
flex-direction: column;
`;

const PreviewImageDiv = styled.div`
& > img {
	width: 200px;
	height: 200px;
}
`;

const ButtonDiv = styled.div`
display:flex;
flex-direction: column;

& > Button {
	width: 100%;
}
`;

const Uploader = props => {
	const [image, setImage] = useState({
		image_file: props.thumbImage.image_file,
		preview_URL: props.thumbImage.preview_URL,
	});

	let inputRef;

	const saveImage = e => {
		// e.preventDefault();
		if (e.target.files[0]) {
			// 새로운 이미지를 올리면 createObjectURL()을 통해 생성한 기존 URL을 폐기
			URL.revokeObjectURL(image.preview_URL);
			const previewURL = URL.createObjectURL(e.target.files[0]);

			setImage(() => (
				{
					image_file: e.target.files[0],
					preview_URL: previewURL,
				}
			));
		}
	};

	const deleteImage = () => {
		// createObjectURL()을 통해 생성한 기존 URL을 폐기
		URL.revokeObjectURL(image.preview_URL);
		setImage({
			image_file: "",
			preview_URL: "",
		});
	};

	useEffect(() => {
		props.changeThumbImage(image);
	}, [image]);

	useEffect(() => () => {
		URL.revokeObjectURL(image.preview_URL);
	}, []);

	return (
		<Wrapper>
			<input type="file" accept="image/*"
				onChange={saveImage}
				// 클릭할 때 마다 file input의 value를 초기화 하지 않으면 버그가 발생할 수 있다
				// 사진 등록을 두개 띄우고 첫번째에 사진을 올리고 지우고 두번째에 같은 사진을 올리면 그 값이 남아있음!
				onClick={e => (e.currentTarget.value = null)}
				ref={refParam => (inputRef = refParam)}
				style={{display: "none"}}
			/>
			<PreviewImageDiv>
				<img src={image.preview_URL} />
			</PreviewImageDiv>

			<ButtonDiv>
				<Button type="primary" onClick={() => inputRef.click()}>
					이미지 업로드
				</Button>
				<Button color="error" onClick={deleteImage}>
					이미지 제거
				</Button>
			</ButtonDiv>
		</Wrapper>
	);
};

export default Uploader;
