import {useState, useRef, useEffect, useMemo} from "react";

export default function useInfiniteScroll({
	root = null, target, threshold = 1, rootMargin = "0px", targetArray, endPoint = 1,
}) {
	const [count, setCount] = useState(0);
	const currentChild = useRef(null);

	const intersectionObserver = useMemo(() => new IntersectionObserver(
		(entries, observer) => {
			if (target?.current === null) {
				return;
			}
			if (entries[0].isIntersecting) {
				setCount(v => v + 1);
				observer.disconnect();
			}
		},
	), [target, root, rootMargin, threshold]);

	useEffect(() => {
		if (target?.current === null) {
			return;
		}

		const observeChild = target.current.children[target.current.children.length - endPoint];

		if (observeChild && currentChild.current !== observeChild) {
			currentChild.current = observeChild;
			intersectionObserver.observe(observeChild);
		}

		return () => {
			if (target.current !== null && intersectionObserver) {
				intersectionObserver.unobserve(target.current);
			}
		};
	}, [count, targetArray, target, endPoint]);

	return {
		count,
		setCount,
	};
}
