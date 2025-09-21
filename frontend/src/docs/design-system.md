# EasyTiWu 设计系统文档

## 概述

EasyTiWu 设计系统是一套完整的设计规范和组件库，旨在确保整个应用的视觉一致性和用户体验的统一性。

## 设计原则

### 1. 一致性 (Consistency)

- 统一的视觉语言和交互模式
- 标准化的组件和布局
- 可预测的用户体验

### 2. 可访问性 (Accessibility)

- 符合 WCAG 2.1 AA 标准
- 支持键盘导航
- 适配屏幕阅读器

### 3. 响应式 (Responsive)

- 移动优先的设计理念
- 流畅的跨设备体验
- 触摸友好的交互

### 4. 性能优化 (Performance)

- 轻量级的样式系统
- 按需加载的组件
- 优化的渲染性能

## 颜色系统

### 主色调 (Primary Colors)

主色调采用蓝色系，传达专业、可信赖的品牌形象。

```css
/* 主色调 - 蓝色系 */
--color-primary-50: #f0f8ff; /* 最浅 */
--color-primary-100: #e0f0ff;
--color-primary-200: #c0e1ff;
--color-primary-300: #80c2ff;
--color-primary-400: #3090ff;
--color-primary-500: #2080f0; /* 基准色 */
--color-primary-600: #1070e0;
--color-primary-700: #0060d0;
--color-primary-800: #0050c0;
--color-primary-900: #0040b0; /* 最深 */
```

**使用场景：**

- Primary-500: 主要按钮、链接、品牌元素
- Primary-600: 按钮悬停状态
- Primary-100: 浅色背景、选中状态

### 语义色彩 (Semantic Colors)

#### 成功色 (Success)

```css
--color-success-500: #18a058; /* 成功状态 */
--color-success-100: #dcfce7; /* 成功背景 */
```

#### 警告色 (Warning)

```css
--color-warning-500: #f0a020; /* 警告状态 */
--color-warning-100: #fef3c7; /* 警告背景 */
```

#### 错误色 (Error)

```css
--color-error-500: #f04040; /* 错误状态 */
--color-error-100: #fee2e2; /* 错误背景 */
```

#### 信息色 (Info)

```css
--color-info-500: #0ea5e9; /* 信息状态 */
--color-info-100: #e0f2fe; /* 信息背景 */
```

### 中性色 (Neutral Colors)

#### 标准灰色系

```css
--color-gray-50: #f9fafb; /* 背景色 */
--color-gray-100: #f3f4f6; /* 浅背景 */
--color-gray-200: #e5e7eb; /* 边框色 */
--color-gray-500: #6b7280; /* 次要文本 */
--color-gray-900: #111827; /* 主要文本 */
```

### 透明度变体

```css
/* 黑色透明度 */
--color-black-05: rgba(0, 0, 0, 0.05); /* 极浅遮罩 */
--color-black-10: rgba(0, 0, 0, 0.1); /* 浅遮罩 */
--color-black-25: rgba(0, 0, 0, 0.25); /* 中等遮罩 */
--color-black-50: rgba(0, 0, 0, 0.5); /* 模态背景 */

/* 白色透明度 */
--color-white-10: rgba(255, 255, 255, 0.1); /* 深色主题遮罩 */
--color-white-90: rgba(255, 255, 255, 0.9); /* 毛玻璃效果 */
```

## 字体系统

### 字体族 (Font Family)

```css
--font-family-sans: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
--font-family-mono: "JetBrains Mono", "Fira Code", Consolas, monospace;
```

### 字体尺寸 (Font Sizes)

```css
--font-size-xs: 12px; /* 辅助信息 */
--font-size-sm: 14px; /* 次要文本 */
--font-size-base: 16px; /* 正文基准 */
--font-size-lg: 18px; /* 小标题 */
--font-size-xl: 20px; /* 中标题 */
--font-size-2xl: 24px; /* 大标题 */
--font-size-3xl: 30px; /* 特大标题 */
```

### 行高 (Line Heights)

```css
--line-height-tight: 1.25; /* 标题 */
--line-height-normal: 1.5; /* 正文 */
--line-height-relaxed: 1.75; /* 长文本 */
```

## 间距系统

### 基础间距 (Base Spacing)

采用 8px 基础单位的间距系统，确保视觉节奏的一致性。

```css
--spacing-base: 8px;

/* 数值间距 */
--spacing-1: 8px; /* 1 * 8px */
--spacing-2: 16px; /* 2 * 8px */
--spacing-3: 24px; /* 3 * 8px */
--spacing-4: 32px; /* 4 * 8px */
--spacing-6: 48px; /* 6 * 8px */
--spacing-8: 64px; /* 8 * 8px */
```

### 语义化间距 (Semantic Spacing)

```css
--spacing-xs: var(--spacing-1); /* 8px - 极小间距 */
--spacing-sm: var(--spacing-2); /* 16px - 小间距 */
--spacing-md: var(--spacing-3); /* 24px - 中等间距 */
--spacing-lg: var(--spacing-4); /* 32px - 大间距 */
--spacing-xl: var(--spacing-6); /* 48px - 超大间距 */
```

### 组件间距 (Component Spacing)

```css
/* 按钮内边距 */
--button-padding-sm: var(--spacing-1) var(--spacing-2); /* 8px 16px */
--button-padding-md: var(--spacing-2) var(--spacing-3); /* 16px 24px */
--button-padding-lg: var(--spacing-2) var(--spacing-4); /* 16px 32px */

/* 卡片内边距 */
--card-padding-sm: var(--spacing-2); /* 16px */
--card-padding-md: var(--spacing-3); /* 24px */
--card-padding-lg: var(--spacing-4); /* 32px */
```

## 圆角系统

```css
--border-radius-xs: 2px; /* 小元素 */
--border-radius-sm: 4px; /* 按钮、输入框 */
--border-radius-md: 6px; /* 卡片 */
--border-radius-lg: 8px; /* 大卡片 */
--border-radius-xl: 12px; /* 模态框 */
--border-radius-full: 9999px; /* 圆形 */
```

## 阴影系统

```css
/* 基础阴影 */
--shadow-xs: 0 1px 2px 0 var(--color-black-05);
--shadow-sm: 0 1px 3px 0 var(--color-black-10);
--shadow-md: 0 4px 6px -1px var(--color-black-10);
--shadow-lg: 0 10px 15px -3px var(--color-black-10);

/* 语义化阴影 */
--shadow-card-light: 0 6px 18px var(--color-black-06); /* 卡片 */
--shadow-card-medium: 0 10px 28px var(--color-black-08); /* 悬浮卡片 */
--shadow-primary: 0 4px 14px 0 rgba(14, 165, 233, 0.15); /* 主色阴影 */
```

## 响应式断点

```css
--breakpoint-xs: 0px; /* 超小屏幕（手机竖屏） */
--breakpoint-sm: 640px; /* 小屏幕（手机横屏） */
--breakpoint-md: 768px; /* 中等屏幕（平板竖屏） */
--breakpoint-lg: 1024px; /* 大屏幕（平板横屏/小桌面） */
--breakpoint-xl: 1280px; /* 超大屏幕（桌面） */
--breakpoint-xxl: 1536px; /* 超超大屏幕（大桌面） */
```

### 媒体查询使用

```css
/* 移动端优先 */
@media (min-width: 768px) {
  /* 平板及以上 */
}

@media (min-width: 1024px) {
  /* 桌面及以上 */
}

/* 特定范围 */
@media (min-width: 768px) and (max-width: 1023px) {
  /* 仅平板 */
}
```

## 组件使用指南

### 按钮 (Buttons)

#### 尺寸规范

```css
/* 按钮高度 */
--button-height-sm: 32px;
--button-height-md: 40px;
--button-height-lg: 48px;

/* 最小宽度 */
--button-min-width: 64px;
```

#### 使用示例

```vue
<!-- 主要按钮 -->
<n-button type="primary" size="medium">
  确认提交
</n-button>

<!-- 次要按钮 -->
<n-button type="default" size="medium">
  取消
</n-button>

<!-- 危险操作 -->
<n-button type="error" size="medium">
  删除
</n-button>
```

### 卡片 (Cards)

#### 使用示例

```vue
<n-card title="卡片标题" :bordered="true" size="medium" hoverable>
  <template #header-extra>
    <n-button size="small">操作</n-button>
  </template>
  
  卡片内容
  
  <template #action>
    <n-space>
      <n-button size="small">取消</n-button>
      <n-button type="primary" size="small">确认</n-button>
    </n-space>
  </template>
</n-card>
```

### 表单 (Forms)

#### 使用示例

```vue
<n-form
  ref="formRef"
  :model="formData"
  :rules="rules"
  label-placement="top"
  size="medium"
>
  <n-form-item label="用户名" path="username">
    <n-input 
      v-model:value="formData.username"
      placeholder="请输入用户名"
    />
  </n-form-item>
  
  <n-form-item label="密码" path="password">
    <n-input 
      v-model:value="formData.password"
      type="password"
      placeholder="请输入密码"
    />
  </n-form-item>
  
  <n-form-item>
    <n-space>
      <n-button @click="handleReset">{{ $t('common.reset') }}</n-button>
      <n-button type="primary" @click="handleSubmit">{{ $t('common.submit') }}</n-button>
    </n-space>
  </n-form-item>
</n-form>
```

## 移动端优化

### 触摸目标尺寸

```css
/* 最小触摸目标 */
--touch-target-min: 44px; /* iOS 推荐 */
--touch-target-comfortable: 48px; /* 舒适尺寸 */
--touch-target-large: 56px; /* 大尺寸 */
```

### 移动端适配

```css
/* 移动端优化类 */
.mobile-optimized {
  /* 底部安全区域适配 */
  padding-bottom: env(safe-area-inset-bottom);
}

/* 触摸友好的按钮 */
@media (max-width: 767px) {
  .n-button {
    min-height: var(--touch-target-min);
    min-width: var(--touch-target-min);
  }
}
```

## 主题系统

### 浅色主题 (Light Theme)

```css
:root {
  --color-background: #ffffff;
  --color-surface: #ffffff;
  --color-text-primary: #2c2c2c;
  --color-text-secondary: #666666;
  --color-border: #d9d9d9;
}
```

### 深色主题 (Dark Theme)

```css
[data-theme="dark"] {
  --color-background: #1e1e1e;
  --color-surface: #1e1e1e;
  --color-text-primary: #f5f5f5;
  --color-text-secondary: #666666;
  --color-border: #404040;
}
```

## 最佳实践

### 1. 使用设计令牌

❌ **不推荐：硬编码值**

```css
.button {
  padding: 12px 24px;
  border-radius: 6px;
  color: #2080f0;
}
```

✅ **推荐：使用设计令牌**

```css
.button {
  padding: var(--spacing-1-5) var(--spacing-3);
  border-radius: var(--border-radius-md);
  color: var(--color-primary-500);
}
```

### 2. 语义化命名

❌ **不推荐：表象命名**

```css
.blue-button {
}
.small-text {
}
.red-border {
}
```

✅ **推荐：语义化命名**

```css
.primary-button {
}
.caption-text {
}
.error-border {
}
```

### 3. 组件复用

❌ **不推荐：重复样式**

```css
.card-1 {
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-2 {
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
```

✅ **推荐：统一组件**

```css
.card {
  padding: var(--card-padding-md);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
}
```

## 工具和资源

### 开发工具

1. **浏览器开发者工具**

   - 检查设计令牌使用情况
   - 调试响应式布局
   - 测试可访问性

2. **设计工具**
   - Figma 设计稿
   - 颜色对比度检查器
   - 字体预览工具

### 参考资源

- [Naive UI 组件库](https://www.naiveui.com/)
- [WCAG 2.1 可访问性指南](https://www.w3.org/WAI/WCAG21/quickref/)
- [Material Design 设计规范](https://material.io/design)
- [Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)

## 更新日志

### v1.0.0 (2024-01-20)

- 初始版本发布
- 完整的颜色系统
- 响应式断点系统
- 移动端优化
- 设计令牌标准化

---

**维护者：** EasyTiWu 开发团队  
**最后更新：** 2024-01-20  
**版本：** 1.0.0
