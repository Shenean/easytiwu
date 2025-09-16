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

import type {DialogApi, LoadingBarApi, MessageApi, NotificationApi} from 'naive-ui'

/**
 * 扩展 Vue 组件实例的全局属性类型
 * 
 * 通过模块声明合并的方式，为 Vue 组件实例添加 Naive UI 离散式 API 的类型定义。
 * 这样在组件中使用 this.$message 等全局属性时，TypeScript 能够提供正确的类型检查和智能提示。
 * 
 * @module @vue/runtime-core
 * @interface ComponentCustomProperties
 * 
 * @property {MessageApi} $message - 消息提示 API，用于显示轻量级的消息提示
 * @property {NotificationApi} $notification - 通知 API，用于显示更显眼的通知消息
 * @property {DialogApi} $dialog - 对话框 API，用于显示确认对话框、信息对话框等
 * @property {LoadingBarApi} $loadingBar - 加载条 API，用于显示页面顶部的加载进度条
 * 
 * @example
 * ```typescript
 * // 在 Vue 组件的方法中使用
 * methods: {
 *   async handleSubmit() {
 *     this.$loadingBar.start()
 *     try {
 *       await submitData()
 *       this.$message.success('提交成功！')
 *       this.$loadingBar.finish()
 *     } catch (error) {
 *       this.$message.error('提交失败！')
 *       this.$loadingBar.error()
 *     }
 *   },
 * 
 *   async handleDelete() {
 *     const confirmed = await new Promise(resolve => {
 *       this.$dialog.warning({
 *         title: '确认删除',
 *         content: '此操作不可撤销，确定要删除吗？',
 *         positiveText: '确定',
 *         negativeText: '取消',
 *         onPositiveClick: () => resolve(true),
 *         onNegativeClick: () => resolve(false)
 *       })
 *     })
 *     
 *     if (confirmed) {
 *       // 执行删除操作
 *     }
 *   }
 * }
 * ```
 */
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    /** 消息提示 API，用于显示成功、错误、警告等轻量级消息 */
    $message: MessageApi
    /** 通知 API，用于显示带标题和内容的通知消息 */
    $notification: NotificationApi
    /** 对话框 API，用于显示确认、警告、信息等对话框 */
    $dialog: DialogApi
    /** 加载条 API，用于显示页面顶部的加载进度条 */
    $loadingBar: LoadingBarApi
  }
}

/**
 * 确保这个文件被 TypeScript 视为模块
 * 
 * 通过导出空对象，确保这个文件被 TypeScript 编译器识别为模块，
 * 而不是全局脚本文件，这样模块声明合并才能正常工作。
 */
export {}