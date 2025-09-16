/**
 * 消息提示工具函数模块
 * 
 * 基于 Naive UI 的消息系统封装，提供统一的消息提示、通知、对话框和加载条功能。
 * 支持国际化，所有消息都可以通过 i18n 进行多语言处理。
 * 
 * @module MessageUtils
 * @author EasyTiWu Team
 * @since 1.0.0
 */

import {dialog, loadingBar, message, notification} from '../main'
import {i18n} from '../i18n'

/** 国际化翻译函数的快捷引用 */
const t = i18n.global.t

/**
 * 消息提示工具 Hook
 * 
 * 提供便捷的方法在组合式 API 中使用各种消息提示功能。
 * 所有方法都基于 Naive UI 的离散式 API，确保在任何组件中都能正常使用。
 * 
 * @returns {Object} 包含各种消息提示方法的对象
 * 
 * @example
 * ```typescript
 * import { useMessage } from '@/utils/message'
 * 
 * const { success, error, notifySuccess } = useMessage()
 * 
 * // 显示成功消息
 * success('操作成功！')
 * 
 * // 显示错误消息，自定义持续时间
 * error('操作失败！', 5000)
 * 
 * // 显示成功通知
 * notifySuccess('上传完成', '文件已成功上传到服务器')
 * ```
 */
export const useMessage = () => {
  return {
    /**
     * 显示成功消息
     * @param {string} content - 消息内容
     * @param {number} [duration=3000] - 显示持续时间（毫秒），0表示不自动关闭
     */
    success: (content: string, duration = 3000) => {
      message.success(content, {duration})
    },
    
    /**
     * 显示错误消息
     * @param {string} content - 消息内容
     * @param {number} [duration=3000] - 显示持续时间（毫秒），0表示不自动关闭
     */
    error: (content: string, duration = 3000) => {
      message.error(content, {duration})
    },
    
    /**
     * 显示警告消息
     * @param {string} content - 消息内容
     * @param {number} [duration=3000] - 显示持续时间（毫秒），0表示不自动关闭
     */
    warning: (content: string, duration = 3000) => {
      message.warning(content, {duration})
    },
    
    /**
     * 显示信息消息
     * @param {string} content - 消息内容
     * @param {number} [duration=3000] - 显示持续时间（毫秒），0表示不自动关闭
     */
    info: (content: string, duration = 3000) => {
      message.info(content, {duration})
    },
    
    /**
     * 显示加载消息
     * @param {string} content - 消息内容
     * @param {number} [duration=0] - 显示持续时间（毫秒），0表示不自动关闭
     * @returns {MessageReactive} 消息实例，可用于手动关闭
     */
    loading: (content: string, duration = 0) => {
      return message.loading(content, {duration})
    },
    
    
    // ==================== 通知提示方法 ====================
    
    /**
     * 显示成功通知
     * 通知比消息更显眼，适合重要操作的反馈
     * @param {string} title - 通知标题
     * @param {string} [content] - 通知内容（可选）
     * @param {number} [duration=4500] - 显示持续时间（毫秒），0表示不自动关闭
     */
    notifySuccess: (title: string, content?: string, duration = 4500) => {
      notification.success({
        title,
        content,
        duration
      })
    },
    
    /**
     * 显示错误通知
     * @param {string} title - 通知标题
     * @param {string} [content] - 通知内容（可选）
     * @param {number} [duration=4500] - 显示持续时间（毫秒），0表示不自动关闭
     */
    notifyError: (title: string, content?: string, duration = 4500) => {
      notification.error({
        title,
        content,
        duration
      })
    },
    
    /**
     * 显示警告通知
     * @param {string} title - 通知标题
     * @param {string} [content] - 通知内容（可选）
     * @param {number} [duration=4500] - 显示持续时间（毫秒），0表示不自动关闭
     */
    notifyWarning: (title: string, content?: string, duration = 4500) => {
      notification.warning({
        title,
        content,
        duration
      })
    },
    
    /**
     * 显示信息通知
     * @param {string} title - 通知标题
     * @param {string} [content] - 通知内容（可选）
     * @param {number} [duration=4500] - 显示持续时间（毫秒），0表示不自动关闭
     */
    notifyInfo: (title: string, content?: string, duration = 4500) => {
      notification.info({
        title,
        content,
        duration
      })
    },
    
    // ==================== 对话框方法 ====================
    
    /**
     * 显示确认对话框
     * 用于需要用户确认的重要操作，如删除、提交等
     * 
     * @param {string} title - 对话框标题
     * @param {string} [content] - 对话框内容（可选）
     * @returns {Promise<boolean>} Promise，resolve为true表示用户点击确认，false表示取消
     * 
     * @example
     * ```typescript
     * const { confirm } = useMessage()
     * 
     * const handleDelete = async () => {
     *   const confirmed = await confirm('删除确认', '确定要删除这个项目吗？')
     *   if (confirmed) {
     *     // 执行删除操作
     *   }
     * }
     * ```
     */
    confirm: (title: string, content?: string) => {
      return new Promise<boolean>((resolve) => {
        dialog.warning({
          title,
          content,
          positiveText: t('common.confirm'),
          negativeText: t('common.cancel'),
          onPositiveClick: () => {
            resolve(true)
          },
          onNegativeClick: () => {
            resolve(false)
          }
        })
      })
    },
    
    // ==================== 加载条方法 ====================
    
    /**
     * 开始显示加载条
     * 通常在页面路由切换或长时间操作开始时调用
     */
    startLoading: () => {
      loadingBar.start()
    },
    
    /**
     * 完成加载条显示
     * 在操作成功完成时调用
     */
    finishLoading: () => {
      loadingBar.finish()
    },
    
    /**
     * 显示加载条错误状态
     * 在操作失败时调用
     */
    errorLoading: () => {
      loadingBar.error()
    }
  }
}

/**
 * 直接导出原始 API，供高级用法使用
 */
/**
 * 导出 Naive UI 的离散式 API 实例
 * 
 * 这些实例可以在任何地方直接使用，不需要在组件上下文中。
 * 适合在工具函数、路由守卫、API 拦截器等非组件环境中使用。
 * 
 * @example
 * ```typescript
 * import { message, notification } from '@/utils/message'
 * 
 * // 在 API 拦截器中使用
 * axios.interceptors.response.use(
 *   response => response,
 *   error => {
 *     message.error('请求失败')
 *     return Promise.reject(error)
 *   }
 * )
 * ```
 */
export {message, notification, dialog, loadingBar}