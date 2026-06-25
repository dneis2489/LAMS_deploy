const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const webpack = require("webpack");

const apiBaseUrl = process.env.API_BASE_URL || "";

module.exports = (_env, argv) => ({
  entry: path.resolve(__dirname, "src/main.tsx"),
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "assets/[name].[contenthash].js",
    chunkFilename: "assets/[name].[contenthash].js",
    publicPath: "/",
    clean: true
  },
  devtool: argv.mode === "production" ? false : "eval-cheap-module-source-map",
  resolve: {
    extensions: [".tsx", ".ts", ".js"]
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: "ts-loader",
        exclude: /node_modules/
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"]
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, "index.html")
    }),
    new webpack.DefinePlugin({
      "process.env.API_BASE_URL": JSON.stringify(apiBaseUrl)
    })
  ],
  optimization: {
    runtimeChunk: "single",
    splitChunks: {
      chunks: "all"
    }
  },
  devServer: {
    port: 3000,
    host: "0.0.0.0",
    historyApiFallback: true,
    hot: true,
    proxy: [
      {
        context: ["/lams"],
        target: "http://localhost:8080",
        changeOrigin: true
      }
    ]
  },
  performance: {
    hints: false
  }
});
