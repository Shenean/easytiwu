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
import {computed, onMounted, onUnmounted, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {MenuOutline} from "@vicons/ionicons5";
import {mainMenuOptions} from "../config/menuOptions";

const route = useRoute();
const router = useRouter();

// 响应式判断是否为移动端
const isMobile = computed(() => window.innerWidth <= 767);

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

// 监听窗口大小变化（轻量节流）
let resizeTimer: number | null = null;
onMounted(() => {
  const onResize = () => {
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = window.setTimeout(() => {
      // 强制触发响应式更新（虽然 computed 已监听，但确保边界情况）
    }, 150);
  };
  window.addEventListener("resize", onResize);
  onUnmounted(() => {
    window.removeEventListener("resize", onResize);
    if (resizeTimer) clearTimeout(resizeTimer);
  });
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
  padding: 0 var(--spacing-sm); /* 更紧凑的左右边距 */
  height: 48px; /* 紧凑高度：6×8基准 */
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 添加阴影增强层次感 */
  position: fixed; /* 固定定位 */
  top: 0; /* 固定在顶部 */
  left: 0; /* 从左边开始 */
  right: 0; /* 到右边结束 */
  z-index: 1000; /* 确保在其他元素之上 */
  background-color: var(--n-card-color, #ffffff); /* 确保有背景色，适配主题 */
  backdrop-filter: blur(8px); /* 添加毛玻璃效果 */
  -webkit-backdrop-filter: blur(8px); /* Safari兼容性 */
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
}

.logo-img {
  height: 32px; /* 紧凑Logo尺寸 */
  width: auto;
  object-fit: contain;
  user-select: none;
}

/* 桌面端菜单样式优化 */
:deep(.desktop-nav .n-menu-item-content) {
  padding: 0 var(--spacing-xs); /* 减少菜单项内边距 */
  font-size: 14px;
}

:deep(.desktop-nav .n-menu-item) {
  height: 48px; /* 与导航栏同高 */
  display: flex;
  align-items: center;
}

/* 移动端适配 */
@media (max-width: 767px) {
  .desktop-nav {
    display: none;
  }

  .navbar {
    padding: 0 var(--spacing-xs); /* 移动端更紧凑 */
    height: 48px;
    /* 移动端保持固定定位 */
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
  }

  .logo-img {
    height: 28px; /* 移动端稍小 */
  }

  .mobile-menu-btn {
    margin-left: auto; /* 确保靠右（虽然space-between已满足，保险） */
  }
}
</style>
