/**
 * 全局类型声明文件
 * 
 * 本文件用于扩展 Vue 的全局类型定义，为组件实例添加 Naive UI 的离散式 API 类型支持。
 * 通过模块声明合并的方式，让 TypeScript 能够正确识别组件实例上的全局属性类型。
 * 
 * ## 功能说明
 * - 🔧 **类型扩展**: 扩展 Vue 组件实例的类型定义
 * - 📝 **智能提示**: 提供完整的 TypeScript 智能提示和类型检查
 * - 🛡️ **类型安全**: 确保全局 API 调用的类型安全性
 * - 🔗 **API 集成**: 集成 Naive UI 的消息、通知、对话框和加载条 API
 * 
 * @fileoverview Vue 全局属性类型扩展
 * @author EasyTiWu Team
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 在 Vue 组件中使用（Options API）
 * export default {
 *   methods: {
 *     showMessage() {
 *       // TypeScript 会提供完整的类型提示
 *       this.$message.success('操作成功！')
 *       this.$notification.info({ title: '提示', content: '这是一条通知' })
 *     }
 *   }
 * }
 * 
 * // 在 Composition API 中使用
 * import { getCurrentInstance } from 'vue'
 * 
 * const instance = getCurrentInstance()
 * if (instance) {
 *   instance.proxy?.$message.error('操作失败！')
 * }
 * ```
 */

/**
 * 全局类型定义文件
 * 
 * 用于定义项目中使用的全局类型和接口。
 * 现在项目已迁移到 TDesign，不再需要 Naive UI 相关的类型定义。
 */

/**
 * 确保这个文件被 TypeScript 视为模块
 * 
 * 通过导出空对象，确保这个文件被 TypeScript 编译器识别为模块，
 * 而不是全局脚本文件，这样模块声明合并才能正常工作。
 */
export {}