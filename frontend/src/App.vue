<template>
  <n-config-provider
    :theme="naiveTheme"
    :theme-overrides="themeOverrides"
    :locale="naiveLocale"
    :date-locale="naiveDateLocale"
  >
    <n-dialog-provider>
      <n-message-provider>
        <AppLayout container-size="xl" content-padding="md" :responsive="true">
          <router-view />
        </AppLayout>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted} from "vue";
import {dateEnUS, dateZhCN, enUS, zhCN} from "naive-ui";
import {useThemeProvider} from "./composables/useTheme";
import {getCurrentLocale} from "./i18n";
import AppLayout from "./components/layout/AppLayout.vue";

// 使用主题管理 composable
const { naiveTheme, themeOverrides, provideTheme } = useThemeProvider();

// 为子组件提供主题功能
provideTheme();

// 响应式的 Naive UI 语言包配置
const naiveLocale = computed(() => {
  const currentLocale = getCurrentLocale();
  return currentLocale === 'en-US' ? enUS : zhCN;
});

const naiveDateLocale = computed(() => {
  const currentLocale = getCurrentLocale();
  return currentLocale === 'en-US' ? dateEnUS : dateZhCN;
});

// 监听语言切换事件，确保 Naive UI 组件响应语言变化
let localeChangeHandler: ((event: Event) => void) | null = null;

onMounted(() => {
  localeChangeHandler = () => {
    // 强制重新计算 computed 值
    // Vue 的响应式系统会自动处理，这里只是确保事件被正确监听
  };
  window.addEventListener('locale-changed', localeChangeHandler);
});

onUnmounted(() => {
  if (localeChangeHandler) {
    window.removeEventListener('locale-changed', localeChangeHandler);
  }
});
</script>

<style>
/**
 * App.vue 全局样式
 * 使用设计令牌系统，确保主题切换的平滑过渡
 */

/* 全局过渡效果 */
* {
  transition: color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    background-color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 确保应用占满整个视口 */
#app {
  min-height: 100vh;
  font-family: var(--font-family-base);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 主题切换时的背景色过渡 */
body {
  background-color: var(--app-bg-color, var(--n-body-color));
  color: var(--app-text-color, var(--n-text-color));
  margin: 0;
  padding: 0;
}

/* 移动端优化 */
@media (max-width: 767px) {
  #app {
    font-size: var(--font-size-sm);
  }
}
</style>
