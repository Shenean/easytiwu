# 样式编码规范

## 📋 概述

本文档定义了 EasyTiWu 前端项目的样式编码规范，旨在消除硬编码样式值，建立统一的样式系统，提升代码质量和维护效率。

## 🚫 禁止使用的样式写法

### 1. 硬编码数值

```css
/* ❌ 禁止 - 硬编码间距值 */
.component {
  padding: 16px;
  margin: 12px;
  gap: 8px;
}

/* ❌ 禁止 - 硬编码字体大小 */
.text {
  font-size: 14px;
  line-height: 20px;
}

/* ❌ 禁止 - 硬编码圆角 */
.card {
  border-radius: 8px;
}

/* ❌ 禁止 - 硬编码颜色值 */
.button {
  background-color: #18a058;
  color: #ffffff;
}
```

### 2. 内联样式

```vue
<!-- ❌ 禁止 - 内联样式 -->
<template>
  <div style="padding: 16px; margin: 12px;">
    <p style="font-size: 14px; color: #666;">内容</p>
  </div>
</template>
```

### 3. 重复的媒体查询

```css
/* ❌ 禁止 - 重复定义相同断点 */
@media (max-width: 768px) {
  .component-a { padding: 12px; }
}

@media (max-width: 768px) {
  .component-b { margin: 8px; }
}
```

## ✅ 推荐的样式写法

### 1. 使用 CSS 变量

```css
/* ✅ 推荐 - 使用间距变量 */
.component {
  padding: var(--spacing-4);     /* 16px */
  margin: var(--spacing-3);      /* 12px */
  gap: var(--spacing-2);         /* 8px */
}

/* ✅ 推荐 - 使用字体变量 */
.text {
  font-size: var(--font-size-sm);     /* 14px */
  line-height: var(--line-height-sm); /* 20px */
}

/* ✅ 推荐 - 使用圆角变量 */
.card {
  border-radius: var(--border-radius-md); /* 8px */
}

/* ✅ 推荐 - 使用颜色变量 */
.button {
  background-color: var(--n-color-primary);
  color: var(--n-text-color-base);
}
```

### 2. 使用工具类

```vue
<!-- ✅ 推荐 - 使用工具类 -->
<template>
  <div class="p-4 m-3">
    <p class="text-sm text-secondary">内容</p>
  </div>
</template>
```

### 3. 使用响应式 Mixin

```css
/* ✅ 推荐 - 使用响应式 Mixin */
.component {
  padding: var(--spacing-4);
}

@include mobile {
  .component {
    padding: var(--spacing-3);
  }
}
```

## 📚 可用的 CSS 变量系统

### 1. 间距变量 (spacing-constants.css)

```css
/* 基础间距单位 (4px 倍数) */
--spacing-1: 4px;
--spacing-2: 8px;
--spacing-3: 12px;
--spacing-4: 16px;
--spacing-5: 20px;
--spacing-6: 24px;
--spacing-8: 32px;
--spacing-12: 48px;

/* 语义化间距 */
--spacing-xs: var(--spacing-1);   /* 4px */
--spacing-sm: var(--spacing-2);   /* 8px */
--spacing-md: var(--spacing-4);   /* 16px */
--spacing-lg: var(--spacing-6);   /* 24px */
--spacing-xl: var(--spacing-8);   /* 32px */
--spacing-2xl: var(--spacing-12); /* 48px */

/* 组件专用间距 */
--button-padding-sm: var(--spacing-1) var(--spacing-3);   /* 4px 12px */
--button-padding-md: var(--spacing-2) var(--spacing-4);   /* 8px 16px */
--button-padding-lg: var(--spacing-3) var(--spacing-5);   /* 12px 20px */

--card-padding-sm: var(--spacing-3);   /* 12px */
--card-padding-md: var(--spacing-4);   /* 16px */
--card-padding-lg: var(--spacing-6);   /* 24px */
```

### 2. 容器变量 (container-constants.css)

```css
/* 容器最大宽度 */
--container-max-width-sm: 640px;
--container-max-width-md: 768px;
--container-max-width-lg: 1024px;
--container-max-width-xl: 1280px;
--container-max-width-2xl: 1536px;

/* 容器内边距 */
--container-padding-mobile: 12px;
--container-padding-tablet: 16px;
--container-padding-desktop: 20px;
```

### 3. 栅格变量 (grid-constants.css)

```css
/* 栅格间距 */
--grid-gap-xs: 4px;
--grid-gap-sm: 8px;
--grid-gap-md: 16px;
--grid-gap-lg: 24px;
--grid-gap-xl: 32px;
--grid-gap-2xl: 48px;

/* 栅格列数 */
--grid-cols-mobile: 1;
--grid-cols-tablet: 2;
--grid-cols-desktop: 3;
--grid-cols-large: 4;
```

### 4. 断点变量 (breakpoints.css)

```css
/* 响应式断点 */
--breakpoint-mobile: 480px;
--breakpoint-tablet: 768px;
--breakpoint-desktop: 1200px;
--breakpoint-large: 1600px;
```

## 🎯 工具类系统

### 1. 间距工具类

```css
/* 内边距 */
.p-1 { padding: var(--spacing-1); }     /* 4px */
.p-2 { padding: var(--spacing-2); }     /* 8px */
.p-3 { padding: var(--spacing-3); }     /* 12px */
.p-4 { padding: var(--spacing-4); }     /* 16px */
.p-5 { padding: var(--spacing-5); }     /* 20px */
.p-6 { padding: var(--spacing-6); }     /* 24px */

/* 外边距 */
.m-1 { margin: var(--spacing-1); }      /* 4px */
.m-2 { margin: var(--spacing-2); }      /* 8px */
.m-3 { margin: var(--spacing-3); }      /* 12px */
.m-4 { margin: var(--spacing-4); }      /* 16px */
.m-5 { margin: var(--spacing-5); }      /* 20px */
.m-6 { margin: var(--spacing-6); }      /* 24px */

/* 方向性间距 */
.pt-4 { padding-top: var(--spacing-4); }
.pb-4 { padding-bottom: var(--spacing-4); }
.pl-4 { padding-left: var(--spacing-4); }
.pr-4 { padding-right: var(--spacing-4); }

.mt-4 { margin-top: var(--spacing-4); }
.mb-4 { margin-bottom: var(--spacing-4); }
.ml-4 { margin-left: var(--spacing-4); }
.mr-4 { margin-right: var(--spacing-4); }
```

### 2. 响应式工具类

```css
/* 移动端专用 */
@media (max-width: 768px) {
  .mobile\:p-2 { padding: var(--spacing-2); }
  .mobile\:p-3 { padding: var(--spacing-3); }
  .mobile\:m-2 { margin: var(--spacing-2); }
  .mobile\:m-3 { margin: var(--spacing-3); }
}

/* 平板端专用 */
@media (min-width: 769px) and (max-width: 1200px) {
  .tablet\:p-4 { padding: var(--spacing-4); }
  .tablet\:p-5 { padding: var(--spacing-5); }
}

/* 桌面端专用 */
@media (min-width: 1201px) {
  .desktop\:p-5 { padding: var(--spacing-5); }
  .desktop\:p-6 { padding: var(--spacing-6); }
}
```

## 🔧 响应式 Mixin 系统

### 1. 断点 Mixin 定义

```css
/* mixins/breakpoints.css */
@define-mixin mobile {
  @media (max-width: 768px) {
    @mixin-content;
  }
}

@define-mixin tablet {
  @media (min-width: 769px) and (max-width: 1200px) {
    @mixin-content;
  }
}

@define-mixin desktop {
  @media (min-width: 1201px) {
    @mixin-content;
  }
}

@define-mixin mobile-and-tablet {
  @media (max-width: 1200px) {
    @mixin-content;
  }
}
```

### 2. Mixin 使用示例

```css
.component {
  padding: var(--spacing-4);
  
  @include mobile {
    padding: var(--spacing-3);
  }
  
  @include tablet {
    padding: var(--spacing-4);
  }
  
  @include desktop {
    padding: var(--spacing-5);
  }
}
```

## 📝 组件开发规范

### 1. Vue 组件样式规范

```vue
<template>
  <div class="component-wrapper">
    <div class="component-header">
      <h2 class="component-title">标题</h2>
    </div>
    <div class="component-content">
      <!-- 内容 -->
    </div>
  </div>
</template>

<style scoped>
/* ✅ 推荐 - 使用 CSS 变量和工具类 */
.component-wrapper {
  padding: var(--card-padding-md);
  border-radius: var(--border-radius-md);
  background-color: var(--n-card-color);
}

.component-header {
  margin-bottom: var(--spacing-4);
  padding-bottom: var(--spacing-3);
  border-bottom: 1px solid var(--n-border-color);
}

.component-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--n-text-color-1);
}

.component-content {
  padding: var(--spacing-4);
}

/* 响应式适配 */
@include mobile {
  .component-wrapper {
    padding: var(--card-padding-sm);
  }
  
  .component-content {
    padding: var(--spacing-3);
  }
}
</style>
```

### 2. 组件 Props 样式绑定

```vue
<script setup lang="ts">
interface Props {
  size?: 'sm' | 'md' | 'lg'
  spacing?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  spacing: 'md'
})

// ✅ 推荐 - 使用计算属性映射到 CSS 变量
const componentClasses = computed(() => ({
  [`size-${props.size}`]: true,
  [`spacing-${props.spacing}`]: true
}))
</script>

<style scoped>
/* 尺寸变体 */
.size-sm {
  padding: var(--button-padding-sm);
  font-size: var(--font-size-sm);
}

.size-md {
  padding: var(--button-padding-md);
  font-size: var(--font-size-md);
}

.size-lg {
  padding: var(--button-padding-lg);
  font-size: var(--font-size-lg);
}

/* 间距变体 */
.spacing-xs { gap: var(--spacing-xs); }
.spacing-sm { gap: var(--spacing-sm); }
.spacing-md { gap: var(--spacing-md); }
.spacing-lg { gap: var(--spacing-lg); }
.spacing-xl { gap: var(--spacing-xl); }
</style>
```

## 🔍 代码审查清单

### 开发阶段检查

- [ ] 是否使用了硬编码的 px 值？
- [ ] 是否使用了内联样式？
- [ ] 是否重复定义了相同的媒体查询？
- [ ] 是否使用了预定义的 CSS 变量？
- [ ] 是否使用了工具类而非自定义样式？
- [ ] 响应式设计是否使用了 Mixin？

### 代码审查检查

- [ ] 所有间距值是否使用 `--spacing-*` 变量？
- [ ] 所有颜色值是否使用 Naive UI 的颜色变量？
- [ ] 所有断点是否使用预定义的 Mixin？
- [ ] 组件样式是否具有良好的可维护性？
- [ ] 是否遵循了组件样式命名规范？

## 🚀 迁移指南

### 1. 硬编码样式迁移

```css
/* 迁移前 */
.old-component {
  padding: 16px;
  margin: 12px 0;
  border-radius: 8px;
  font-size: 14px;
}

/* 迁移后 */
.new-component {
  padding: var(--spacing-4);
  margin: var(--spacing-3) 0;
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
}
```

### 2. 媒体查询迁移

```css
/* 迁移前 */
.component {
  padding: 20px;
}

@media (max-width: 768px) {
  .component {
    padding: 12px;
  }
}

/* 迁移后 */
.component {
  padding: var(--spacing-5);
}

@include mobile {
  .component {
    padding: var(--spacing-3);
  }
}
```

### 3. 内联样式迁移

```vue
<!-- 迁移前 -->
<template>
  <div style="padding: 16px; margin-bottom: 12px;">
    内容
  </div>
</template>

<!-- 迁移后 -->
<template>
  <div class="p-4 mb-3">
    内容
  </div>
</template>
```

## ⚡ 性能优化

### 1. CSS 变量的优势

- **运行时修改**：可以通过 JavaScript 动态修改
- **主题切换**：支持动态主题切换
- **减少重复**：避免重复定义相同的值
- **易于维护**：集中管理样式配置

### 2. 工具类的优势

- **减少 CSS 体积**：复用预定义的样式类
- **提升开发效率**：无需编写自定义样式
- **保持一致性**：确保样式的统一性
- **易于调试**：样式类名直观明了

## 📊 违规检测

### 1. ESLint 规则配置

```javascript
// .eslintrc.js
module.exports = {
  rules: {
    // 禁止在 Vue 模板中使用内联样式
    'vue/no-inline-styles': 'error',
    
    // 禁止在 CSS 中使用硬编码数值
    'no-hardcoded-values': 'error'
  }
}
```

### 2. Stylelint 规则配置

```javascript
// .stylelintrc.js
module.exports = {
  rules: {
    // 禁止使用硬编码的像素值
    'declaration-property-value-disallowed-list': {
      '/^(padding|margin|gap|font-size|border-radius)$/': ['/\\d+px$/'],
    },
    
    // 强制使用 CSS 变量
    'custom-property-pattern': '^[a-z][a-z0-9]*(-[a-z0-9]+)*$',
  }
}
```

## 🎯 最佳实践总结

1. **优先使用 CSS 变量**：所有数值都应使用预定义的 CSS 变量
2. **善用工具类**：简单的样式使用工具类，复杂的样式使用组件类
3. **统一响应式**：使用 Mixin 系统处理所有响应式需求
4. **语义化命名**：使用有意义的变量名和类名
5. **模块化组织**：按功能模块组织样式文件
6. **定期审查**：建立定期的样式代码审查机制

---

*本规范将根据项目发展持续更新，请定期查看最新版本。*