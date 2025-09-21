<template>
  <n-layout-header bordered class="navbar" tag="header">
    <div class="navbar-container">
      <router-link to="/" class="logo" :aria-label="t('navigation.backToHome')">
        <img src="/EasyTiwu.png" alt="EasyTiwu" class="logo-img" draggable="false" />
      </router-link>

      <n-menu :value="currentRouteName" mode="horizontal" :options="menuOptions" @update:value="handleNavigate"
        class="desktop-nav" :disabled-keys="[currentRouteName]" />
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import {computed} from "vue";
import {NLayoutHeader, NMenu} from "naive-ui";
import {useRoute, useRouter} from "vue-router";
import {mainMenuOptions} from "../config/menuOptions";
import {useI18n} from "vue-i18n";

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const currentRouteName = computed(() => route.name?.toString() || "upload");

const menuOptions = computed(() => {
  return mainMenuOptions.filter((option) => !option.meta?.hidden);
});

const handleNavigate = (key: string) => {
  if (key === currentRouteName.value) return;
  router.push({ name: key }).catch((err) => {
    if (err.name !== "NavigationDuplicated") {
    }
  });
};
</script>

<style scoped>
.navbar {
  padding: 0 var(--spacing-sm);
  height: var(--nav-height);
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-sm);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  backdrop-filter: blur(var(--spacing-1));
  -webkit-backdrop-filter: blur(var(--spacing-1));
  transition: background-color 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
}

.navbar-container {
  width: 100%;
  max-width: var(--container-medium-width);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-sm);
}

.logo {
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: opacity 0.2s ease;
}

.logo:hover {
  opacity: 0.8;
}

.logo-img {
  height: var(--spacing-4);
  width: auto;
  object-fit: contain;
  user-select: none;
  transition: filter 0.3s ease;
}

:global(.dark) .logo-img {
  filter: brightness(0.9) contrast(1.1);
}

:deep(.desktop-nav) {
  background: transparent;
}

:deep(.desktop-nav .n-menu-item-content) {
  padding: 0 var(--spacing-xs);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  transition: color 0.2s ease;
}

:deep(.desktop-nav .n-menu-item) {
  height: var(--nav-height);
  display: flex;
  align-items: center;
  border-radius: var(--border-radius-md);
  transition: background-color 0.2s ease;
}

:deep(.desktop-nav .n-menu-item:hover .n-menu-item-content) {
  color: var(--color-primary-600);
}

:deep(.desktop-nav .n-menu-item--selected .n-menu-item-content) {
  color: var(--color-primary-600);
  font-weight: var(--font-weight-semibold);
}

:deep(.desktop-nav .n-menu-item--disabled .n-menu-item-content) {
  color: var(--color-text-disabled);
}
</style>
