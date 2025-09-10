<template>
  <div class="settings-container">
    <n-card title="设置" class="settings-card">
      <div class="settings-content">
        <!-- 主题设置 -->
        <div class="setting-section">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <i class="i-ion-color-palette-outline"></i>
            </n-icon>
            <h3 class="setting-title">主题设置</h3>
          </div>
          <div class="setting-description">
            选择您喜欢的应用主题
          </div>
          <div class="theme-options">
            <n-radio-group
              v-model:value="currentTheme"
              @update:value="handleThemeChange"
              class="theme-radio-group"
            >
              <div class="theme-option">
                <n-radio value="light" class="theme-radio">
                  <div class="theme-preview light-preview">
                    <div class="preview-header"></div>
                    <div class="preview-content">
                      <div class="preview-sidebar"></div>
                      <div class="preview-main"></div>
                    </div>
                  </div>
                  <span class="theme-label">浅色主题</span>
                </n-radio>
              </div>
              <div class="theme-option">
                <n-radio value="dark" class="theme-radio">
                  <div class="theme-preview dark-preview">
                    <div class="preview-header"></div>
                    <div class="preview-content">
                      <div class="preview-sidebar"></div>
                      <div class="preview-main"></div>
                    </div>
                  </div>
                  <span class="theme-label">深色主题</span>
                </n-radio>
              </div>
              <div class="theme-option">
                <n-radio value="auto" class="theme-radio">
                  <div class="theme-preview auto-preview">
                    <div class="preview-header">
                      <div class="preview-sun"></div>
                      <div class="preview-moon"></div>
                    </div>
                    <div class="preview-content">
                      <div class="preview-sidebar"></div>
                      <div class="preview-main"></div>
                    </div>
                  </div>
                  <span class="theme-label">跟随系统</span>
                </n-radio>
              </div>
            </n-radio-group>
          </div>
        </div>

        <!-- 其他设置预留 -->
        <div class="setting-section">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <i class="i-ion-settings-outline"></i>
            </n-icon>
            <h3 class="setting-title">其他设置</h3>
          </div>
          <div class="setting-description">
            更多功能即将推出...
          </div>
        </div>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, inject } from 'vue'
import { useMessage } from 'naive-ui'

const message = useMessage()

// 主题相关
const currentTheme = ref<'light' | 'dark' | 'auto'>('light')

// 注入全局主题方法（稍后在App.vue中提供）
const setGlobalTheme = inject<(theme: string) => void>('setGlobalTheme')
const getGlobalTheme = inject<() => string>('getGlobalTheme')

// 初始化主题
onMounted(() => {
  if (getGlobalTheme) {
    currentTheme.value = getGlobalTheme() as 'light' | 'dark' | 'auto'
  } else {
    // 从localStorage读取
    const savedTheme = localStorage.getItem('app-theme') || 'light'
    currentTheme.value = savedTheme as 'light' | 'dark' | 'auto'
  }
})

// 处理主题切换
const handleThemeChange = (theme: 'light' | 'dark' | 'auto') => {
  currentTheme.value = theme
  
  // 保存到localStorage
  localStorage.setItem('app-theme', theme)
  
  // 调用全局主题设置方法
  if (setGlobalTheme) {
    setGlobalTheme(theme)
  }
  
  // 显示成功消息
  const themeNames = {
    light: '浅色主题',
    dark: '深色主题',
    auto: '跟随系统'
  }
  message.success(`已切换到${themeNames[theme]}`)
}
</script>

<style scoped>
.settings-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.settings-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.settings-content {
  padding: 20px 0;
}

.setting-section {
  margin-bottom: 40px;
}

.setting-section:last-child {
  margin-bottom: 0;
}

.setting-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.setting-icon {
  margin-right: 12px;
  color: #18a058;
}

.setting-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--n-text-color);
}

.setting-description {
  color: var(--n-text-color-2);
  margin-bottom: 20px;
  font-size: 14px;
}

.theme-options {
  margin-top: 16px;
}

.theme-radio-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.theme-option {
  flex: 1;
  min-width: 200px;
}

.theme-radio {
  width: 100%;
  padding: 0;
}

:deep(.n-radio__label) {
  width: 100%;
  padding: 0;
}

.theme-preview {
  width: 100%;
  height: 120px;
  border-radius: 12px;
  border: 2px solid transparent;
  overflow: hidden;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.theme-radio:hover .theme-preview {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

:deep(.n-radio--checked) .theme-preview {
  border-color: #18a058;
  box-shadow: 0 0 0 3px rgba(24, 160, 88, 0.2);
}

/* 浅色主题预览 */
.light-preview {
  background: #ffffff;
}

.light-preview .preview-header {
  height: 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
}

.light-preview .preview-content {
  display: flex;
  height: 90px;
}

.light-preview .preview-sidebar {
  width: 30%;
  background: #f8f9fa;
  border-right: 1px solid #dee2e6;
}

.light-preview .preview-main {
  flex: 1;
  background: #ffffff;
}

/* 深色主题预览 */
.dark-preview {
  background: #1a1a1a;
}

.dark-preview .preview-header {
  height: 30px;
  background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%);
  border-bottom: 1px solid #404040;
}

.dark-preview .preview-content {
  display: flex;
  height: 90px;
}

.dark-preview .preview-sidebar {
  width: 30%;
  background: #2d2d2d;
  border-right: 1px solid #404040;
}

.dark-preview .preview-main {
  flex: 1;
  background: #1a1a1a;
}

/* 自动主题预览 */
.auto-preview {
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 50%, #1a1a1a 50%, #1a1a1a 100%);
}

.auto-preview .preview-header {
  height: 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f8f9fa 50%, #2d2d2d 50%, #2d2d2d 100%);
  border-bottom: 1px solid #dee2e6;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-sun {
  width: 12px;
  height: 12px;
  background: #ffd43b;
  border-radius: 50%;
  position: absolute;
  left: 20%;
  animation: rotate 3s linear infinite;
}

.preview-moon {
  width: 12px;
  height: 12px;
  background: #74c0fc;
  border-radius: 50%;
  position: absolute;
  right: 20%;
  clip-path: inset(0 0 0 30%);
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.auto-preview .preview-content {
  display: flex;
  height: 90px;
}

.auto-preview .preview-sidebar {
  width: 30%;
  background: linear-gradient(135deg, #f8f9fa 0%, #f8f9fa 50%, #2d2d2d 50%, #2d2d2d 100%);
  border-right: 1px solid #dee2e6;
}

.auto-preview .preview-main {
  flex: 1;
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 50%, #1a1a1a 50%, #1a1a1a 100%);
}

.theme-label {
  display: block;
  text-align: center;
  font-weight: 500;
  color: var(--n-text-color);
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-container {
    padding: 16px;
  }
  
  .theme-radio-group {
    flex-direction: column;
    gap: 16px;
  }
  
  .theme-option {
    min-width: auto;
  }
  
  .theme-preview {
    height: 100px;
  }
}
</style>