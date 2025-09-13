# 设计系统规范

## 概述

本文档定义了基于 Naive UI 的统一设计系统，包括布局系统、栅格系统、间距规范和组件样式指南。

## 1. 布局系统

### 1.1 容器布局

使用 Naive UI 的 `n-layout` 系列组件构建页面布局：

```vue
<n-layout style="min-height: 100vh">
  <n-layout-header bordered class="navbar">
    <!-- 导航栏内容 -->
  </n-layout-header>
  <n-layout-content style="padding: 20px">
    <!-- 主要内容 -->
  </n-layout-content>
</n-layout>
```

### 1.2 页面容器

所有页面应使用 `PageContainer` 组件包装，确保统一的内边距和响应式行为：

```vue
<PageContainer :padding="'20px'">
  <!-- 页面内容 -->
</PageContainer>
```

## 2. 栅格系统

### 2.1 基础栅格

使用 Naive UI 的 `n-grid` 和 `n-grid-item` 组件：

```vue
<!-- 桌面端 4 列，移动端 2 列 -->
<n-grid :cols="mobileMode ? 2 : 4" :x-gap="16" :y-gap="16">
  <n-grid-item v-for="item in items" :key="item.id">
    <!-- 内容 -->
  </n-grid-item>
</n-grid>
```

### 2.2 响应式栅格

推荐使用 `ResponsiveGrid` 组件实现更精细的响应式控制：

```vue
<ResponsiveGrid 
  :cols="{ xs: 1, sm: 2, md: 3, lg: 4, xl: 5 }"
  :gap="{ xs: 8, sm: 12, md: 16, lg: 20, xl: 24 }"
>
  <ResponsiveGridItem v-for="item in items" :key="item.id">
    <!-- 内容 -->
  </ResponsiveGridItem>
</ResponsiveGrid>
```

## 3. 间距规范

### 3.1 间距标准

采用 4px 基础单位的间距系统：

- **4px**: 最小间距，用于紧密相关的元素
- **8px**: 小间距，用于相关元素之间
- **12px**: 中小间距，用于组件内部元素
- **16px**: 标准间距，用于组件之间
- **20px**: 中等间距，用于区块之间
- **24px**: 大间距，用于主要区域分隔
- **32px**: 超大间距，用于页面级分隔
- **48px**: 特大间距，用于重要内容强调

### 3.2 内边距 (Padding)

#### 容器内边距
- **页面容器**: `20px`
- **卡片容器**: `16px` - `24px`
- **小组件**: `8px` - `12px`
- **按钮**: `8px 12px` - `12px 16px`

#### 响应式内边距
```css
/* 桌面端 */
.container {
  padding: 20px 24px;
}

/* 平板端 */
@media (max-width: 768px) {
  .container {
    padding: 16px 20px;
  }
}

/* 移动端 */
@media (max-width: 480px) {
  .container {
    padding: 12px 16px;
  }
}
```

### 3.3 外边距 (Margin)

#### 组件间距
- **相关组件**: `8px` - `12px`
- **独立组件**: `16px` - `20px`
- **区块分隔**: `24px` - `32px`

#### 垂直间距
```css
.section + .section {
  margin-top: 24px;
}

.component + .component {
  margin-top: 16px;
}
```

### 3.4 间隙 (Gap)

使用 Naive UI 的 `n-space` 组件或 CSS Grid/Flexbox 的 gap 属性：

```vue
<!-- 使用 n-space -->
<n-space vertical size="large">
  <div>内容1</div>
  <div>内容2</div>
</n-space>

<!-- 使用 CSS Grid -->
<div style="display: grid; gap: 16px;">
  <div>内容1</div>
  <div>内容2</div>
</div>
```

## 4. 响应式断点

### 4.1 断点定义

```typescript
export const BREAKPOINTS = {
  xs: 0,     // 超小屏幕 (手机竖屏)
  sm: 576,   // 小屏幕 (手机横屏)
  md: 768,   // 中等屏幕 (平板)
  lg: 992,   // 大屏幕 (桌面)
  xl: 1200,  // 超大屏幕 (大桌面)
  xxl: 1600  // 超超大屏幕
} as const
```

### 4.2 媒体查询

```css
/* 移动端优先 */
.component {
  /* 基础样式 (xs) */
}

@media (min-width: 576px) {
  .component {
    /* sm 及以上 */
  }
}

@media (min-width: 768px) {
  .component {
    /* md 及以上 */
  }
}

@media (min-width: 992px) {
  .component {
    /* lg 及以上 */
  }
}
```

## 5. 组件样式规范

### 5.1 按钮样式

```vue
<template>
  <n-button 
    :type="type" 
    :size="size"
    :style="{
      borderRadius: '6px',
      padding: size === 'small' ? '6px 12px' : '8px 16px'
    }"
  >
    {{ text }}
  </n-button>
</template>
```

### 5.2 卡片样式

```vue
<template>
  <n-card 
    :bordered="true"
    :style="{
      borderRadius: '8px',
      padding: '16px'
    }"
  >
    <!-- 卡片内容 -->
  </n-card>
</template>
```

### 5.3 表单样式

```vue
<template>
  <n-form :model="form" :rules="rules">
    <n-form-item label="标签" path="field">
      <n-input 
        v-model:value="form.field"
        :style="{ borderRadius: '6px' }"
      />
    </n-form-item>
  </n-form>
</template>
```

## 6. 主题配置

### 6.1 颜色系统

遵循 Naive UI 的主题色彩系统：

- **主色**: `--n-color-primary`
- **成功色**: `--n-color-success`
- **警告色**: `--n-color-warning`
- **错误色**: `--n-color-error`
- **信息色**: `--n-color-info`

### 6.2 文字颜色

- **主要文字**: `--n-text-color`
- **次要文字**: `--n-text-color-2`
- **禁用文字**: `--n-text-color-3`

### 6.3 背景颜色

- **页面背景**: `--n-color-body`
- **卡片背景**: `--n-color-card`
- **悬浮背景**: `--n-color-hover`

## 7. 最佳实践

### 7.1 布局原则

1. **移动端优先**: 先设计移动端布局，再适配桌面端
2. **语义化布局**: 使用合适的 HTML 语义标签和 Naive UI 组件
3. **一致性**: 保持间距、字体、颜色的一致性
4. **可访问性**: 确保良好的键盘导航和屏幕阅读器支持

### 7.2 性能优化

1. **避免深层嵌套**: 减少不必要的 DOM 层级
2. **合理使用 v-show/v-if**: 根据场景选择合适的条件渲染
3. **懒加载**: 对大列表和图片使用懒加载

### 7.3 代码规范

1. **组件命名**: 使用 PascalCase 命名组件
2. **样式作用域**: 使用 scoped 样式避免样式污染
3. **CSS 变量**: 优先使用 Naive UI 提供的 CSS 变量

## 8. 检查清单

在开发新组件或页面时，请确保：

- [ ] 使用了正确的 Naive UI 布局组件
- [ ] 间距符合 4px 基础单位规范
- [ ] 实现了响应式设计
- [ ] 使用了统一的颜色和字体
- [ ] 组件样式与主题保持一致
- [ ] 代码遵循项目规范
- [ ] 通过了可访问性测试

## 9. 常见问题

### Q: 如何选择合适的间距？
A: 遵循 4px 基础单位，相关元素使用较小间距（8px-12px），独立元素使用较大间距（16px-24px）。

### Q: 什么时候使用 n-grid vs ResponsiveGrid？
A: 简单的等宽布局使用 n-grid，需要精细响应式控制时使用 ResponsiveGrid。

### Q: 如何确保主题一致性？
A: 优先使用 Naive UI 的 CSS 变量，避免硬编码颜色值。

---

*本文档会根据项目需求持续更新，请定期查看最新版本。*