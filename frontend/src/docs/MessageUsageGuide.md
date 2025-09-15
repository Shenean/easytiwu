# 消息提示使用指南

本指南介绍如何在项目中使用统一的消息提示系统。

## 概述

项目已配置了 `createDiscreteApi` 来统一管理消息提示组件，包括：
- `message` - 轻量级消息提示
- `notification` - 通知提示
- `dialog` - 对话框
- `loadingBar` - 加载进度条

## 使用方式

### 1. 在组合式 API 中使用

```vue
<script setup lang="ts">
import { useMessage } from '@/utils/message'

const message = useMessage()

// 基础消息提示
function showSuccess() {
  message.success('操作成功！')
}

function showError() {
  message.error('操作失败！')
}

function showWarning() {
  message.warning('请注意！')
}

function showInfo() {
  message.info('提示信息')
}

// 加载消息
function showLoading() {
  const loading = message.loading('正在处理...', 0) // duration=0 表示不自动关闭
  
  // 3秒后关闭
  setTimeout(() => {
    loading.destroy()
  }, 3000)
}

// 通知提示
function showNotification() {
  message.notifySuccess('成功', '数据已保存')
  message.notifyError('错误', '网络连接失败')
  message.notifyWarning('警告', '磁盘空间不足')
  message.notifyInfo('信息', '有新版本可用')
}

// 确认对话框
async function confirmDelete() {
  const confirmed = await message.confirm(
    '确认删除',
    '此操作不可撤销，确定要删除吗？'
  )
  
  if (confirmed) {
    console.log('用户确认删除')
  }
}

// 加载条
function handleRequest() {
  message.startLoading()
  
  // 模拟异步请求
  fetch('/api/data')
    .then(() => {
      message.finishLoading()
      message.success('请求成功')
    })
    .catch(() => {
      message.errorLoading()
      message.error('请求失败')
    })
}
</script>
```

### 2. 在选项式 API 中使用

```vue
<script>
export default {
  methods: {
    handleSubmit() {
      // 使用全局属性
      this.$message.success('提交成功！')
      this.$notification.info({
        title: '提示',
        content: '数据已保存到服务器'
      })
    },
    
    async confirmAction() {
      const result = await new Promise((resolve) => {
        this.$dialog.warning({
          title: '确认操作',
          content: '确定要执行此操作吗？',
          positiveText: '确定',
          negativeText: '取消',
          onPositiveClick: () => resolve(true),
          onNegativeClick: () => resolve(false)
        })
      })
      
      if (result) {
        console.log('用户确认')
      }
    }
  }
}
</script>
```

### 3. 高级用法

```typescript
// 直接导入原始 API
import { message, notification, dialog, loadingBar } from '@/utils/message'

// 自定义配置
function customMessage() {
  message.create('自定义消息', {
    type: 'success',
    duration: 5000,
    closable: true,
    icon: () => h('i', { class: 'custom-icon' })
  })
}

// 复杂通知
function complexNotification() {
  notification.create({
    title: '系统通知',
    content: '这是一个复杂的通知内容',
    meta: '2024-01-01 12:00:00',
    avatar: () => h('img', { src: '/avatar.png' }),
    action: () => h('button', { onClick: handleAction }, '查看详情'),
    onClose: () => console.log('通知已关闭')
  })
}
```

## 配置说明

### 全局配置

在 `main.ts` 中已配置了以下全局设置：

```typescript
const {message, notification, dialog, loadingBar} = createDiscreteApi(
  ['message', 'notification', 'dialog', 'loadingBar'],
  {
    configProviderProps: {
      theme: null, // 跟随应用主题
      locale: zhCN, // 中文语言包
      dateLocale: dateZhCN // 中文日期格式
    }
  }
)
```

### 主题适配

消息提示组件会自动跟随应用的主题设置（亮色/暗色模式）。

### 国际化

所有消息提示组件都已配置中文语言包，按钮文本等会显示为中文。

## 最佳实践

1. **统一使用工具函数**：优先使用 `useMessage()` 工具函数，而不是直接使用 Naive UI 的 `useMessage()`

2. **合理设置持续时间**：
   - 成功消息：3秒
   - 错误消息：3-5秒
   - 警告消息：4秒
   - 信息消息：3秒
   - 加载消息：根据实际需要设置

3. **错误处理**：在异步操作中正确处理加载状态

```typescript
async function handleAsyncOperation() {
  const loading = message.loading('处理中...', 0)
  
  try {
    await someAsyncOperation()
    loading.destroy()
    message.success('操作成功')
  } catch (error) {
    loading.destroy()
    message.error('操作失败：' + error.message)
  }
}
```

4. **避免重复提示**：在短时间内避免显示相同的消息

5. **用户体验**：重要操作使用确认对话框，普通反馈使用消息提示

## 类型支持

项目已配置完整的 TypeScript 类型支持，在使用时会有完整的类型提示和检查。

## 注意事项

- 消息提示组件是全局单例，无需在组件中重复配置
- 所有消息提示都会自动适配当前主题
- 在服务端渲染（SSR）环境中使用时需要特别注意
- 避免在组件销毁后调用消息提示方法