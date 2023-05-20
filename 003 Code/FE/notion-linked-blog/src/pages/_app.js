import {Provider} from "react-redux";

import store from "@/redux/store";
import {createGlobalStyle} from "styled-components";

const GlobalStyles = createGlobalStyle`
	html,
	body,
	div#__next {
		height: 100%;
	}

	html,
	body {
		margin: 0;
		padding: 0;
	}
`;

export default function App({Component, pageProps}) {
	return (
		<Provider store={store}>
			<GlobalStyles />
			<Component {...pageProps} />
		</Provider>
	);
}
