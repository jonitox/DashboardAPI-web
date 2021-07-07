const webpack = require('webpack');
const fs = require('fs');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

const CONSTANTS = require('./constants');

const getDomain = () => {
	let devDomain = 'localhost';
	try {
		devDomain = fs.readFileSync(`${CONSTANTS.SERVICE_PATH.root }/webpack-dev-server-public-domain.txt`, 'utf8');
		if (devDomain === '') {
			throw new Error('no serch domain string');
		}

		console.info('\x1b[33m%s\x1b[0m', `devserver domain : ${devDomain}`);
	} catch (err) {
		console.error('\x1b[31m%s\x1b[0m', err.message);
		console.error('\x1b[31m%s\x1b[0m', 'Run initWebpackDevServerPublicDomain task ');
	}

	return devDomain;
};

module.exports = {
	// Chrome browser에서 bundling하기 전 상태의 source file들을 볼 수 있게 디버깅하기 쉽게
	// devtool: 'inline-source-map', // Node.js 앞단 bundling한 파일 caching
	mode: 'development',
	/*
	 *
	 * webpack-dev-server의 기본 설정입니다.
	 * 실제 프로젝트에서 webpack-dev-server를 실행하려면 프로젝트 아래에서 dependency 설치 후 사용하세요.
	 *
	 * - npm install webpack-dev-server@1.16.5 --save-dev
	 *
	 */
	devServer: {
		historyApiFallback: true,
		disableHostCheck: true,
		host: '0.0.0.0',
		public: getDomain(),
		port: 3000,
		proxy: {
			'**': 'http://localhost:8080',
		},
	},
	plugins: [
		/*
		 * 이 플러그인은 HMR이 활성화되어있을 때 모듈의 상대 경로를 표시합니다.
		 * 개발에 사용할 것을 제안합니다.
		 */
		new webpack.NamedModulesPlugin()
	],
	optimization: {
		minimize: false
	}
};
