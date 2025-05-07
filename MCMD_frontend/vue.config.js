module.exports = {
  transpileDependencies: ['vuetify'],
  lintOnSave: false,
  devServer: {
    port: 8080, // 改回默认端口
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
    client: {
      overlay: false // 禁用错误遮罩，提高性能
    }
  },
  // 优化构建性能
  productionSourceMap: false,
  // 配置 Webpack
  configureWebpack: {
    optimization: {
      splitChunks: {
        chunks: 'all',
        minSize: 20000
      }
    },
    resolve: {
      fallback: {
        crypto: false
      }
    }
  }
}
