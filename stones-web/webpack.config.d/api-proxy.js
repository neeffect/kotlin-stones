
config.devServer = Object.assign(
    {},
    config.devServer || {},
    { proxy: {
            '/api': {
                target: 'http://localhost:3000',
                pathRewrite: {'^/api' : ''}
            },
            '/sys': {
                target: 'http://localhost:3000'
            }
        }
    }
)


//this is some higher kotlin js fuckup (sometimes needed - sometimes not)
// config.resolve.alias = {
//     'kotlin-extensions': 'kotlin-wrappers-kotlin-extensions-jsLegacy',
//     'kotlin-react': 'kotlin-wrappers-kotlin-react-jsLegacy',
//     'kotlin-styled': 'kotlin-wrappers-kotlin-styled-jsLegacy',
//     'kotlin-wrappers-kotlin-css': 'kotlin-css'
// };
