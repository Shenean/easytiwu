<template>
  <n-config-provider
    :theme="naiveTheme"
    :theme-overrides="themeOverrides"
    :locale="naiveLocale"
    :date-locale="naiveDateLocale"
  >
    <n-dialog-provider>
      <n-message-provider>
        <AppLayout container-size="xl" content-padding="md">
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


const { naiveTheme, themeOverrides, provideTheme } = useThemeProvider();


provideTheme();


const naiveLocale = computed(() => {
  const currentLocale = getCurrentLocale();
  return currentLocale === 'en-US' ? enUS : zhCN;
});

const naiveDateLocale = computed(() => {
  const currentLocale = getCurrentLocale();
  return currentLocale === 'en-US' ? dateEnUS : dateZhCN;
});


let localeChangeHandler: ((event: Event) => void) | null = null;

onMounted(() => {
  localeChangeHandler = () => {

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

* {
  transition: color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    background-color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    border-color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}


#app {
  min-height: 100vh;
  font-family: var(--font-family-base);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}


body {
  background-color: var(--app-bg-color, var(--n-body-color));
  color: var(--app-text-color, var(--n-text-color));
  margin: 0;
  padding: 0;
}


</style>
