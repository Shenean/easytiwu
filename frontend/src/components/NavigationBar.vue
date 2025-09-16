<template>
  <n-layout-header bordered class="navbar" tag="header">
    <div class="navbar-container">
      <!-- Logo (居左) -->
      <router-link
        to="/"
        class="logo"
        :aria-label="$t('navigation.backToHome')"
      >
        <img
          src="/EasyTiwu.png"
          alt="EasyTiwu"
          class="logo-img"
          draggable="false"
        />
      </router-link>

      <!-- 桌面端菜单 (居右) -->
      <n-menu
        v-if="!isMobile"
        :value="currentRouteName"
        mode="horizontal"
        :options="menuOptions"
        @update:value="handleNavigate"
        class="desktop-nav"
        :disabled-keys="[currentRouteName]"
      />

      <!-- 移动端汉堡按钮 (居右) -->
      <n-button
        v-else
        text
        @click="showMobileMenu = true"
        class="mobile-menu-btn"
        :aria-label="$t('navigation.openMenu')"
      >
        <template #icon>
          <n-icon :size="20">
            <MenuOutline />
          </n-icon>
        </template>
      </n-button>
    </div>

    <!-- 移动端抽屉菜单 -->
    <n-drawer v-model:show="showMobileMenu" :width="240" placement="right">
      <n-drawer-content :title="$t('navigation.menu')" closable>
        <n-menu
          :value="currentRouteName"
          :options="menuOptions"
          @update:value="handleNavigateAndClose"
        />
      </n-drawer-content>
    </n-drawer>
  </n-layout-header>
</template>

<script setup lang="ts">
import {computed, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {MenuOutline} from "@vicons/ionicons5";
import {mainMenuOptions} from "../config/menuOptions";
import {useBreakpoints} from "@/composables/useBreakpoints";
import {useTheme} from "@/composables/useTheme";

const route = useRoute();
const router = useRouter();

// 使用响应式断点检测
const { isMobile } = useBreakpoints();

// 使用主题系统
const { actualTheme } = useTheme();

// 移动端菜单状态
const showMobileMenu = ref(false);

// 当前路由名（安全兜底）
const currentRouteName = computed(() => route.name?.toString() || "upload");

// 过滤菜单项
const menuOptions = computed(() => {
  return mainMenuOptions.filter((option) => !option.meta?.hidden);
});

// 统一导航处理
const handleNavigate = (key: string) => {
  if (key === currentRouteName.value) return;
  router.push({ name: key }).catch((err) => {
    if (err.name !== "NavigationDuplicated") {
      // 静默处理导航错误
    }
  });
};

// 移动端导航 + 自动关闭
const handleNavigateAndClose = (key: string) => {
  handleNavigate(key);
  showMobileMenu.value = false;
};

// 主题变化时的处理逻辑
watch(actualTheme, (newTheme) => {
  // 主题切换时可以添加额外的处理逻辑
  // 例如：更新页面标题、发送分析事件等
  console.log(`主题已切换至: ${newTheme}`);
});

// 路由变化时关闭移动端菜单
watch(
  () => route.fullPath,
  () => {
    if (showMobileMenu.value && isMobile.value) {
      showMobileMenu.value = false;
    }
  }
);
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
  /* 主题适配的背景色 */
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  backdrop-filter: blur(var(--spacing-1));
  -webkit-backdrop-filter: blur(var(--spacing-1));
  /* 主题切换过渡效果 */
  transition: background-color 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
}

.navbar-container {
  width: 100%;
  max-width: var(--container-medium-width);
  margin: 0 auto;
  display: flex;
  justify-content: space-between; /* Logo左，菜单右 */
  align-items: center;
  gap: var(--spacing-sm); /* 可选：项目间微间距 */
}

.logo {
  text-decoration: none;
  display: flex;
  align-items: center;
  /* 主题适配的过渡效果 */
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
  /* 主题适配的过渡效果 */
  transition: filter 0.3s ease;
}

/* 暗色主题下的Logo适配 */
:global(.dark) .logo-img {
  filter: brightness(0.9) contrast(1.1);
}

/* 桌面端菜单样式优化 */
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

/* 移动端汉堡按钮样式 */
.mobile-menu-btn {
  margin-left: auto;
  color: var(--color-text-primary);
  transition: color 0.2s ease, background-color 0.2s ease;
}

.mobile-menu-btn:hover {
  background-color: var(--color-surface-hover);
  color: var(--color-primary-600);
}

/* 移动端抽屉菜单样式优化 */
:deep(.n-drawer .n-drawer-content) {
  background-color: var(--color-surface);
}

:deep(.n-drawer .n-menu-item-content) {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  transition: color 0.2s ease;
}

:deep(.n-drawer .n-menu-item:hover .n-menu-item-content) {
  color: var(--color-primary-600);
}

:deep(.n-drawer .n-menu-item--selected .n-menu-item-content) {
  color: var(--color-primary-600);
  font-weight: var(--font-weight-semibold);
}

/* 移动端适配 */
@media (max-width: 767px) {
  .desktop-nav {
    display: none;
  }

  .navbar {
    padding: 0 var(--spacing-xs);
    height: var(--nav-height);
  }

  .logo-img {
    height: calc(var(--spacing-3) + var(--spacing-1));
  }
}
</style>
