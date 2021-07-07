const webpackMerge = require('webpack-merge');
const CONSTANTS = require('./constants');

const config = {
	entry: {
		// js
		app: '/app.js',
	},
	optimization: {
		splitChunks: {
			cacheGroups: {
				lib: {
					enforce: true
				},
				common: {
					test: /[\\/](node_modules|lib|sass|css)[\\/].*\.(js|css|scss)/,
					chunks: 'all',
					name: 'common',
					enforce: true,
				},
				app: {
					test: /\.vue/,
					chunks: 'all',
					name: 'app',
					enforce: true,
				}
			},
		},
	}
};

module.exports = webpackMerge(CONSTANTS.addConstantsServicePath2Entry(config), require('./webpack.default'));