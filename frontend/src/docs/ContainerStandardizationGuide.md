# 容器标准化使用指南

## 概述

本指南介绍了项目中统一的容器尺寸管理系统，解决了之前存在的间距不一致和硬编码配置问题。

## 🎯 解决的问题

### 1. 间距不一致问题
- **问题**：不同页面的 `PageContainer` 使用了不同的 `padding` 值
- **解决方案**：统一使用 CSS 变量管理所有间距配置

### 2. 最大宽度配置问题
- **问题**：部分页面使用硬编码的 `max-width` 值，缺乏统一标准
- **解决方案**：建立标准的容器宽度规范，提供预设的尺寸选项

### 3. 样式维护困难
- **问题**：硬编码值分散在各个文件中，难以统一修改
- **解决方案**：使用 CSS 变量集中管理，一处修改全局生效

## 📋 容器尺寸规范

### 容器最大宽度

```css
--container-max-width-xs: 480px;   /* 超小容器 */
--container-max-width-sm: 768px;   /* 小容器 */
--container-max-width-md: 1080px;  /* 中等容器 (默认) */
--container-max-width-lg: 1200px;  /* 大容器 */
--container-max-width-xl: 1400px;  /* 超大容器 */
--container-max-width-full: 100%;  /* 全宽容器 */
```

### 容器内边距

```css
/* 响应式内边距 */
--container-responsive-padding-mobile: 8px 8px;    /* 移动端 */
--container-responsive-padding-small: 12px 8px;    /* 小屏幕 */
--container-responsive-padding-tablet: 32px 20px;  /* 平板端 */
--container-responsive-padding-desktop: 48px 24px; /* 桌面端 */

/* 默认内边距 */
--container-default-padding: 20px;
```

### 卡片样式配置

```css
/* 圆角配置 */
--card-border-radius-mobile: 8px;
--card-border-radius-tablet: 12px;
--card-border-radius-desktop: 16px;

/* 阴影配置 */
--card-shadow-light: 0 6px 18px rgba(0, 0, 0, 0.06);
--card-shadow-medium: 0 10px 28px rgba(0, 0, 0, 0.08);
--card-shadow-mobile: 0 2px 8px rgba(0, 0, 0, 0.06);
--card-shadow-tablet: 0 4px 12px rgba(0, 0, 0, 0.08);
```

## 🚀 使用方法

### 1. PageContainer 组件使用

#### 基础用法（推荐）
```vue
<template>
  <!-- 使用默认配置，无需指定 max-width 和 padding -->
  <PageContainer title="页面标题">
    <!-- 页面内容 -->
  </PageContainer>
</template>
```

#### 自定义容器尺寸
```vue
<template>
  <!-- 使用大容器 -->
  <PageContainer 
    title="页面标题" 
    max-width="var(--container-max-width-lg)"
  >
    <!-- 页面内容 -->
  </PageContainer>
  
  <!-- 使用小容器 -->
  <PageContainer 
    title="页面标题" 
    max-width="var(--container-max-width-sm)"
  >
    <!-- 页面内容 -->
  </PageContainer>
</template>
```

#### 自定义内边距
```vue
<template>
  <!-- 使用自定义内边距 -->
  <PageContainer 
    title="页面标题" 
    padding="var(--spacing-8)"
  >
    <!-- 页面内容 -->
  </PageContainer>
</template>
```

### 2. 工具类使用

```vue
<template>
  <!-- 使用容器尺寸工具类 -->
  <div class="container-md">
    <!-- 内容 -->
  </div>
  
  <!-- 使用响应式容器工具类 -->
  <div class="container-responsive">
    <!-- 内容 -->
  </div>
</template>
```

### 3. 直接使用 CSS 变量

```vue
<style scoped>
.custom-container {
  max-width: var(--container-max-width-lg);
  padding: var(--container-default-padding);
  margin: 0 auto;
}

.custom-card {
  border-radius: var(--card-default-border-radius);
  box-shadow: var(--card-default-shadow);
}

.custom-card:hover {
  box-shadow: var(--card-default-shadow-hover);
}
</style>
```

## 📱 响应式设计

容器系统内置了完整的响应式支持：

```css
/* 桌面端 (≥1200px) */
padding: 48px 24px;

/* 平板端 (769px - 1199px) */
padding: 32px 20px;

/* 移动端 (≤768px) */
padding: 8px 8px;
min-height: calc(100vh - 120px);

/* 小屏幕 (≤480px) */
padding: 12px 8px;
min-height: calc(100vh - 100px);
```

## ✅ 最佳实践

### 1. 优先使用默认配置
```vue
<!-- ✅ 推荐：使用默认配置 -->
<PageContainer title="页面标题">
  <!-- 内容 -->
</PageContainer>

<!-- ❌ 避免：不必要的自定义 -->
<PageContainer title="页面标题" max-width="1080px" padding="20px">
  <!-- 内容 -->
</PageContainer>
```

### 2. 使用语义化的变量名
```css
/* ✅ 推荐：使用语义化变量 */
.container {
  max-width: var(--container-max-width-lg);
  padding: var(--container-default-padding);
}

/* ❌ 避免：硬编码值 */
.container {
  max-width: 1200px;
  padding: 20px;
}
```

### 3. 保持一致性
```vue
<!-- ✅ 推荐：在同类页面中保持一致的配置 -->
<!-- 所有列表页面都使用默认配置 -->
<PageContainer title="用户列表">
<PageContainer title="产品列表">
<PageContainer title="订单列表">

<!-- ✅ 推荐：在特殊页面中使用合适的尺寸 -->
<!-- 详情页面使用大容器 -->
<PageContainer title="用户详情" max-width="var(--container-max-width-lg)">
```

## 🔧 迁移指南

### 从硬编码值迁移

```vue
<!-- 迁移前 -->
<PageContainer title="页面标题" max-width="1080px" padding="20px">

<!-- 迁移后 -->
<PageContainer title="页面标题">
```

### 自定义样式迁移

```css
/* 迁移前 */
.page-container {
  padding: 16px 12px;
  max-width: 1080px;
}

/* 迁移后 */
.page-container {
  padding: var(--container-responsive-padding-mobile);
  max-width: var(--container-default-max-width);
}
```

## 📊 配置对比

| 场景 | 旧配置 | 新配置 | 优势 |
|------|--------|--------|------|
| 默认页面 | `max-width="1080px"` | 使用默认值 | 减少重复代码 |
| 移动端间距 | `padding: 12px 8px` | `var(--container-responsive-padding-mobile)` | 统一管理 |
| 卡片圆角 | `border-radius: 16px` | `var(--card-default-border-radius)` | 主题一致性 |
| 响应式断点 | 硬编码媒体查询 | CSS变量 + 工具类 | 易于维护 |

## 🎨 主题定制

如需调整全局容器样式，只需修改 `container-constants.css` 中的变量值：

```css
:root {
  /* 调整默认容器宽度 */
  --container-default-max-width: 1200px;
  
  /* 调整默认内边距 */
  --container-default-padding: 24px;
  
  /* 调整卡片圆角 */
  --card-default-border-radius: 12px;
}
```

## 📝 注意事项

1. **避免硬编码**：不要在组件中直接使用像素值，始终使用 CSS 变量
2. **保持一致性**：同类型页面应使用相同的容器配置
3. **响应式优先**：优先使用 `responsive` 属性启用响应式布局
4. **语义化命名**：使用有意义的变量名，便于理解和维护
5. **渐进增强**：从默认配置开始，只在必要时进行自定义

通过遵循这些规范，可以确保项目中容器样式的一致性和可维护性。