/** @type {import('next').NextConfig} */
const Dotenv = require("dotenv-webpack");
const removeImports = require("next-remove-imports");

const nextConfig = removeImports({
	reactStrictMode: true,
	webpack: config => {
		config.plugins.push(new Dotenv({silent: true}));
		return config;
	},
});

module.exports = nextConfig;
