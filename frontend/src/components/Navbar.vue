<template>
  <n-layout-header bordered class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/" class="logo" aria-label="返回首页">
        EasyTiwu
      </router-link>

      <!-- 桌面端菜单 -->
      <n-menu
        v-if="!isMobile"
        :value="currentRouteName"
        mode="horizontal"
        :options="menuOptions"
        @update:value="handleMenuSelect"
        class="menu"
        :disabled-keys="disabledKeys"
      />

      <!-- 移动端汉堡菜单 -->
      <n-dropdown
        v-else
        :options="menuOptions"
        trigger="click"
        placement="bottom-end"
        animation="fade-in-scale-up-transition"
        @select="handleMenuSelect"
      >
        <n-button text aria-label="菜单" class="btn-ripple">
          <n-icon size="24">
            <i class="i-ion-menu-outline"></i>
          </n-icon>
        </n-button>
      </n-dropdown>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useWindowSize } from '@vueuse/core'
import { mainMenuOptions } from '../config/menuOptions'

const route = useRoute()
const router = useRouter()

// 当前路由名
const currentRouteName = computed(() => {
  return (route.name?.toString() || 'upload') as string
})

// 禁用当前所在路由项
const disabledKeys = computed(() => [currentRouteName.value])

// 菜单配置项
const menuOptions = ref<MenuOption[]>(mainMenuOptions)

// 菜单选择跳转
const handleMenuSelect = (key: string) => {
  if (key === currentRouteName.value) return
  router.push({ name: key }).catch(err => {
    console.warn('[Router] Navigation cancelled or duplicated:', err)
  })
}

// 响应式判断是否为移动端
const { width } = useWindowSize()
const MOBILE_BREAKPOINT = 768
const isMobile = computed(() => width.value < MOBILE_BREAKPOINT)

// 路由变化监听（可选增强）
let unwatchRoute: (() => void) | null = null
onMounted(() => {
  unwatchRoute = router.afterEach(() => {
    // currentRouteName 已自动更新
  })
})
onUnmounted(() => {
  if (unwatchRoute) unwatchRoute()
})

// 暴露方法（可选）
defineExpose({
  refreshMenu: () => {
    // 预留外部刷新入口
  }
})
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 64px;
  padding: 0 20px;
  display: flex;
  align-items: center;

  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px 16px 0 0;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);

  transition: background-color 0.3s, box-shadow 0.3s;
  animation: navbar-fade-in 0.6s ease-out both;
}

@keyframes navbar-fade-in {
  from { opacity: 0; transform: translateY(-8px); }
  to   { opacity: 1; transform: translateY(0); }
}

.navbar-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-weight: 700;
  font-size: 22px;
  color: #2c3e50;
  text-decoration: none;
  padding: 0 15px;
  letter-spacing: -0.5px;
  transition: color 0.2s, transform 0.2s;
}
.logo:hover {
  color: #18a058;
  transform: scale(1.05);
}

.menu {
  display: flex;
  align-items: center;
  height: 100%;
  background: transparent;
}

:deep(.n-menu-item-content) {
  font-weight: 500;
  transition: color 0.2s, transform 0.2s;
}
:deep(.n-menu-item-content:hover),
:deep(.n-menu-item.n-menu-item--active .n-menu-item-content) {
  color: #18a058 !important;
  transform: scale(1.03);
}

/* 毛玻璃叠加渐变 */
.navbar::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px 16px 0 0;
  background: linear-gradient(135deg,
    rgba(255, 255, 255, 0.25) 0%,
    rgba(255, 255, 255, 0.05) 100%);
  pointer-events: none;
  z-index: -1;
}

/* Ripple 效果 */
.btn-ripple {
  position: relative;
  overflow: hidden;
}
.btn-ripple::after {
  content: '';
  position: absolute;
  left: 50%; top: 50%;
  width: 0; height: 0;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.4s, height 0.4s;
}
.btn-ripple:active::after {
  width: 120%; height: 120%;
  transition: 0s;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .navbar { padding: 0 12px; height: 56px; }
  .logo   { font-size: 20px; padding: 0 8px; }
  .n-button { padding: 6px 10px; }
}
</style>
