// 全局类型声明文件
import type {DialogApi, LoadingBarApi, MessageApi, NotificationApi} from 'naive-ui'

// 扩展 Vue 全局属性类型
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $message: MessageApi
    $notification: NotificationApi
    $dialog: DialogApi
    $loadingBar: LoadingBarApi
  }
}

// 确保这个文件被视为模块
export {}