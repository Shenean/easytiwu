<template>
  <PageContainer title="设置" card-class="settings-card" container-class="settings-container">
    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <i class="i-ion-moon"></i>
            </n-icon>
            <h3 class="setting-title">暗色主题</h3>
          </div>
        </div>

        <div class="theme-toggle-container">
          <button class="theme-toggle-btn" :class="`theme-${currentTheme}`" @click="toggleTheme"
            :aria-label="getThemeLabel()">
            <div class="theme-icon-container">
              <n-icon size="18" class="theme-icon">
                <i :class="getThemeIcon()"></i>
              </n-icon>
            </div>
            <span class="theme-text">{{ getThemeText() }}</span>
          </button>
        </div>
      </div>
    </div>

    <div class="setting-section small">
      <div class="setting-header">
        <n-icon size="20" class="setting-icon">
          <i class="i-ion-settings-outline"></i>
        </n-icon>
        <h3 class="setting-title">其他设置</h3>
      </div>
      <div class="setting-description">更多功能即将推出…</div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import {inject, onMounted, ref} from 'vue'
import {useMessage} from 'naive-ui'
import PageContainer from '../components/common/PageContainer.vue'

type Theme = 'light' | 'dark' | 'system'

const message = useMessage()
const currentTheme = ref<Theme>('system')

const setGlobalTheme = inject<(theme: Theme) => void>('setGlobalTheme')
const getGlobalTheme = inject<() => Theme>('getGlobalTheme')

onMounted(() => {
  if (getGlobalTheme) {
    currentTheme.value = getGlobalTheme()
  } else {
    currentTheme.value = (localStorage.getItem('app-theme') as Theme) || 'system'
  }
})

function toggleTheme() {
  const themeOrder: Theme[] = ['light', 'dark', 'system']
  const currentIndex = themeOrder.indexOf(currentTheme.value)
  const nextIndex = (currentIndex + 1) % themeOrder.length
  currentTheme.value = themeOrder[nextIndex]

  localStorage.setItem('app-theme', currentTheme.value)
  if (setGlobalTheme) setGlobalTheme(currentTheme.value)

  message.success(`已切换到 ${getThemeText()}`)
}

function getThemeText(): string {
  const themeTexts = {
    light: '亮色主题',
    dark: '暗色主题',
    system: '跟随系统'
  }
  return themeTexts[currentTheme.value]
}

function getThemeIcon(): string {
  const themeIcons = {
    light: 'i-ion-sunny',
    dark: 'i-ion-moon',
    system: 'i-ion-desktop-outline'
  }
  return themeIcons[currentTheme.value]
}

function getThemeLabel(): string {
  const themeOrder: Theme[] = ['light', 'dark', 'system']
  const currentIndex = themeOrder.indexOf(currentTheme.value)
  const nextIndex = (currentIndex + 1) % themeOrder.length
  const nextTheme = themeOrder[nextIndex]
  const nextThemeText = {
    light: '亮色主题',
    dark: '暗色主题',
    system: '跟随系统'
  }[nextTheme]
  return `切换到${nextThemeText}`
}
</script>

<style scoped>
.settings-container {
  max-width: var(--container-max-width-sm);
  margin: 0 auto;
  padding: var(--spacing-5);
  width: 100%;
  box-sizing: border-box;
}

.settings-card {
  border-radius: var(--card-border-radius-desktop);
  box-shadow: var(--card-shadow-medium);
  border: 1px solid var(--color-black-05);
  width: 100%;
  box-sizing: border-box;
}

.setting-section {
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px dashed var(--n-border-color);
}

.setting-section.small {
  border-bottom: none;
  padding-bottom: var(--spacing-3);
}

/* 设置行布局 */
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-5);
}

.setting-info {
  flex: 1;
}

.setting-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-1);
}

.setting-icon {
  color: var(--color-primary);
}

.setting-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--n-text-color);
}

.setting-description {
  color: var(--n-text-color-2);
  font-size: var(--font-size-xs);
  margin: 0;
}

/* 主题切换按钮 */
.theme-toggle-container {
  flex-shrink: 0;
}

.theme-toggle-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-3) var(--spacing-4);
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: var(--spacing-3);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  outline: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(12px);
  position: relative;
  overflow: hidden;
  min-width: 120px;
  justify-content: flex-start;
}

.theme-toggle-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.theme-toggle-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  border-color: rgba(0, 0, 0, 0.15);
}

.theme-toggle-btn:hover::before {
  opacity: 1;
}

.theme-toggle-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.theme-toggle-btn:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.theme-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: var(--spacing-8);
  height: var(--spacing-8);
  border-radius: var(--spacing-2);
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.theme-text {
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--n-text-color);
  transition: color 0.3s ease;
  white-space: nowrap;
}

/* 不同主题状态的样式 */
.theme-toggle-btn.theme-light .theme-icon-container {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  border-color: rgba(255, 215, 0, 0.3);
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.2);
}

.theme-toggle-btn.theme-dark .theme-icon-container {
  background: linear-gradient(135deg, #4a5568, #2d3748);
  border-color: rgba(74, 85, 104, 0.3);
  box-shadow: 0 2px 8px rgba(74, 85, 104, 0.2);
}

.theme-toggle-btn.theme-system .theme-icon-container {
  background: linear-gradient(135deg, var(--color-primary), rgba(var(--color-primary-rgb), 0.8));
  border-color: rgba(var(--color-primary-rgb), 0.3);
  box-shadow: 0 2px 8px rgba(var(--color-primary-rgb), 0.2);
}

.theme-icon {
  transition: all 0.3s ease;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.theme-toggle-btn.theme-light .theme-icon {
  color: #b45309;
}

.theme-toggle-btn.theme-dark .theme-icon {
  color: #e2e8f0;
}

.theme-toggle-btn.theme-system .theme-icon {
  color: white;
}

/* 暗色主题下的按钮样式 */
@media (prefers-color-scheme: dark) {
  .theme-toggle-btn {
    background: rgba(40, 40, 40, 0.9);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .theme-toggle-btn::before {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.02));
  }

  .theme-toggle-btn:hover {
    border-color: rgba(255, 255, 255, 0.2);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
  }

  .theme-text {
    color: rgba(255, 255, 255, 0.9);
  }

  .theme-icon-container {
    background: rgba(60, 60, 60, 0.8);
    border-color: rgba(255, 255, 255, 0.1);
  }
}



/* 移动端设置页面优化 */
@media (max-width: var(--breakpoint-tablet)) {
  .settings-container {
    padding: var(--container-responsive-padding-tablet);
    max-width: 100%;
  }

  .settings-card {
    border-radius: var(--card-border-radius-tablet);
    margin: 0;
    box-shadow: var(--card-shadow-tablet);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-5);
  }

  .setting-section.small {
    padding: var(--spacing-3) var(--spacing-5);
  }

  .setting-row {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: var(--spacing-4);
    min-height: var(--spacing-14);
  }

  .setting-info {
    flex: 1;
  }

  .setting-header {
    gap: var(--spacing-3); /* 12px */
    margin-bottom: var(--spacing-1); /* 4px */
  }

  .setting-icon {
    font-size: var(--font-size-lg);
  }

  .setting-title {
    font-size: var(--font-size-base);
    font-weight: 500;
    line-height: 1.4;
  }

  .setting-description {
    font-size: var(--font-size-sm);
    line-height: 1.4;
    margin-top: var(--spacing-1); /* 4px */
  }

  .theme-toggle-container {
    flex-shrink: 0;
  }

  .theme-toggle-btn {
    padding: var(--spacing-2) var(--spacing-3);
    min-width: 100px;
    min-height: var(--spacing-12);
    gap: var(--spacing-2);
  }

  .theme-icon-container {
    width: var(--spacing-7);
    height: var(--spacing-7);
  }

  .theme-text {
    font-size: var(--font-size-xs);
  }

  /* 表单组件优化 */
  :deep(.n-radio-group) {
    width: 100%;
  }

  :deep(.n-radio) {
    width: 100%;
    min-height: var(--spacing-12); /* 48px */
    padding: var(--spacing-2) var(--spacing-3);
  }

  :deep(.n-radio__dot) {
    transform: scale(1.1);
  }

  :deep(.n-card__header) {
    padding: var(--spacing-5) var(--spacing-5) 0;
  }

  :deep(.n-card__content) {
    padding: 0;
  }
}

@media (max-width: var(--breakpoint-mobile)) {
  .settings-container {
    padding: var(--container-responsive-padding-mobile);
  }

  .settings-card {
    border-radius: var(--card-border-radius-mobile);
    box-shadow: var(--card-shadow-mobile);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-4); /* 16px 16px */
  }

  .setting-section.small {
    padding: var(--spacing-3) var(--spacing-4); /* 12px 16px */
  }

  .setting-row {
    gap: var(--spacing-3);
    min-height: var(--spacing-13); /* 52px */
  }

  .setting-header {
    gap: var(--spacing-2);
  }

  .setting-icon {
    font-size: var(--font-size-base);
  }

  .setting-title {
    font-size: var(--font-size-base);
    font-weight: 500;
  }

  .setting-description {
    font-size: var(--font-size-xs);
  }

  .theme-toggle-btn {
    padding: var(--spacing-2);
    min-width: 90px;
    min-height: var(--spacing-11);
    gap: var(--spacing-2);
  }

  .theme-icon-container {
    width: var(--spacing-6);
    height: var(--spacing-6);
  }

  .theme-text {
    font-size: var(--font-size-xs);
  }

  :deep(.n-card__header) {
    padding: var(--spacing-4) var(--spacing-4) 0;
  }

  :deep(.n-radio) {
    min-height: var(--spacing-11);
    padding: var(--spacing-2) var(--spacing-3); /* 8px 12px */
  }
}

/* 横屏模式优化 */
@media (max-width: var(--breakpoint-tablet)) and (orientation: landscape) {
  .settings-container {
    padding: var(--spacing-2) var(--spacing-4);
  }

  .setting-section {
    padding: var(--spacing-3) var(--spacing-5);
  }

  .setting-row {
    min-height: var(--spacing-12);
  }

  .theme-toggle-btn {
    padding: var(--spacing-1);
    min-width: var(--spacing-11);
    min-height: var(--spacing-11);
  }
}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .settings-container {
    padding: var(--spacing-1) var(--spacing-2);
  }

  .settings-card {
    border-radius: var(--spacing-2);
  }

  .setting-section {
    padding: var(--spacing-2) var(--spacing-3);
  }

  .setting-row {
    min-height: var(--spacing-10);
    padding: var(--spacing-2) 0;
  }
}

/* 触摸优化 */
@media (hover: none) and (pointer: coarse) {
  .theme-toggle-btn {
    min-width: var(--spacing-12);
    min-height: var(--spacing-12);
    padding: var(--spacing-2);
  }

  .theme-toggle-btn:hover {
    transform: none;
    background: transparent;
  }

  .theme-toggle-btn:active {
    transform: scale(0.96);
    transition: transform 0.1s ease;
    background: var(--color-primary-08);
    border-radius: var(--border-radius-md);
  }

  .setting-row {
    min-height: var(--spacing-14);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-5);
  }
}
</style>
