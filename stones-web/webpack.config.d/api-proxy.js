
config.devServer = Object.assign(
    {},
    config.devServer || {},
    { proxy: {
            '/api': {
                target: 'http://localhost:3000',
                pathRewrite: {'^/api' : ''}
            }
        }
    }
)
