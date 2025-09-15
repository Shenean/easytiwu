# 主题色彩系统优化指南

## 概述

本项目已按照UI设计规范优化主题色彩系统，使用标准色板确保视觉一致性和用户体验。

## 标准色板

### 主色系（蓝色）
- **Primary**: `#2080F0` - 主要操作按钮、链接、强调色
- **Primary Hover**: `#3090FF` - 悬停状态
- **Primary Pressed**: `#1070E0` - 按下状态
- **Primary Suppl**: `#3090FF` - 补充色

### 功能色系
- **Success**: `#18A058` - 成功状态、完成操作
- **Warning**: `#F0A020` - 警告提示、需注意操作
- **Error**: `#F04040` - 错误状态、危险操作
- **Info**: `#2080F0` - 信息提示（同主色）

### 文本颜色
- **Primary Text**: `#2C2C2C` (浅色模式) / `#F5F5F5` (深色模式)
- **Secondary Text**: `#666666` - 次要文本、描述文字

### 背景和边框
- **Background**: `#FFFFFF` (浅色模式) / `#1E1E1E` (深色模式)
- **Border**: `#D9D9D9` - 边框、分割线
- **Hover**: `rgba(32, 128, 240, 0.1)` - 悬停背景色

## 实现方式

### 1. Naive UI 主题覆盖

在 `App.vue` 中通过 `themeOverrides` 配置：

```typescript
const themeOverrides = computed<GlobalThemeOverrides>(() => {
  const baseOverrides: GlobalThemeOverrides = {
    common: {
      // 主色系
      primaryColor: '#2080F0',
      primaryColorHover: '#3090FF',
      primaryColorPressed: '#1070E0',
      primaryColorSuppl: '#3090FF',
      
      // 功能色系
      successColor: '#18A058',
      warningColor: '#F0A020',
      errorColor: '#F04040',
      infoColor: '#2080F0',
      
      // 文本和背景
      textColorBase: actualTheme.value === 'dark' ? '#F5F5F5' : '#2C2C2C',
      textColor2: '#666666',
      borderColor: '#D9D9D9',
      bodyColor: actualTheme.value === 'dark' ? '#1E1E1E' : '#FFFFFF',
      hoverColor: 'rgba(32, 128, 240, 0.1)',
    }
  }
  return baseOverrides
})
```

### 2. CSS 设计令牌

在 `design-tokens.css` 中定义标准色板：

```css
/* 主色调 - 基于UI设计规范的标准蓝色 */
--color-primary-500: #2080f0;
--color-success-500: #18a058;
--color-warning-500: #f0a020;
--color-error-500: #f04040;

/* 语义化颜色 */
--color-text-primary: #2c2c2c;
--color-text-secondary: #666666;
--color-border: #d9d9d9;
--color-background: #ffffff;
--color-hover: rgba(32, 128, 240, 0.1);
```

### 3. 深色模式支持

```css
@media (prefers-color-scheme: dark) {
  :root {
    --color-background: #1e1e1e;
    --color-text-primary: #f5f5f5;
    --color-text-inverse: #2c2c2c;
  }
}
```

## 使用建议

### 1. 组件开发
- 优先使用 Naive UI 组件的内置颜色
- 通过 `useThemeVars()` 获取主题变量
- 避免硬编码颜色值

### 2. 自定义样式
- 使用 CSS 变量：`var(--color-primary-500)`
- 遵循语义化命名：`var(--color-text-primary)`
- 确保深色模式兼容性

### 3. 颜色扩展
- 新增颜色应遵循现有命名规范
- 提供完整的色阶（50-900）
- 考虑无障碍访问性（对比度）

## 效果展示

### 主要改进
1. **统一性**: 所有组件使用一致的蓝色主题
2. **规范性**: 符合UI设计规范的标准色板
3. **可维护性**: 通过CSS变量和主题覆盖集中管理
4. **响应性**: 支持浅色/深色模式自动切换
5. **扩展性**: 易于添加新的颜色变量和主题

### 视觉对比
- **之前**: 绿色主题 (`#18a058`)
- **现在**: 蓝色主题 (`#2080F0`)
- **改进**: 更符合现代设计趋势，提升品牌一致性

## 注意事项

1. **兼容性**: 确保所有浏览器支持CSS变量
2. **性能**: 避免频繁切换主题导致的重绘
3. **测试**: 在不同设备和主题下验证颜色效果
4. **文档**: 及时更新设计系统文档

通过这次优化，项目拥有了更加规范、统一、可维护的主题色彩系统。