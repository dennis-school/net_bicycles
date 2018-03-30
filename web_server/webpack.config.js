var webpack = require('webpack');
const path = require( 'path' );
const { TsConfigPathsPlugin } = require('awesome-typescript-loader');

var PROD = JSON.parse(process.env.PROD_ENV || '0');

module.exports = [ {
    mode: 'development',
    entry: './src/user_client/index.ts',
    output: {
        path: path.resolve( __dirname, 'public_html/script' ),
        filename: 'script.js'
    },
    resolve: {
        // Add '.ts' and '.tsx' as a resolvable extension.
        extensions: ['.webpack.js', '.web.js', '.ts', '.tsx', '.js'],
        plugins: [
            new TsConfigPathsPlugin( )
        ]
    },
    module: {
        rules: [
            // all files with a '.ts' or '.tsx' extension will be handled by 'ts-loader'
            { test: /\.tsx?$/
            , use: 'ts-loader'
            }
        ]
    },
    node: {
      util: false
    }
}, {
    mode: 'development',
    entry: './src/admin_client/index.ts',
    output: {
        path: path.resolve( __dirname, 'public_html/script' ),
        filename: 'admin_script.js'
    },
    resolve: {
        // Add '.ts' and '.tsx' as a resolvable extension.
        extensions: ['.webpack.js', '.web.js', '.ts', '.tsx', '.js'],
        plugins: [
            new TsConfigPathsPlugin( )
        ]
    },
    module: {
        rules: [
            // all files with a '.ts' or '.tsx' extension will be handled by 'ts-loader'
            { test: /\.tsx?$/
            , use: 'ts-loader'
            }
        ]
    },
    node: {
      util: false
    }
} ]
