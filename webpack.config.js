const path = require('path');
const webpack = require('webpack');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const ChunkManifestPlugin = require('chunk-manifest-webpack-plugin');
const WebpackChunkHash = require('webpack-chunk-hash');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

const config = {
    context: path.resolve(__dirname),
	entry: {
		app: './src/index.js',
		vendor: ['vue', 'vue-router'],
	},
    output: {
        path: path.resolve(__dirname, 'dist'),
        publicPath: '/',
	    filename: '[name].[hash].js',
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                loader: 'vue-loader',
                options: {
                    loaders: {
                        'scss': [
                            'vue-style-loader',
                            'css-loader',
                            'sass-loader'
                        ],
                        'sass': [
                            'vue-style-loader',
                            'css-loader',
                            'sass-loader?indentedSyntax'
                        ]
                    }
                }
            },
            {
		        test: /\.(jpg|jpeg|gif|png|svg|woff|woff2)$/,
		        use: {
			        loader: 'file-loader',
			        options: { name: '[name].[hash].[ext]' },
		        },
	        },
            {
                test: /\.js$/,
                loader: 'babel-loader',
                exclude: /node_modules/
            }
        ],
        loaders: [
            {
                test: /\.css$/,
                loaders: ['style', 'css']
            },
            {
                test: /\.json$/,
                loaders: ["json-loader"]
            },
            {
                test: /\.less$/,
                loaders: ['style', 'css', 'less']
            },
            {
                test: /\.scss$/,
                loaders: ['style', 'css', 'sass']
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: 'babel-loader'
            },
        ]
    },
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        }
    },
    plugins: [
	    new webpack.optimize.UglifyJsPlugin({
		    mangle: true,
		    compress: {
			    warnings: false, // Suppress uglification warnings
			    pure_getters: true,
			    unsafe: true,
			    unsafe_comps: true,
			    screw_ie8: true
		    },
		    output: {
			    comments: false,
		    },
		    exclude: [/\.min\.js$/gi] // skip pre-minified libs
	    }),
	    new webpack.optimize.ModuleConcatenationPlugin(),
	    new ExtractTextPlugin('style.css', {allChunks: true}),
	    new HtmlWebpackPlugin({
	        inlineManifestWebpackName: 'webpackManifest',
            template: './index.html',
	    }),
	    new webpack.optimize.CommonsChunkPlugin({
		    name: 'vendor',
	    }),
    ],
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        },
        extensions: ['*', '.js', '.vue', '.json']
    },
    devServer: {
        historyApiFallback: true,
        noInfo: true,
        overlay: true
    },
    performance: {
        hints: false
    },
    devtool: '#eval-source-map'
};

/* Production */

if (process.env.NODE_ENV === 'production') {
	config.output.filename = '[name].[chunkhash].js';
	config.plugins = [
		...config.plugins, // ES6 array destructuring, available in Node 5+
		new webpack.HashedModuleIdsPlugin(),
		new WebpackChunkHash(),
		new ChunkManifestPlugin({
			filename: 'chunk-manifest.json',
			manifestVariable: 'webpackManifest',
			inlineManifest: true,
		}),
	];
}

/* bundle analyzing */

if (process.env.NODE_ENV === 'debug') {
	config.plugins = [
		...config.plugins,
		new BundleAnalyzerPlugin({
			generateStatsFile: true,
			statsFilename: 'webpack_build_analysis.json',
			openAnalyzer: false
		}),
	];
}

module.exports = config;