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
          <button class="theme-toggle-btn" :class="{ 'dark-mode': isDarkMode }" @click="toggleTheme"
            :aria-label="isDarkMode ? '切换到亮色主题' : '切换到暗色主题'">
            <div class="toggle-track">
              <div class="toggle-thumb">
                <n-icon size="14" class="theme-icon">
                  <i :class="isDarkMode ? 'i-ion-moon' : 'i-ion-sunny'"></i>
                </n-icon>
              </div>
            </div>
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
import { ref, onMounted, inject } from 'vue'
import { useMessage } from 'naive-ui'
import PageContainer from '../components/common/PageContainer.vue'

type Theme = 'light' | 'dark'

const message = useMessage()
const isDarkMode = ref(false)

const setGlobalTheme = inject<(theme: Theme) => void>('setGlobalTheme')
const getGlobalTheme = inject<() => Theme>('getGlobalTheme')

onMounted(() => {
  let currentTheme: Theme
  if (getGlobalTheme) {
    currentTheme = getGlobalTheme()
  } else {
    currentTheme = (localStorage.getItem('app-theme') as Theme) || 'light'
  }
  isDarkMode.value = currentTheme === 'dark'
})

function toggleTheme() {
  isDarkMode.value = !isDarkMode.value
  const newTheme: Theme = isDarkMode.value ? 'dark' : 'light'

  localStorage.setItem('app-theme', newTheme)
  if (setGlobalTheme) setGlobalTheme(newTheme)

  message.success(`已切换到 ${isDarkMode.value ? '暗色主题' : '亮色主题'}`)
}
</script>

<style scoped>
.settings-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.settings-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.setting-section {
  padding: 16px 20px;
  border-bottom: 1px dashed var(--n-border-color);
}

.setting-section.small {
  border-bottom: none;
  padding-bottom: 12px;
}

/* 设置行布局 */
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.setting-info {
  flex: 1;
}

.setting-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.setting-icon {
  color: #18a058;
}

.setting-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--n-text-color);
}

.setting-description {
  color: var(--n-text-color-2);
  font-size: 13px;
  margin: 0;
}

/* 主题切换按钮 */
.theme-toggle-container {
  flex-shrink: 0;
}

.theme-toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  outline: none;
}

.theme-toggle-btn:hover {
  transform: scale(1.05);
}

.theme-toggle-btn:active {
  transform: scale(0.95);
}

.toggle-track {
  position: relative;
  width: 44px;
  height: 22px;
  background: #e5e7eb;
  border-radius: 11px;
  transition: background-color 0.3s ease;
}

.theme-toggle-btn.dark-mode .toggle-track {
  background: #18a058;
}

.toggle-thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 18px;
  height: 18px;
  background: white;
  border-radius: 50%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.theme-toggle-btn.dark-mode .toggle-thumb {
  transform: translateX(22px);
  background: white;
}

.theme-icon {
  color: #18a058;
  transition: color 0.3s ease;
}

.theme-toggle-btn.dark-mode .theme-icon {
  color: #18a058;
}



/* 移动端设置页面优化 */
@media (max-width: 768px) {
  .settings-container {
    padding: 12px;
    max-width: 100%;
  }

  .settings-card {
    border-radius: 12px;
    margin: 0;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  }

  .setting-section {
    padding: 16px 20px;
  }

  .setting-section.small {
    padding: 12px 20px;
  }

  .setting-row {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    min-height: 56px;
  }

  .setting-info {
    flex: 1;
  }

  .setting-header {
    gap: 10px;
    margin-bottom: 2px;
  }

  .setting-icon {
    font-size: 18px;
  }

  .setting-title {
    font-size: 16px;
    font-weight: 500;
    line-height: 1.4;
  }

  .setting-description {
    font-size: 13px;
    line-height: 1.4;
    margin-top: 2px;
  }

  .theme-toggle-container {
    flex-shrink: 0;
  }

  .theme-toggle-btn {
    padding: 6px;
    min-width: 48px;
    min-height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .toggle-track {
    width: 42px;
    height: 22px;
    border-radius: 11px;
  }

  .toggle-thumb {
    width: 18px;
    height: 18px;
    top: 2px;
    left: 2px;
  }

  .theme-toggle-btn.dark-mode .toggle-thumb {
    transform: translateX(20px);
  }

  /* 表单组件优化 */
  :deep(.n-radio-group) {
    width: 100%;
  }

  :deep(.n-radio) {
    width: 100%;
    min-height: 48px;
    padding: 8px 12px;
  }

  :deep(.n-radio__dot) {
    transform: scale(1.1);
  }

  :deep(.n-card__header) {
    padding: 20px 20px 0;
  }

  :deep(.n-card__content) {
    padding: 0;
  }
}

@media (max-width: 480px) {
  .settings-container {
    padding: 8px;
  }

  .settings-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  }

  .setting-section {
    padding: 14px 16px;
  }

  .setting-section.small {
    padding: 10px 16px;
  }

  .setting-row {
    gap: 12px;
    min-height: 52px;
  }

  .setting-header {
    gap: 8px;
  }

  .setting-icon {
    font-size: 16px;
  }

  .setting-title {
    font-size: 15px;
    font-weight: 500;
  }

  .setting-description {
    font-size: 12px;
  }

  .theme-toggle-btn {
    padding: 4px;
    min-width: 44px;
    min-height: 44px;
  }

  .toggle-track {
    width: 38px;
    height: 20px;
    border-radius: 10px;
  }

  .toggle-thumb {
    width: 16px;
    height: 16px;
    top: 2px;
    left: 2px;
  }

  .theme-toggle-btn.dark-mode .toggle-thumb {
    transform: translateX(18px);
  }

  :deep(.n-card__header) {
    padding: 16px 16px 0;
  }

  :deep(.n-radio) {
    min-height: 44px;
    padding: 6px 10px;
  }
}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .settings-container {
    padding: 8px 16px;
  }

  .setting-section {
    padding: 12px 20px;
  }

  .setting-row {
    min-height: 48px;
  }

  .theme-toggle-btn {
    padding: 4px;
    min-width: 44px;
    min-height: 44px;
  }
}

/* 触摸优化 */
@media (hover: none) and (pointer: coarse) {
  .theme-toggle-btn {
    min-width: 48px;
    min-height: 48px;
    padding: 6px;
  }

  .theme-toggle-btn:hover {
    transform: none;
    background: transparent;
  }

  .theme-toggle-btn:active {
    transform: scale(0.96);
    transition: transform 0.1s ease;
    background: rgba(24, 160, 88, 0.08);
    border-radius: 8px;
  }

  .setting-row {
    min-height: 56px;
  }

  .setting-section {
    padding: 16px 20px;
  }
}
</style>
