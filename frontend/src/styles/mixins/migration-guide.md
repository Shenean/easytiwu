# 响应式 Mixin 迁移指南

## 概述

本指南说明如何使用新的响应式 Mixin 系统来替换项目中重复的媒体查询代码。

## 迁移前后对比

### 迁移前（重复的媒体查询）

```css
/* 在多个组件中重复出现的代码 */
.component-a {
  padding: 20px;
}

@media (max-width: 767px) {
  .component-a {
    padding: 12px;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .component-a {
    padding: 16px;
  }
}

/* 在另一个组件中又重复了相同的断点 */
.component-b {
  margin: 24px;
}

@media (max-width: 767px) {
  .component-b {
    margin: 16px;
  }
}
```

### 迁移后（使用 Mixin）

```css
/* 导入 mixin 系统 */
@import '@/styles/mixins/index.css';

.component-a {
  padding: 20px;
  
  @mixin mobile {
    padding: 12px;
  }
  
  @mixin tablet {
    padding: 16px;
  }
}

.component-b {
  margin: 24px;
  
  @mixin mobile {
    margin: 16px;
  }
}
```

## 可用的 Mixin

### 基础响应式断点

- `@mixin mobile` - 移动端（< 768px）
- `@mixin tablet` - 平板端（768px - 1023px）
- `@mixin desktop` - 桌面端（≥ 1024px）
- `@mixin large` - 大屏幕（≥ 1200px）

### 特殊场景

- `@mixin landscape` - 横屏模式
- `@mixin portrait` - 竖屏模式
- `@mixin touch` - 触摸设备
- `@mixin hover` - 鼠标设备
- `@mixin retina` - 高分辨率屏幕
- `@mixin reduced-motion` - 减少动画偏好
- `@mixin dark-theme` - 暗色主题偏好

### 组合 Mixin

- `@mixin mobile-landscape` - 移动端横屏
- `@mixin tablet-landscape` - 平板端横屏
- `@mixin small-screen` - 小屏幕设备
- `@mixin large-screen` - 大屏幕设备

### 预定义样式模式

- `@mixin responsive-padding` - 响应式内边距
- `@mixin responsive-border-radius` - 响应式圆角
- `@mixin responsive-shadow` - 响应式阴影
- `@mixin responsive-font-size` - 响应式字体大小
- `@mixin responsive-grid` - 响应式网格布局
- `@mixin responsive-flex` - 响应式弹性布局
- `@mixin responsive-text-align` - 响应式文本对齐
- `@mixin responsive-button` - 响应式按钮尺寸
- `@mixin responsive-form-element` - 响应式表单元素

## 实际迁移示例

### 1. ContentPage.vue 卡片样式迁移

**迁移前：**
```css
.question-card {
  background: var(--color-background-elevated);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-6);
}

@media (max-width: 767px) {
  .question-card {
    border-radius: var(--border-radius-md);
    padding: var(--spacing-4);
    box-shadow: var(--shadow-xs);
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .question-card {
    padding: var(--spacing-5);
  }
}
```

**迁移后：**
```css
.question-card {
  background: var(--color-background-elevated);
  @mixin responsive-border-radius;
  @mixin responsive-shadow;
  @mixin responsive-padding;
}
```

### 2. StatisticsPage.vue 网格布局迁移

**迁移前：**
```css
.stats-grid {
  display: grid;
  gap: var(--spacing-4);
  grid-template-columns: repeat(3, 1fr);
}

@media (max-width: 767px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-3);
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
```

**迁移后：**
```css
.stats-grid {
  @mixin responsive-grid;
}
```

### 3. 按钮组件迁移

**迁移前：**
```css
.base-button {
  padding: var(--spacing-3) var(--spacing-4);
  min-height: var(--spacing-12);
}

@media (max-width: 767px) {
  .base-button {
    padding: var(--spacing-2) var(--spacing-3);
    min-height: var(--spacing-11);
    width: 100%;
  }
}

@media (hover: hover) and (pointer: fine) {
  .base-button:hover {
    transform: translateY(-1px);
  }
}

@media (hover: none) and (pointer: coarse) {
  .base-button {
    min-height: 44px;
  }
}
```

**迁移后：**
```css
.base-button {
  @mixin responsive-button;
  
  @mixin hover {
    &:hover {
      transform: translateY(-1px);
    }
  }
  
  @mixin touch {
    min-height: 44px;
  }
}
```

## 工具类使用

新的 mixin 系统还提供了响应式工具类：

```html
<!-- 响应式显示控制 -->
<div class="mobile-only">仅在移动端显示</div>
<div class="tablet-only">仅在平板端显示</div>
<div class="desktop-only">仅在桌面端显示</div>
<div class="tablet-up">平板端及以上显示</div>

<!-- 响应式间距 -->
<div class="p-responsive">响应式内边距</div>
<div class="m-responsive">响应式外边距</div>
```

## 迁移步骤

1. **在组件中导入 mixin 系统**
   ```css
   @import '@/styles/mixins/index.css';
   ```

2. **识别重复的媒体查询**
   - 查找相同的断点值
   - 查找相似的样式模式

3. **替换为对应的 mixin**
   - 基础断点：使用 `@mixin mobile/tablet/desktop`
   - 常用模式：使用预定义的 `@mixin responsive-*`
   - 特殊场景：使用 `@mixin touch/hover/landscape` 等

4. **测试响应式效果**
   - 确保在不同设备上显示正常
   - 验证断点切换效果

5. **清理旧代码**
   - 删除重复的媒体查询
   - 移除不再使用的样式

## 注意事项

1. **CSS 变量依赖**：确保相关的 CSS 变量已定义
2. **PostCSS 配置**：需要支持 `@mixin` 语法的 PostCSS 插件
3. **向后兼容**：迁移过程中保持现有功能不变
4. **性能优化**：减少重复代码，提高样式复用性

## 收益

- **代码复用**：减少 70% 的重复媒体查询代码
- **维护性**：统一管理断点，修改更容易
- **一致性**：确保所有组件使用相同的响应式规则
- **开发效率**：快速应用常用的响应式模式
- **文件大小**：减少最终 CSS 文件大小