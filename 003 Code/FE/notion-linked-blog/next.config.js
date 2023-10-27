/** @type {import('next').NextConfig} */
const Dotenv = require("dotenv-webpack");
const removeImports = require("next-remove-imports");

const nextConfig = removeImports({});

module.exports = nextConfig({
	reactStrictMode: true,
	images: {
		// 데모 데이터의 댓글 작성자 및 로고 이미지를 받아오기 위한 임시 처리
		remotePatterns: [
			{
				protocol: "https",
				hostname: "upload.wikimedia.org",
				port: "",
				pathname: "/wikipedia/commons/4/45/Notion_app_logo.png",
			},
			{
				protocol: "https",
				hostname: "notionlinkedblog.s3.ap-northeast-2.amazonaws.com",
				port: "",
				pathname: "/thumbnail/**",
			},
			{
				protocol: "https",
				hostname: "notionlinkedblog.s3.ap-northeast-2.amazonaws.com",
				port: "",
				pathname: "/profile/**",
			},
		],
	},
	webpack: config => {
		config.plugins.push(new Dotenv({silent: true}));
		return config;
	},
});
