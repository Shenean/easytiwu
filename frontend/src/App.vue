<template>
  <n-config-provider :theme="naiveTheme" :theme-overrides="themeOverrides">
    <n-dialog-provider>
      <n-message-provider>
        <n-layout style="min-height: 100vh">
          <NavigationBar />
          <n-layout-content style="padding: 20px">
            <router-view />
          </n-layout-content>
        </n-layout>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, provide, watch } from 'vue'
import { darkTheme, type GlobalThemeOverrides } from 'naive-ui'
import NavigationBar from './components/NavigationBar.vue'
import { RouterView } from 'vue-router'

// 主题状态管理
const currentTheme = ref<'light' | 'dark' | 'auto'>('light')
const systemPrefersDark = ref(false)

// 计算实际应用的主题
const actualTheme = computed(() => {
  if (currentTheme.value === 'auto') {
    return systemPrefersDark.value ? 'dark' : 'light'
  }
  return currentTheme.value
})

// Naive UI 主题配置
const naiveTheme = computed(() => {
  return actualTheme.value === 'dark' ? darkTheme : null
})

// 主题覆盖配置
const themeOverrides = computed<GlobalThemeOverrides>(() => {
  const baseOverrides: GlobalThemeOverrides = {
    common: {
      primaryColor: '#18a058',
      primaryColorHover: '#36ad6a',
      primaryColorPressed: '#0c7a43',
      primaryColorSuppl: '#36ad6a',
    }
  }
  
  if (actualTheme.value === 'dark') {
    return {
      ...baseOverrides,
      common: {
        ...baseOverrides.common,
        bodyColor: '#1a1a1a',
        cardColor: '#2d2d2d',
        modalColor: '#2d2d2d',
        popoverColor: '#2d2d2d',
        tableHeaderColor: '#2d2d2d',
        tableColor: '#1a1a1a',
        inputColor: '#2d2d2d',
        codeColor: '#2d2d2d'
      }
    }
  }
  
  return baseOverrides
})

// 监听系统主题变化
const updateSystemTheme = () => {
  systemPrefersDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
}

// 设置全局主题
const setGlobalTheme = (theme: string) => {
  currentTheme.value = theme as 'light' | 'dark' | 'auto'
  localStorage.setItem('app-theme', theme)
  applyThemeToDocument()
}

// 获取当前主题
const getGlobalTheme = () => {
  return currentTheme.value
}

// 应用主题到文档
const applyThemeToDocument = () => {
  const theme = actualTheme.value
  document.documentElement.setAttribute('data-theme', theme)
  
  // 更新CSS变量
  if (theme === 'dark') {
    document.documentElement.style.setProperty('--bg-color', '#1a1a1a')
    document.documentElement.style.setProperty('--text-color', '#ffffff')
    document.documentElement.style.setProperty('--border-color', '#404040')
  } else {
    document.documentElement.style.setProperty('--bg-color', '#ffffff')
    document.documentElement.style.setProperty('--text-color', '#2c3e50')
    document.documentElement.style.setProperty('--border-color', '#e0e0e0')
  }
}

// 初始化主题
onMounted(() => {
  // 从localStorage读取保存的主题
  const savedTheme = localStorage.getItem('app-theme') || 'light'
  currentTheme.value = savedTheme as 'light' | 'dark' | 'auto'
  
  // 初始化系统主题检测
  updateSystemTheme()
  
  // 监听系统主题变化
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', updateSystemTheme)
  
  // 应用初始主题
  applyThemeToDocument()
})

// 监听主题变化
watch(actualTheme, () => {
  applyThemeToDocument()
})

// 提供主题方法给子组件
provide('setGlobalTheme', setGlobalTheme)
provide('getGlobalTheme', getGlobalTheme)
</script>

<style>
/* 全局主题样式 */
:root {
  --bg-color: #ffffff;
  --text-color: #2c3e50;
  --border-color: #e0e0e0;
}

[data-theme="dark"] {
  --bg-color: #1a1a1a;
  --text-color: #ffffff;
  --border-color: #404040;
}

body {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

* {
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
}
</style>