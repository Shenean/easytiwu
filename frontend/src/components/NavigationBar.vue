<template>
  <n-layout-header bordered class="navbar" tag="header">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/" class="logo" aria-label="返回首页">
        <img src="/EasyTiwu.png" alt="EasyTiwu" class="logo-img" draggable="false" />
      </router-link>

      <!-- 桌面端菜单 -->
      <n-menu v-if="!isMobile" :value="currentRouteName" mode="horizontal" :options="menuOptions"
        @update:value="handleNavigate" class="desktop-nav" :disabled-keys="[currentRouteName]" />

      <!-- 移动端汉堡按钮 -->
      <BaseButton v-else text @click="showMobileMenu = !showMobileMenu" class="mobile-menu-btn"
        :class="{ 'mobile-menu-btn--active': showMobileMenu }" aria-label="打开导航菜单">
        <template #icon>
          <n-icon :size="20">
            <component :is="showMobileMenu ? CloseOutline : MenuOutline" />
          </n-icon>
        </template>
      </BaseButton>
    </div>

    <!-- 移动端下拉菜单 -->
    <Transition name="mobile-menu">
      <nav v-if="showMobileMenu && isMobile" class="mobile-menu">
        <div v-for="option in menuOptions" :key="option.key" class="mobile-menu-item"
          :class="{ 'mobile-menu-item--active': option.key === currentRouteName }"
          @click="handleNavigateAndClose(option.key as string)"
          :aria-current="option.key === currentRouteName ? 'page' : undefined">
          <n-icon v-if="option.icon" :size="18" class="mobile-menu-item__icon">
            <component :is="option.icon()" />
          </n-icon>
          <span class="mobile-menu-item__label">{{ option.label }}</span>
        </div>
      </nav>
    </Transition>
  </n-layout-header>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {CloseOutline, MenuOutline} from '@vicons/ionicons5'
import {mainMenuOptions} from '../config/menuOptions'
import BaseButton from './common/BaseButton.vue'

const route = useRoute()
const router = useRouter()

// 响应式判断是否为移动端
const isMobile = computed(() => window.innerWidth <= 768)

// 移动端菜单状态
const showMobileMenu = ref(false)

// 当前路由名（安全兜底）
const currentRouteName = computed(() => (route.name?.toString() || 'upload'))

// 过滤菜单项（可扩展隐藏逻辑）
const menuOptions = computed(() => {
  return mainMenuOptions.filter(option => !option.meta?.hidden)
})

// 统一导航处理
const handleNavigate = (key: string) => {
  if (key === currentRouteName.value) return
  router.push({ name: key }).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      console.warn('[Router] Navigation error:', err)
    }
  })
}

// 移动端导航 + 自动关闭
const handleNavigateAndClose = (key: string) => {
  handleNavigate(key)
  showMobileMenu.value = false
}

// 监听窗口大小变化（轻量节流）
let resizeTimer: number | null = null
onMounted(() => {
  const onResize = () => {
    if (resizeTimer) clearTimeout(resizeTimer)
    resizeTimer = window.setTimeout(() => { }, 150) // 触发 Vue 响应式更新
  }
  window.addEventListener('resize', onResize)
  onUnmounted(() => {
    window.removeEventListener('resize', onResize)
    if (resizeTimer) clearTimeout(resizeTimer)
  })
})

// 路由变化时关闭移动端菜单
watch(
  () => route.fullPath,
  () => {
    if (showMobileMenu.value && isMobile.value) {
      showMobileMenu.value = false
    }
  }
)
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
  background-color: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
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
  user-select: none;
}

/* 桌面菜单 */
:deep(.n-menu-item-content) {
  font-weight: 500;
  transition: color 0.2s, transform 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

:deep(.n-menu-item-content:hover),
:deep(.n-menu-item.n-menu-item--active .n-menu-item-content) {
  color: #18a058 !important;
  transform: scale(1.03);
}

/* 移动端按钮 */
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

.mobile-menu-btn:hover,
.mobile-menu-btn--active {
  background-color: rgba(24, 160, 88, 0.1);
  border-color: rgba(24, 160, 88, 0.2);
  transform: scale(1.05);
}

/* 移动端下拉菜单 */
.mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 999;
  border-radius: 0 0 12px 12px;
  overflow: hidden;
}

.mobile-menu-item {
  padding: 14px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: rgba(51, 54, 57, 0.9);
  font-size: 15px;
  min-height: 48px;
}

.mobile-menu-item:hover {
  background-color: rgba(24, 160, 88, 0.12);
  color: #18a058;
  transform: translateX(4px);
}

.mobile-menu-item:active {
  background-color: rgba(24, 160, 88, 0.18);
  transform: translateX(2px) scale(0.98);
}

.mobile-menu-item--active {
  background-color: rgba(24, 160, 88, 0.08);
  color: #18a058;
  font-weight: 700;
}

.mobile-menu-item:last-child {
  border-bottom: none;
}

.mobile-menu-item__icon {
  color: inherit;
  flex-shrink: 0;
}

/* 动画 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .navbar {
    padding: 0 16px;
    height: 52px;
  }

  .logo {
    padding: 0 8px;
  }

  .logo-img {
    height: 32px;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 0 12px;
    height: 48px;
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

/* 暗色主题 */
@media (prefers-color-scheme: dark) {
  .navbar {
    background-color: rgba(16, 16, 20, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .mobile-menu {
    background-color: rgba(16, 16, 20, 0.98);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  }

  .mobile-menu-item {
    color: rgba(255, 255, 255, 0.9);
    border-bottom-color: rgba(255, 255, 255, 0.08);
  }

  .mobile-menu-item:hover,
  .mobile-menu-item--active {
    background-color: rgba(24, 160, 88, 0.15);
    color: #4ade80;
  }

  .mobile-menu-item:active {
    background-color: rgba(24, 160, 88, 0.2);
  }

  .mobile-menu-btn {
    background-color: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .mobile-menu-btn:hover {
    background-color: rgba(24, 160, 88, 0.15);
    border-color: rgba(24, 160, 88, 0.3);
  }
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .mobile-menu-item {
    min-height: 52px;
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
}
</style>