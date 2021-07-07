const webpack = require('webpack');
const Tenth2Plugin = require('webpack-tenth2-plugin');
const WriteWebpackPlugin = require('write-webpack-plugin');

const CONSTANTS = require('./constants');
const TENTH2 = CONSTANTS.TENTH2;

const branch = require('child_process')
	.execSync('git rev-parse --abbrev-ref HEAD')
	.toString()
	.trim();

const commitHash = require('child_process')
	.execSync('git rev-parse --short HEAD')
	.toString()
	.trim();

const TENTH2_PATH_PREFIX = `/kraken/${
	CONSTANTS.PROJECT_NAME
	}${branch !== 'master' ? `/${ branch}` : ''
	}/${ commitHash}`;

const PUBLIC_PATH = `https://${ TENTH2.downloadHost }/${ TENTH2.id }${TENTH2_PATH_PREFIX}`;

module.exports = {
	plugins: [
		new Tenth2Plugin({
			tenthOptions: {
				serviceId: TENTH2.id,
				writeKey: TENTH2.wkey,
				host: TENTH2.uploadHost,
			},
			uploadOptions: {
				prefix: TENTH2_PATH_PREFIX,
			},
		}),

		// cdnPath.txt 이름 변경이 필요하면 ResourceDistributionConfig 클래스를 참조
		new WriteWebpackPlugin([
			{
				name: 'cdnPath.txt',
				data: Buffer.from(PUBLIC_PATH)
			}
		]),
	],
	output: {
		publicPath: PUBLIC_PATH,
	},
};
