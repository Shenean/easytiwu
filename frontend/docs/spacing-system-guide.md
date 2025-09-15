# 间距系统使用指南

## 概述

本项目采用基于8px基准网格的间距系统，确保设计的一致性和可维护性。所有间距值都是8px的倍数，提供了从8px到160px的完整间距范围。

## 核心原则

### 1. 8px基准网格
- 所有间距值必须是8px的倍数
- 提供从`--spacing-1`(8px)到`--spacing-20`(160px)的标准间距
- 禁止使用非8px倍数的硬编码值

### 2. 语义化命名
- 使用描述性的CSS变量名
- 优先使用组件专用的间距变量
- 避免直接使用数值型变量名

### 3. 响应式设计
- 不同屏幕尺寸使用不同的间距值
- 移动端使用较小的间距
- 桌面端使用较大的间距

## 间距变量系统

### 基础间距变量

```css
:root {
  /* 基础间距 - 8px基准网格 */
  --spacing-1: 8px;   /* xs */
  --spacing-2: 16px;  /* sm */
  --spacing-3: 24px;  /* md */
  --spacing-4: 32px;  /* lg */
  --spacing-6: 48px;  /* xl */
  --spacing-8: 64px;  /* 2xl */
  --spacing-12: 96px; /* 3xl */
  --spacing-16: 128px; /* 4xl */
  --spacing-20: 160px; /* 5xl */
}
```

### 组件专用间距变量

```css
:root {
  /* 容器间距 */
  --container-padding-mobile: var(--spacing-2) var(--spacing-2);
  --container-padding-tablet: var(--spacing-2) var(--spacing-3);
  --container-padding-desktop: var(--spacing-3) var(--spacing-6);
  --container-default-padding: var(--spacing-3);
  
  /* 卡片间距 */
  --card-padding-compact: var(--spacing-2);
  --card-padding-default: var(--spacing-3);
  --card-padding-comfortable: var(--spacing-4);
  
  /* 按钮间距 */
  --button-gap-tight: var(--spacing-1);
  --button-gap-default: var(--spacing-2);
  --button-gap-loose: var(--spacing-3);
}
```

## 使用方法

### 1. CSS变量优先

**✅ 推荐做法：**
```css
.container {
  padding: var(--container-default-padding);
  margin-bottom: var(--spacing-3);
}

.card {
  padding: var(--card-padding-default);
  gap: var(--spacing-2);
}
```

**❌ 避免做法：**
```css
.container {
  padding: 20px; /* 硬编码值 */
  margin-bottom: 24px; /* 硬编码值 */
}
```

### 2. 工具类使用

**基础间距工具类：**
```html
<!-- 内边距 -->
<div class="spacing-p-md">24px内边距</div>
<div class="spacing-px-lg">32px水平内边距</div>
<div class="spacing-py-sm">16px垂直内边距</div>

<!-- 外边距 -->
<div class="spacing-m-lg">32px外边距</div>
<div class="spacing-mx-auto">水平居中</div>
<div class="spacing-my-xl">48px垂直外边距</div>

<!-- 间隙 -->
<div class="gap-md">24px间隙</div>
<div class="gap-row-sm">16px行间隙</div>
```

**语义化组件工具类：**
```html
<!-- 卡片间距 -->
<div class="component-spacing-card">标准卡片间距</div>
<div class="component-spacing-card-compact">紧凑卡片间距</div>

<!-- 表单间距 -->
<div class="component-spacing-form-field">表单字段间距</div>
<div class="component-spacing-form-section">表单区块间距</div>

<!-- 按钮组间距 -->
<div class="component-spacing-button-group">按钮组间距</div>
```

### 3. 响应式间距

**响应式Mixin使用：**
```css
.responsive-container {
  @mixin container-spacing;
}

.responsive-card {
  @mixin card-spacing;
}

.responsive-component {
  @mixin component-spacing;
}
```

**响应式工具类：**
```html
<div class="spacing-responsive-p-mobile">移动端内边距</div>
<div class="spacing-responsive-p-tablet">平板端内边距</div>
<div class="spacing-responsive-p-desktop">桌面端内边距</div>
```

## 最佳实践

### 1. 选择合适的间距方法

**优先级顺序：**
1. **组件专用变量** - 如`--card-padding-default`
2. **语义化工具类** - 如`.component-spacing-card`
3. **基础工具类** - 如`.spacing-p-md`
4. **基础变量** - 如`var(--spacing-3)`

### 2. 响应式间距策略

```css
/* 移动端优先 */
.component {
  padding: var(--spacing-2); /* 16px */
}

/* 平板端 */
@media (min-width: 769px) {
  .component {
    padding: var(--spacing-3); /* 24px */
  }
}

/* 桌面端 */
@media (min-width: 1025px) {
  .component {
    padding: var(--spacing-4); /* 32px */
  }
}
```

### 3. 组件间距规范

**卡片组件：**
```css
.card {
  padding: var(--card-padding-default);
  margin-bottom: var(--spacing-3);
  
  &.compact {
    padding: var(--card-padding-compact);
  }
  
  &.comfortable {
    padding: var(--card-padding-comfortable);
  }
}
```

**表单组件：**
```css
.form-field {
  margin-bottom: var(--spacing-3);
  
  &.compact {
    margin-bottom: var(--spacing-2);
  }
}

.form-section {
  margin-bottom: var(--spacing-6);
  padding: var(--spacing-4);
}
```

**按钮组件：**
```css
.button-group {
  gap: var(--button-gap-default);
  
  &.tight {
    gap: var(--button-gap-tight);
  }
  
  &.loose {
    gap: var(--button-gap-loose);
  }
}
```

## 常见问题

### Q: 什么时候可以使用硬编码间距值？
A: 原则上不应该使用硬编码值。如果确实需要特殊间距，应该：
1. 首先考虑是否可以调整设计以使用标准间距
2. 如果必须使用，应该创建新的CSS变量
3. 确保该值是8px的倍数

### Q: 如何处理设计稿中非8px倍数的间距？
A: 
1. 与设计师沟通，调整为最接近的8px倍数
2. 如果是12px，调整为8px或16px
3. 如果是20px，调整为16px或24px

### Q: 响应式间距如何选择？
A: 
1. 移动端：使用较小间距（8px-16px）
2. 平板端：使用中等间距（16px-24px）
3. 桌面端：使用较大间距（24px-48px）

### Q: 如何在Vue组件中使用间距系统？
A: 
```vue
<template>
  <div class="component-spacing-card">
    <h2 class="spacing-mb-md">标题</h2>
    <p class="spacing-mb-sm">内容</p>
    <div class="component-spacing-button-group">
      <button>按钮1</button>
      <button>按钮2</button>
    </div>
  </div>
</template>

<style scoped>
.custom-spacing {
  padding: var(--container-default-padding);
  
  @mixin container-spacing;
}
</style>
```

## 文件结构

```
src/styles/
├── spacing-constants.css      # 基础间距变量定义
├── container-constants.css    # 容器间距变量
├── spacing-utilities.pcss     # 间距工具类
├── spacing-responsive.pcss    # 响应式间距Mixin
└── grid-constants.css         # 栅格间距变量
```

## 维护指南

### 添加新的间距变量
1. 确保值是8px的倍数
2. 使用语义化命名
3. 在相应的常量文件中定义
4. 更新工具类（如需要）
5. 更新文档

### 修改现有间距
1. 评估影响范围
2. 确保向后兼容
3. 更新相关组件
4. 测试响应式表现
5. 更新文档

---

**记住：一致的间距系统是优秀用户界面的基础。遵循这些指南将确保我们的应用具有专业、统一的视觉体验。**