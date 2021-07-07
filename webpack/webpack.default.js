const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CleanWebpackPlugin = require('clean-webpack-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');

const CONSTANTS = require('./constants');

const config = {
	resolve: {
		modules: [CONSTANTS.SERVICE_PATH.resource + '/vue', 'node_modules'],
		extensions: ['.js', '.vue']
	},
	module: {
		// JS 번들링하면서 각 test 정규식에 해당하는 파일들을 찾아 맞는 로더를 실행시킨다. (Webpack 4 부터는 loader 가 없어지고 rules)
		rules: [
			{
				test: /\.vue$/,
				loader: 'vue-loader',
			},
			{
				test: /\.css$/,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader'
				]
			},
			{
				test: /\.js$/,
				loader: 'babel-loader',
				exclude: /(node_modules)/
			},
			{
				test: /\.(png|jpg|gif|svg)$/,
				loader: 'file-loader',
				options: {
					name: '[name].[ext]?[hash]'
				}
			}
		]
	},
	plugins: [
		new VueLoaderPlugin(),
		new CleanWebpackPlugin(['dist'], {
			root: CONSTANTS.SERVICE_PATH.resource
		}),
		new webpack.ProvidePlugin({
			$: 'jquery',
			jQuery: 'jquery',
			_: 'lodash',
			moment: 'moment',
		}),
		// webpack 4 부터는 ExtractTextPlugin 대신 MiniCssExtractPlugin을 써야함.
		new MiniCssExtractPlugin({
			filename: '[name].merged.css'
		})
	],
	// 번들링된 파일 관련 설정
	output: {
		path: CONSTANTS.SERVICE_PATH.dist,
		publicPath: CONSTANTS.WEBPACK_CONFIG.publicPath,
		filename: '[name].merged.js',
	},
};

module.exports = webpackMerge(config, require(`./webpack.${process.env.NODE_ENV}.js`));
