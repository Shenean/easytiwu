# UI设计规范修复实施计划

## 概述

本文档提供了基于UI设计规范合规性评估的详细修复计划，包含具体的代码示例和实施步骤。

## 第一阶段：核心规范修复

### 1. 集成 vue-i18n 国际化

#### 1.1 安装依赖
```bash
pnpm add vue-i18n@9
```

#### 1.2 创建国际化配置
```typescript
// src/i18n/index.ts
import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN.json'
import enUS from './locales/en-US.json'

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
}

export const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN',
  fallbackLocale: 'en-US',
  messages
})
```

#### 1.3 创建语言包
```json
// src/i18n/locales/zh-CN.json
{
  "common": {
    "submit": "提交",
    "cancel": "取消",
    "delete": "删除",
    "edit": "编辑",
    "save": "保存",
    "loading": "加载中...",
    "success": "操作成功",
    "error": "操作失败"
  },
  "navigation": {
    "home": "首页",
    "bank": "题库",
    "upload": "上传",
    "statistics": "统计",
    "settings": "设置"
  },
  "theme": {
    "light": "亮色主题",
    "dark": "暗色主题",
    "system": "跟随系统",
    "switchTo": "切换到{theme}"
  }
}
```

#### 1.4 更新 main.ts
```typescript
// src/main.ts
import { createApp } from 'vue'
import { i18n } from './i18n'
import App from './App.vue'

const app = createApp(App)
app.use(i18n)
app.mount('#app')
```

#### 1.5 更新组件使用国际化
```vue
<!-- SettingsPage.vue -->
<template>
  <PageContainer :title="$t('navigation.settings')">
    <div class="setting-section">
      <h3 class="setting-title">{{ $t('theme.darkMode') }}</h3>
      <n-button @click="toggleTheme">
        {{ $t('theme.switchTo', { theme: getNextThemeText() }) }}
      </n-button>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

function getNextThemeText() {
  const nextTheme = getNextTheme()
  return t(`theme.${nextTheme}`)
}
</script>
```

### 2. 修复字体栈和字号系统

#### 2.1 更新 design-tokens.css
```css
/* src/styles/design-tokens.css */
:root {
  /* ===== 字体系统 - 符合规范 ===== */
  
  /* 字体族 - 添加 Emoji 支持 */
  --font-family-base: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji",
    "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
  
  /* 语义化字号系统 */
  --font-size-display-large: 32px;   /* 页面标题、大屏展示 */
  --font-size-display-medium: 24px;  /* 模块标题 */
  --font-size-title-large: 20px;     /* 卡片/区块标题 */
  --font-size-title-medium: 16px;    /* 按钮文字、导航标题 */
  --font-size-body-large: 16px;      /* 正文、表单标签 */
  --font-size-body-medium: 14px;     /* 普通文本、辅助信息 */
  --font-size-body-small: 12px;      /* 注释、时间、标签 */
  --font-size-caption: 12px;         /* 表格列头、小提示 */
  
  /* 字重系统 */
  --font-weight-display: 700;        /* Display 级别 */
  --font-weight-title: 600;          /* Title 级别 */
  --font-weight-body: 400;           /* Body 级别 */
  --font-weight-caption: 500;        /* Caption 级别 */
}
```

#### 2.2 创建排版工具类
```css
/* src/styles/typography.css */
/* 语义化排版类 */
.text-display-large {
  font-size: var(--font-size-display-large);
  font-weight: var(--font-weight-display);
  line-height: 1.2;
}

.text-display-medium {
  font-size: var(--font-size-display-medium);
  font-weight: var(--font-weight-display);
  line-height: 1.3;
}

.text-title-large {
  font-size: var(--font-size-title-large);
  font-weight: var(--font-weight-title);
  line-height: 1.4;
}

.text-title-medium {
  font-size: var(--font-size-title-medium);
  font-weight: var(--font-weight-title);
  line-height: 1.4;
}

.text-body-large {
  font-size: var(--font-size-body-large);
  font-weight: var(--font-weight-body);
  line-height: 1.5;
}

.text-body-medium {
  font-size: var(--font-size-body-medium);
  font-weight: var(--font-weight-body);
  line-height: 1.5;
}

.text-body-small {
  font-size: var(--font-size-body-small);
  font-weight: var(--font-weight-body);
  line-height: 1.4;
}

.text-caption {
  font-size: var(--font-size-caption);
  font-weight: var(--font-weight-caption);
  line-height: 1.4;
}
```

### 3. 规范按钮使用

#### 3.1 更新 BankPage.vue 按钮使用
```vue
<!-- src/pages/BankPage.vue -->
<template>
  <div class="bank-actions">
    <!-- 主操作：只有一个 primary -->
    <BaseButton 
      type="primary" 
      size="medium" 
      @click="handlePractice(bank.id)"
      :aria-label="$t('bank.practiceLabel', { name: bank.name })"
    >
      {{ $t('bank.startPractice') }}
    </BaseButton>
    
    <!-- 次要操作：使用 default 或 text -->
    <BaseButton 
      type="default" 
      size="medium" 
      @click="handleWrongSet(bank.id)"
      :aria-label="$t('bank.wrongSetLabel', { name: bank.name })"
    >
      {{ $t('bank.wrongSet') }}
    </BaseButton>
    
    <!-- 危险操作：使用 text + error 色彩 -->
    <BaseButton 
      text 
      type="error" 
      size="medium" 
      @click="confirmDelete(bank.id)"
      :aria-label="$t('bank.deleteLabel', { name: bank.name })"
    >
      {{ $t('common.delete') }}
    </BaseButton>
  </div>
</template>
```

#### 3.2 创建按钮使用指南组件
```vue
<!-- src/components/common/ButtonGroup.vue -->
<template>
  <div class="button-group" :class="`button-group--${layout}`">
    <slot />
  </div>
</template>

<script setup lang="ts">
interface Props {
  layout?: 'horizontal' | 'vertical'
  align?: 'left' | 'center' | 'right'
}

withDefaults(defineProps<Props>(), {
  layout: 'horizontal',
  align: 'left'
})
</script>

<style scoped>
.button-group {
  display: flex;
  gap: var(--spacing-3);
}

.button-group--horizontal {
  flex-direction: row;
}

.button-group--vertical {
  flex-direction: column;
}

.button-group--center {
  justify-content: center;
}

.button-group--right {
  justify-content: flex-end;
}
</style>
```

### 4. 减少自定义样式，使用 Naive UI 原生 API

#### 4.1 重构 SettingsPage.vue 主题切换
```vue
<!-- src/pages/SettingsPage.vue -->
<template>
  <PageContainer :title="$t('navigation.settings')">
    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <component :is="MoonOutline" />
            </n-icon>
            <h3 class="text-title-medium">{{ $t('theme.darkMode') }}</h3>
          </div>
        </div>

        <!-- 使用 Naive UI 原生组件替代自定义样式 -->
        <n-button-group>
          <n-button 
            v-for="theme in themeOptions" 
            :key="theme.value"
            :type="currentTheme === theme.value ? 'primary' : 'default'"
            size="small"
            @click="setTheme(theme.value)"
            :aria-label="$t('theme.switchTo', { theme: theme.label })"
          >
            <template #icon>
              <n-icon>
                <component :is="theme.icon" />
              </n-icon>
            </template>
            {{ theme.label }}
          </n-button>
        </n-button-group>
      </div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { SunnyOutline, MoonOutline, DesktopOutline } from '@vicons/ionicons5'

const { t } = useI18n()

const themeOptions = computed(() => [
  {
    value: 'light',
    label: t('theme.light'),
    icon: SunnyOutline
  },
  {
    value: 'dark', 
    label: t('theme.dark'),
    icon: MoonOutline
  },
  {
    value: 'system',
    label: t('theme.system'),
    icon: DesktopOutline
  }
])

function setTheme(theme: string) {
  // 使用统一的消息提示
  message.success(t('theme.switchSuccess', { theme: t(`theme.${theme}`) }))
}
</script>

<style scoped>
/* 大幅减少自定义样式，使用语义化类名 */
.setting-section {
  padding: var(--spacing-4);
  border-bottom: 1px solid var(--n-border-color);
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-4);
}

.setting-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.setting-icon {
  color: var(--n-color-primary);
}
</style>
```

## 第二阶段：体验优化

### 1. 统一响应式断点

#### 1.1 更新 useBreakpoints.ts
```typescript
// src/composables/useBreakpoints.ts
import { ref, onMounted, onUnmounted } from 'vue'

// 符合规范的断点定义
const BREAKPOINTS = {
  s: 768,   // 小屏幕
  m: 1024,  // 中等屏幕
  l: 1280   // 大屏幕
} as const

export function useBreakpoints() {
  const currentBreakpoint = ref<keyof typeof BREAKPOINTS | 'xs'>('xs')
  const isMobile = computed(() => currentBreakpoint.value === 'xs')
  const isTablet = computed(() => currentBreakpoint.value === 's')
  const isDesktop = computed(() => ['m', 'l'].includes(currentBreakpoint.value))

  const updateBreakpoint = () => {
    const width = window.innerWidth
    if (width >= BREAKPOINTS.l) {
      currentBreakpoint.value = 'l'
    } else if (width >= BREAKPOINTS.m) {
      currentBreakpoint.value = 'm'
    } else if (width >= BREAKPOINTS.s) {
      currentBreakpoint.value = 's'
    } else {
      currentBreakpoint.value = 'xs'
    }
  }

  onMounted(() => {
    updateBreakpoint()
    window.addEventListener('resize', updateBreakpoint)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateBreakpoint)
  })

  return {
    currentBreakpoint: readonly(currentBreakpoint),
    isMobile,
    isTablet,
    isDesktop,
    BREAKPOINTS
  }
}
```

#### 1.2 更新 CSS 断点
```css
/* src/styles/mixins/breakpoints.css */
/* 符合规范的断点 Mixin */
@media (min-width: 768px) {
  .responsive-s {
    /* 小屏幕样式 */
  }
}

@media (min-width: 1024px) {
  .responsive-m {
    /* 中等屏幕样式 */
  }
}

@media (min-width: 1280px) {
  .responsive-l {
    /* 大屏幕样式 */
  }
}
```

### 2. 完善无障碍支持

#### 2.1 创建无障碍工具函数
```typescript
// src/utils/accessibility.ts
export function generateAriaLabel(action: string, target?: string): string {
  return target ? `${action} ${target}` : action
}

export function announceToScreenReader(message: string) {
  const announcement = document.createElement('div')
  announcement.setAttribute('aria-live', 'polite')
  announcement.setAttribute('aria-atomic', 'true')
  announcement.className = 'sr-only'
  announcement.textContent = message
  
  document.body.appendChild(announcement)
  
  setTimeout(() => {
    document.body.removeChild(announcement)
  }, 1000)
}
```

#### 2.2 添加无障碍样式
```css
/* src/styles/accessibility.css */
/* 屏幕阅读器专用样式 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* 焦点可见性 */
.focus-visible {
  outline: 2px solid var(--n-color-primary);
  outline-offset: 2px;
}

/* 高对比度支持 */
@media (prefers-contrast: high) {
  :root {
    --n-border-color: #000000;
    --n-text-color: #000000;
  }
}

/* 减少动画支持 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

### 3. 优化组件命名

#### 3.1 重命名组件
```bash
# 重命名建议
mv src/components/NavigationBar.vue src/components/TheNavigationBar.vue
# 或
mv src/components/NavigationBar.vue src/components/AppNavigationBar.vue
```

#### 3.2 更新导入引用
```typescript
// 更新所有引用
import TheNavigationBar from '@/components/TheNavigationBar.vue'
// 或
import AppNavigationBar from '@/components/AppNavigationBar.vue'
```

## 第三阶段：系统完善

### 1. 优化样式文件结构

#### 1.1 创建新的样式文件
```css
/* src/styles/naive-ui-overrides.css */
/* Naive UI 组件样式覆盖 */
.n-button {
  border-radius: var(--border-radius-md);
}

.n-card {
  border-radius: var(--border-radius-lg);
}

.n-input {
  border-radius: var(--border-radius-sm);
}
```

```css
/* src/styles/utilities.css */
/* 工具类样式 */
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }

.flex { display: flex; }
.flex-col { flex-direction: column; }
.items-center { align-items: center; }
.justify-center { justify-content: center; }
.justify-between { justify-content: space-between; }

.w-full { width: 100%; }
.h-full { height: 100%; }
```

#### 1.2 更新样式导入
```css
/* src/styles/index.css */
/* 基础系统 */
@import './design-tokens.css';
@import './typography.css';
@import './accessibility.css';

/* 组件样式 */
@import './components.css';
@import './naive-ui-overrides.css';

/* 工具类 */
@import './utilities.css';

/* Mixin 系统 */
@import './mixins/index.css';
```

### 2. 完善主题系统

#### 2.1 添加组件级主题覆盖
```typescript
// src/App.vue - 更新主题覆盖配置
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
    },
    // 组件级覆盖
    Button: {
      borderRadius: '4px',
      fontWeight: '500'
    },
    Card: {
      borderRadius: '8px'
    },
    Input: {
      borderRadius: '4px'
    },
    DataTable: {
      borderRadius: '6px'
    }
  }

  return baseOverrides
})
```

## 实施时间表

### 第一周：核心修复
- [ ] 集成 vue-i18n (2天)
- [ ] 修复字体和字号系统 (1天)
- [ ] 规范按钮使用 (1天)
- [ ] 减少自定义样式 (1天)

### 第二周：体验优化
- [ ] 统一响应式断点 (2天)
- [ ] 完善无障碍支持 (2天)
- [ ] 优化组件命名 (1天)

### 第三周：系统完善
- [ ] 优化样式结构 (2天)
- [ ] 完善主题系统 (2天)
- [ ] 添加文档和测试 (1天)

## 验收标准

### 功能验收
- [ ] 国际化切换正常工作
- [ ] 主题切换无样式问题
- [ ] 响应式布局在所有断点正常
- [ ] 无障碍功能通过 axe 检测

### 代码质量
- [ ] ESLint 检查通过
- [ ] 样式符合 BEM 命名规范
- [ ] 组件命名符合 Vue 风格指南
- [ ] 无 console 警告或错误

### 性能指标
- [ ] Lighthouse 性能评分 > 90
- [ ] 首屏加载时间 < 2s
- [ ] 主题切换响应时间 < 100ms

通过这个详细的实施计划，项目将完全符合UI设计规范要求，提供更好的用户体验和开发体验。