import {useAppDispatch, useAppSelector} from "@/app/hooks";
import {increment, decrement} from "@/features/counter/counterSlice";
import {Button, Typography} from "antd";

const {Text} = Typography;

const textStyle = {
	textAlign: "center", padding: "10px",
};

export default function Home() {
	const {value: count} = useAppSelector(state => state.counter);
	const dispatch = useAppDispatch();

	return (
		<>
			<div>
				<Button type="primary" onClick={() => dispatch(increment())}>
					Increment
				</Button>
				<Text style={textStyle}>{count}</Text>
				<Button type="primary" danger onClick={() => dispatch(decrement())}>
					Decrement
				</Button>
			</div>
		</>);
}
