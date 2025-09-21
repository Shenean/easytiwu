module.exports = {
  plugins: {

    // PostCSS Nested - 支持嵌套语法
    'postcss-nested': {},
    
    // PostCSS Custom Properties - CSS 变量支持
    'postcss-custom-properties': {
      preserve: true // 保留原始 CSS 变量
    },
    
    // PostCSS Import - 支持 @import 语法
    'postcss-import': {
      path: ['src/styles']
    },
    
    // Autoprefixer - 自动添加浏览器前缀
    autoprefixer: {
      overrideBrowserslist: [
        '> 1%',
        'last 2 versions',
        'not dead',
        'not ie 11'
      ]
    },
    
    // 生产环境优化
    ...(process.env.NODE_ENV === 'production' ? {
      // CSSnano - CSS 压缩优化
      cssnano: {
        preset: ['default', {
          discardComments: {
            removeAll: true
          },
          normalizeWhitespace: true,
          mergeLonghand: true,
          mergeRules: true
        }]
      }
    } : {})
  }
};

/*
配置说明：

1. postcss-mixins:
   - 核心插件，支持 @mixin 和 @define-mixin 语法
   - mixinsFiles: 指定 mixin 文件的搜索路径
   - mixinsDir: mixin 文件目录

2. postcss-nested:
   - 支持 CSS 嵌套语法
   - 与 mixin 配合使用，提供更好的代码组织

3. postcss-custom-properties:
   - CSS 变量支持
   - preserve: true 保留原始变量，确保运行时动态修改

4. postcss-import:
   - 支持 @import 语法
   - 自动解析和内联导入的 CSS 文件

5. autoprefixer:
   - 自动添加浏览器前缀
   - 确保跨浏览器兼容性

6. cssnano (生产环境):
   - CSS 压缩和优化
   - 移除注释、合并规则、压缩空白

使用方法：
1. 安装依赖：npm install postcss-mixins postcss-nested postcss-custom-properties postcss-import autoprefixer cssnano
2. 在 Vue 组件中使用：@import '@/styles/mixins/index.css'
3. 使用 mixin：@mixin mobile { ... }
*/