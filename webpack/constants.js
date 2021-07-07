/* eslint-disable no-undef */
const path = require('path');

const projectName = `${process.env.PROJECT_NAME}`;

const serviceRootPath = path.resolve(__dirname, '../');
const serviceResourcePath = path.resolve(serviceRootPath, 'src/main/resources/static');

// 서비스 모듈 경로
const SERVICE_PATH = {
	root: serviceRootPath,
	resource: serviceResourcePath,
	js: `${serviceResourcePath}/js`,
	css: `${serviceResourcePath}/css`,
	scss: `${serviceResourcePath}/scss`,
	dist: `${serviceResourcePath}/dist`,
};

// webpack 설정 옵션
const WEBPACK_CONFIG = {
	publicPath: 'dist',
};

// Tenth2 업로드 관련 설정
const TENTH2 = {
	id: 'media',
	wkey: 'w_6171f6551f23cf2f69a6635e766beb',
	uploadHost: 'twg.tset.daumcdn.net',
	downloadHost: 't1.daumcdn.net',
};

const HANDLEBARS_HELPER_FILES = {
	path: `${SERVICE_PATH.js }/common/handlebars-helpers`,
};

// entry에 자동으로 js, css 절대경로를 붙여주는 함수
const addConstantsServicePath2Entry = (config, jsPath, cssPath, scssPath) => {
	jsPath = jsPath || SERVICE_PATH.js;
	cssPath = cssPath || SERVICE_PATH.css;
	scssPath = scssPath || SERVICE_PATH.scss;

	if (!jsPath || !cssPath || !scssPath) throw new Error('addConstantsServicePath2Entry parameter error');

	for (const key in config.entry) {
		const value = config.entry[key];

		if (typeof (value) === 'string') {
			if (value.substr(-3) === '.js' && !value.match(jsPath)) {
				config.entry[key] = jsPath + value;
			} else if (value.substr(-4) === '.css' && !value.match(cssPath)) {
				config.entry[key] = cssPath + value;
			} else if (value.substr(-5) === '.scss' && !value.match(scssPath)) {
				config.entry[key] = scssPath + value;
			}
		} else {

		}
	}

	return config;
};

module.exports = {
	WEBPACK_CONFIG,
	SERVICE_PATH,
	TENTH2,
	HANDLEBARS_HELPER_FILES,
	PROJECT_NAME: projectName,
	addConstantsServicePath2Entry,
};
