<template>
  <div class="settings-container">
    <n-card title="设置" class="settings-card">
      <div class="setting-section">
        <div class="setting-header">
          <n-icon size="20" class="setting-icon">
            <i class="i-ion-color-palette-outline"></i>
          </n-icon>
          <h3 class="setting-title">主题设置</h3>
        </div>

        <div class="setting-description">选择您喜欢的应用主题</div>

        <n-radio-group v-model:value="currentTheme" class="theme-group" @update:value="handleThemeChange">
          <div class="theme-list">
            <label
              v-for="t in themes"
              :key="t.value"
              class="theme-item"
              :class="{ selected: currentTheme === t.value }"
            >
              <n-radio :value="t.value" class="theme-radio">
                <div class="preview" :data-variant="t.value" />
                <div class="theme-label">{{ t.label }}</div>
              </n-radio>
            </label>
          </div>
        </n-radio-group>
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
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, inject } from 'vue'
import { useMessage } from 'naive-ui'

type Theme = 'light' | 'dark' | 'auto'

const message = useMessage()
const currentTheme = ref<Theme>('light')

const setGlobalTheme = inject<(theme: Theme) => void>('setGlobalTheme')
const getGlobalTheme = inject<() => Theme>('getGlobalTheme')

const themes: { value: Theme; label: string }[] = [
  { value: 'light', label: '浅色主题 🌞' },
  { value: 'dark', label: '深色主题 🌙' },
  { value: 'auto', label: '跟随系统' }
]

onMounted(() => {
  if (getGlobalTheme) {
    currentTheme.value = getGlobalTheme()
  } else {
    currentTheme.value = (localStorage.getItem('app-theme') as Theme) || 'light'
  }
})

function handleThemeChange(theme: Theme) {
  currentTheme.value = theme
  localStorage.setItem('app-theme', theme)
  if (setGlobalTheme) setGlobalTheme(theme)

  const names = { light: '浅色主题', dark: '深色主题', auto: '跟随系统' }
  message.success(`已切换到 ${names[theme]}`)
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
  background: rgba(255,255,255,0.85);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  border: 1px solid rgba(0,0,0,0.05);
}

.setting-section {
  padding: 16px 20px;
  border-bottom: 1px dashed var(--n-border-color);
}

.setting-section.small { border-bottom: none; padding-bottom: 12px; }

.setting-header { display:flex; align-items:center; gap:12px; margin-bottom:8px; }
.setting-icon { color: #18a058; }
.setting-title { margin:0; font-size:16px; font-weight:600; color:var(--n-text-color); }
.setting-description { color:var(--n-text-color-2); margin-bottom:12px; font-size:13px; }

/* 主题列表 */
.theme-group { width:100%; }
.theme-list { display:flex; gap:14px; flex-wrap:wrap; justify-content:center; }
.theme-item {
  display:flex;
  flex-direction:column;
  align-items:center;
  width:220px;
  cursor:pointer;
  user-select:none;
  padding:8px;
  border-radius:10px;
  transition:transform .18s, box-shadow .18s;
}
.theme-item:hover { transform:translateY(-4px); box-shadow:0 8px 20px rgba(0,0,0,0.06); }

.theme-radio { width:100%; display:flex; flex-direction:column; align-items:center; gap:8px; padding:0; }
.preview {
  width:100%;
  height:96px;
  border-radius:8px;
  border:1px solid rgba(0,0,0,0.06);
  box-sizing:border-box;
  background-size:cover;
}

/* 根据 data-variant 简洁着色 */
.preview[data-variant="light"] { background: linear-gradient(180deg,#fff,#f6f6f8); }
.preview[data-variant="dark"] { background: linear-gradient(180deg,#1b1b1b,#111); }
.preview[data-variant="auto"] {
  background: linear-gradient(90deg,#fff 50%, #111 50%);
  position:relative;
}

/* 选中态 */
.theme-item.selected { outline: 3px solid rgba(24,160,88,0.12); box-shadow:0 10px 30px rgba(24,160,88,0.08); }

/* 标签 */
.theme-label { font-size:14px; color:var(--n-text-color); font-weight:500; text-align:center; }

/* 移动端设置页面优化 */
@media (max-width: 768px) {
  .settings-container {
    padding: 16px;
  }
  
  .settings-card {
    border-radius: 8px;
    margin: 0 -4px;
  }
  
  .setting-section {
    padding: 12px 16px;
  }
  
  .setting-section.small {
    padding-bottom: 8px;
  }
  
  .setting-header {
    gap: 8px;
    margin-bottom: 6px;
  }
  
  .setting-title {
    font-size: 15px;
  }
  
  .setting-description {
    font-size: 12px;
    margin-bottom: 10px;
  }
  
  .theme-list {
    gap: 10px;
    justify-content: space-around;
  }
  
  .theme-item {
    width: 100px;
    padding: 6px;
  }
  
  .theme-radio {
    gap: 6px;
  }
  
  .preview {
    height: 60px;
    border-radius: 6px;
  }
  
  .theme-label {
    font-size: 12px;
  }
  
  /* 表单组件优化 */
  :deep(.n-radio-group) {
    width: 100%;
  }
  
  :deep(.n-radio) {
    width: 100%;
    min-height: 44px;
  }
  
  :deep(.n-radio__dot) {
    transform: scale(1.2);
  }
  
  :deep(.n-card__header) {
    padding: 16px 16px 0;
  }
  
  :deep(.n-card__content) {
    padding: 0;
  }
}

@media (max-width: 480px) {
  .settings-container {
    padding: 12px;
  }
  
  .setting-section {
    padding: 10px 12px;
  }
  
  .setting-title {
    font-size: 14px;
  }
  
  .setting-description {
    font-size: 11px;
  }
  
  .theme-item {
    width: 90px;
    padding: 4px;
  }
  
  .preview {
    height: 50px;
  }
  
  .theme-label {
    font-size: 11px;
  }
  
  :deep(.n-card__header) {
    padding: 12px 12px 0;
  }
}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .theme-list {
    justify-content: center;
    gap: 12px;
  }
  
  .theme-item {
    width: 80px;
  }
  
  .preview {
    height: 45px;
  }
}

/* 触摸优化 */
@media (hover: none) and (pointer: coarse) {
  .theme-item:hover {
    transform: none;
    box-shadow: none;
  }
  
  .theme-item:active {
    transform: scale(0.98);
    transition: transform 0.1s;
  }
}

</style>
