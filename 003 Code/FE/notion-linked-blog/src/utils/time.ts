export default function convertKRTimeStyle(time) {
	let result = "";

	const date = new Date(time);

	result += `${date.getFullYear()}년 `;
	result += `${date.getMonth() + 1}월 `;
	result += `${date.getDate()}일`;
	return result;
}
