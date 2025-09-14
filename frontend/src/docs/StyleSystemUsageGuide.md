# 样式系统使用指南

## 概述

本指南介绍如何使用项目中新建立的统一断点常量和颜色系统变量，以提高代码一致性和维护性。

## 断点系统使用

### 断点常量

项目中定义了以下标准断点：

```css
:root {
  --breakpoint-mobile: 480px;    /* 移动端 */
  --breakpoint-tablet: 768px;    /* 平板端 */
  --breakpoint-desktop: 1200px;  /* 桌面端 */
  --breakpoint-large: 1400px;    /* 大屏幕 */
}
```

### 使用方式

#### 1. 使用CSS变量（推荐）

```css
/* 替代硬编码的断点值 */
@media (max-width: var(--breakpoint-mobile)) {
  .component {
    padding: var(--spacing-4);
  }
}

@media (min-width: var(--breakpoint-tablet)) {
  .component {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }
}
```

#### 2. 使用预定义的响应式类

```html
<!-- 响应式显示控制 -->
<div class="mobile-only">仅在移动端显示</div>
<div class="tablet-only">仅在平板端显示</div>
<div class="desktop-up">桌面端及以上显示</div>

<!-- 响应式容器 -->
<div class="responsive-container">
  <!-- 自动适应不同屏幕的内边距和边距 -->
</div>

<!-- 响应式网格 -->
<div class="responsive-grid">
  <!-- 自动适应不同屏幕的列数和间距 -->
</div>
```

## 颜色系统使用

### 颜色变量分类

#### 1. 主题色系
```css
--color-primary: #18a058;           /* 主色调 */
--color-primary-hover: #36ad6a;     /* 悬停状态 */
--color-primary-active: #0c7a43;    /* 激活状态 */
--color-primary-light: #4ade80;     /* 浅色变体 */
```

#### 2. 文本颜色
```css
--color-text-primary: #333333;      /* 主要文本 */
--color-text-secondary: #666666;    /* 次要文本 */
--color-text-tertiary: #999999;     /* 辅助文本 */
--color-text-inverse: #ffffff;      /* 反色文本 */
```

#### 3. 背景颜色
```css
--color-bg-primary: #ffffff;        /* 主背景 */
--color-bg-secondary: #f8f9fa;      /* 次背景 */
--color-bg-tertiary: #f1f3f4;       /* 辅助背景 */
```

#### 4. 边框颜色
```css
--color-border-primary: #e0e0e0;    /* 主边框 */
--color-border-secondary: #e8e8e8;  /* 次边框 */
--color-border-focus: var(--color-primary); /* 焦点边框 */
```

#### 5. 状态颜色
```css
--color-success: #52c41a;           /* 成功状态 */
--color-warning: #faad14;           /* 警告状态 */
--color-error: #ff4d4f;             /* 错误状态 */
--color-info: #1890ff;              /* 信息状态 */
```

### 使用方式

#### 1. 在CSS中使用变量

```css
/* 替代硬编码的颜色值 */
.button {
  background-color: var(--color-primary);
  color: var(--color-text-inverse);
  border: 1px solid var(--color-border-primary);
}

.button:hover {
  background-color: var(--color-primary-hover);
}

.text-content {
  color: var(--color-text-primary);
}

.subtitle {
  color: var(--color-text-secondary);
}
```

#### 2. 使用预定义的工具类

```html
<!-- 文本颜色 -->
<p class="text-primary">主要文本</p>
<p class="text-secondary">次要文本</p>
<p class="text-success">成功状态文本</p>
<p class="text-error">错误状态文本</p>

<!-- 背景颜色 -->
<div class="bg-primary">主背景</div>
<div class="bg-secondary">次背景</div>
<div class="bg-success">成功状态背景</div>

<!-- 边框颜色 -->
<div class="border-primary">主边框</div>
<div class="border-success">成功状态边框</div>

<!-- 主题色相关 -->
<span class="theme-primary">主题色文本</span>
<button class="theme-primary-bg">主题色按钮</button>
```

## 深色模式支持

颜色系统自动支持深色模式，有两种激活方式：

### 1. 手动切换
```html
<!-- 设置data-theme属性 -->
<html data-theme="dark">
```

### 2. 自动检测系统偏好
```css
/* 系统偏好深色模式时自动应用 */
@media (prefers-color-scheme: dark) {
  /* 深色模式样式自动生效 */
}
```

## 迁移指南

### 替换硬编码断点

**之前：**
```css
@media (max-width: 768px) {
  .component { padding: 16px; }
}
```

**之后：**
```css
@media (max-width: var(--breakpoint-tablet)) {
  .component { padding: var(--spacing-4); }
}
```

### 替换硬编码颜色

**之前：**
```css
.button {
  background-color: #18a058;
  color: #333;
  border: 1px solid #e0e0e0;
}
```

**之后：**
```css
.button {
  background-color: var(--color-primary);
  color: var(--color-text-primary);
  border: 1px solid var(--color-border-primary);
}
```

## 最佳实践

### 1. 优先使用变量
- 始终使用CSS变量而非硬编码值
- 保持颜色和断点的一致性

### 2. 语义化命名
- 使用语义化的变量名（如 `--color-text-primary` 而非 `--color-dark-gray`）
- 便于主题切换和维护

### 3. 响应式设计
- 使用统一的断点系统
- 利用预定义的响应式类减少重复代码

### 4. 可访问性
- 颜色系统支持高对比度模式
- 自动适配用户的系统偏好设置

### 5. 性能优化
- CSS变量支持浏览器原生优化
- 减少重复的样式定义

## 文件引入

确保在主样式文件中正确引入：

```css
/* global.css */
@import './breakpoints.css';
@import './colors.css';
@import './spacing-constants.css';
```

## 工具支持

### VS Code 插件推荐
- CSS Variable Autocomplete - 自动补全CSS变量
- Color Highlight - 高亮显示颜色值

### 开发工具
- 浏览器开发者工具可以实时查看和修改CSS变量值
- 支持深色模式的实时切换测试

---

通过使用统一的断点和颜色系统，项目的样式代码将更加一致、易维护，并且能够更好地支持主题切换和响应式设计。