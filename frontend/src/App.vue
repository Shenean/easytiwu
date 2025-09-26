<template>
  <t-config-provider :global-config="tdesignConfig">
    <AppLayout 
      container-size="xl" 
      content-padding="md"
      :show-header="showLayoutElements"
      :show-footer="showLayoutElements"
    >
      <router-view />
    </AppLayout>
  </t-config-provider>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted} from "vue";
import {useRoute} from "vue-router";
import {useThemeProvider} from "./composables/useTheme";
import AppLayout from "./components/layout/AppLayout.vue";

const route = useRoute();

const { provideTheme } = useThemeProvider();

provideTheme();

const showLayoutElements = computed(() => {
  const authRoutes = ['login', 'register'];
  return !authRoutes.includes(route.name as string);
});

const tdesignConfig = computed(() => {
  return {
    classPrefix: "t",
  };
});

let localeChangeHandler: ((event: Event) => void) | null = null;

onMounted(() => {
  localeChangeHandler = () => {};
  window.addEventListener("locale-changed", localeChangeHandler);
});

onUnmounted(() => {
  if (localeChangeHandler) {
    window.removeEventListener("locale-changed", localeChangeHandler);
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
  background-color: var(--app-bg-color, var(--td-bg-color-page));
  color: var(--app-text-color, var(--td-text-color-primary));
  margin: 0;
  padding: 0;
}
</style>
