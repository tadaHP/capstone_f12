export default function convertKRTimeStyle(time) {
	let result = "";

	result += `${time.getFullYear()}년 `;
	result += `${time.getMonth() + 1}월 `;
	result += `${time.getDate()}일`;
	return result;
}
