<template>
  <n-layout-header bordered class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/" class="logo" aria-label="返回首页">
        <img src="/EasyTiwu.png" alt="EasyTiwu" class="logo-img" />
      </router-link>

      <!-- 桌面端菜单 -->
      <n-menu :value="currentRouteName" mode="horizontal" :options="menuOptions" @update:value="handleMenuSelect"
        class="menu desktop-nav" :disabled-keys="disabledKeys" />

      <!-- 移动端汉堡菜单 -->
      <div class="mobile-nav">
        <BaseButton text @click="showMobileMenu = !showMobileMenu" class="mobile-menu-btn">
          <template #icon>
            <n-icon size="20">
              <MenuOutline />
            </n-icon>
          </template>
        </BaseButton>
      </div>
    </div>

    <!-- 移动端下拉菜单 -->
    <div v-if="showMobileMenu" class="mobile-menu">
      <div v-for="option in menuOptions" :key="option.key" class="mobile-menu-item"
        @click="navigateAndClose(option.key as string)">
        <span>{{ option.label }}</span>
      </div>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import type {MenuOption} from 'naive-ui'
import {MenuOutline} from '@vicons/ionicons5'
import {mainMenuOptions} from '../config/menuOptions'
import BaseButton from './common/BaseButton.vue'

const route = useRoute()
const router = useRouter()

// 移动端菜单显示状态
const showMobileMenu = ref(false)

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

// 移动端导航并关闭菜单
const navigateAndClose = (key: string) => {
  showMobileMenu.value = false
  handleMenuSelect(key)
}



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
  height: 56px;
  padding: 0 20px;
  display: flex;
  align-items: center;

  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 12px 12px 0 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  transition: background-color 0.3s, box-shadow 0.3s;
  animation: navbar-fade-in 0.6s ease-out both;
}

@keyframes navbar-fade-in {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
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
  text-decoration: none;
  padding: 0 15px;
  transition: transform 0.2s;
  display: flex;
  align-items: center;
}

.logo:hover {
  transform: scale(1.05);
}

.logo-img {
  height: 40px;
  width: auto;
  object-fit: contain;
}

.menu {
  display: flex;
  align-items: center;
  height: 100%;

}

/* 响应式导航 */
.desktop-nav {
  display: flex;
}

.mobile-nav {
  display: none;
}

.mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 999;
  border-radius: 0 0 12px 12px;
}

.mobile-menu-item {
  padding: 12px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: rgba(51, 54, 57, 0.9);
  font-size: 15px;
  min-height: 44px;
}

.mobile-menu-item:hover {
  background-color: rgba(24, 160, 88, 0.12);
  color: #18a058;
  transform: translateX(4px);
  font-weight: 700;
}

.mobile-menu-item:active {
  background-color: rgba(24, 160, 88, 0.18);
  transform: translateX(2px) scale(0.98);
}

.mobile-menu-item:last-child {
  border-bottom: none;
}

.mobile-menu-btn {
  padding: 8px;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(4px);
  transition: all 0.2s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
  min-width: 40px;
  min-height: 40px;
}

.mobile-menu-btn:hover {
  background-color: rgba(24, 160, 88, 0.1);
  border-color: rgba(24, 160, 88, 0.2);
  transform: scale(1.05);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .mobile-nav {
    display: flex;
  }

  .navbar {
    padding: 0 16px;
    height: 52px;
    background-color: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(12px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }

  .logo {
    padding: 0 8px;
  }

  .logo-img {
    height: 32px;
  }

  .mobile-menu {
    background-color: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(16px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  }

  .mobile-menu-item {
    padding: 14px 20px;
    font-size: 15px;
    min-height: 48px;
    color: rgba(51, 54, 57, 0.95);
  }

  .mobile-menu-item:hover {
    background-color: rgba(24, 160, 88, 0.15);
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 0 12px;
    height: 48px;
  }

  .logo {
    padding: 0 4px;
  }

  .logo-img {
    height: 28px;
  }

  .mobile-menu-item {
    padding: 12px 18px;
    font-size: 14px;
    min-height: 42px;
  }

  .mobile-menu-btn {
    padding: 6px;
    min-width: 36px;
    min-height: 36px;
  }
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

  pointer-events: none;
  z-index: -1;
}

/* 暗色主题适配 */
@media (prefers-color-scheme: dark) {
  .navbar {
    background-color: rgba(16, 16, 20, 0.95);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .mobile-menu {
    background-color: rgba(16, 16, 20, 0.98);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  }

  .mobile-menu-item {
    color: rgba(255, 255, 255, 0.9);
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }

  .mobile-menu-item:hover {
    background-color: rgba(24, 160, 88, 0.2);
    color: #4ade80;
  }

  .mobile-menu-item:active {
    background-color: rgba(24, 160, 88, 0.25);
  }

  .mobile-menu-btn {
    background-color: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .mobile-menu-btn:hover {
    background-color: rgba(24, 160, 88, 0.15);
    border-color: rgba(24, 160, 88, 0.3);
  }

  @media (max-width: 768px) {
    .navbar {
      background-color: rgba(16, 16, 20, 0.98);
      border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    }

    .mobile-menu {
      background-color: rgba(16, 16, 20, 0.98);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.5);
    }

    .mobile-menu-item {
      color: rgba(255, 255, 255, 0.95);
    }

    .mobile-menu-item:hover {
      background-color: rgba(24, 160, 88, 0.18);
    }
  }
}

/* Ripple 效果 */
.btn-ripple {
  position: relative;
  overflow: hidden;
}

.btn-ripple::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 0;
  height: 0;

  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.4s, height 0.4s;
}

.btn-ripple:active::after {
  width: 120%;
  height: 120%;
  transition: 0s;
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .mobile-menu-item {
    min-height: 48px;
    padding: 16px 20px;
    font-size: 16px;
  }

  .mobile-menu-btn {
    min-width: 44px;
    min-height: 44px;
    padding: 10px;
  }

  .mobile-menu-item:hover {
    transform: none;
  }

  .mobile-menu-item:active {
    transform: scale(0.98);
    background-color: rgba(24, 160, 88, 0.2);
  }
}
</style>
