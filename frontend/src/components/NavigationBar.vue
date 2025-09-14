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
  height: var(--spacing-14);
  padding: 0 var(--spacing-5);
  display: flex;
  align-items: center;
  background-color: var(--color-white-98);
  backdrop-filter: blur(var(--spacing-3));
  border-bottom: 1px solid var(--color-black-05);
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  box-shadow: 0 var(--spacing-1) var(--spacing-3) var(--color-black-05);
  transition: background-color 0.3s, box-shadow 0.3s;
  animation: navbar-fade-in 0.6s ease-out both;
}

@keyframes navbar-fade-in {
  from {
    opacity: 0;
    transform: translateY(calc(-1 * var(--spacing-2)));
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.navbar-container {
  width: 100%;
  max-width: var(--spacing-300);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  text-decoration: none;
  padding: 0 var(--spacing-4);
  transition: transform 0.2s;
  display: flex;
  align-items: center;
}

.logo:hover {
  transform: scale(1.05);
}

.logo-img {
  height: var(--spacing-10);
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
  gap: var(--spacing-xs);
}

:deep(.n-menu-item-content:hover),
:deep(.n-menu-item.n-menu-item--active .n-menu-item-content) {
  color: var(--color-primary) !important;
  transform: scale(1.03);
}

/* 移动端按钮 */
.mobile-menu-btn {
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  background-color: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(var(--spacing-1));
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.06);
  min-width: var(--spacing-10);
  min-height: var(--spacing-10);
  position: relative;
  overflow: hidden;
}

.mobile-menu-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  opacity: 0;
  transition: opacity 0.25s ease;
}

.mobile-menu-btn:hover::before {
  opacity: 1;
}

.mobile-menu-btn:hover,
.mobile-menu-btn--active {
  background-color: rgba(var(--color-primary-rgb), 0.12);
  border-color: rgba(var(--color-primary-rgb), 0.25);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(var(--color-primary-rgb), 0.15);
}

.mobile-menu-btn--active {
  background-color: rgba(var(--color-primary-rgb), 0.15);
}

/* 移动端下拉菜单 */
.mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(var(--spacing-4)) saturate(1.2);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 var(--spacing-2) var(--spacing-6) rgba(0, 0, 0, 0.12),
              0 var(--spacing-1) var(--spacing-4) rgba(0, 0, 0, 0.08);
  z-index: 999;
  border-radius: 0 0 var(--border-radius-lg) var(--border-radius-lg);
  overflow: hidden;
  border-left: 1px solid rgba(0, 0, 0, 0.05);
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}

.mobile-menu-item {
  padding: var(--spacing-4) var(--spacing-lg);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  font-weight: 600;
  color: rgba(0, 0, 0, 0.85);
  font-size: var(--font-size-sm);
  min-height: var(--spacing-12);
  position: relative;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(2px);
}

.mobile-menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(to bottom, var(--color-primary), var(--color-primary-light));
  transform: scaleY(0);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: bottom;
}

.mobile-menu-item:hover {
  background-color: rgba(var(--color-primary-rgb), 0.08);
  color: var(--color-primary);
  transform: translateX(var(--spacing-1));
  box-shadow: inset 0 0 0 1px rgba(var(--color-primary-rgb), 0.1);
}

.mobile-menu-item:hover::before {
  transform: scaleY(1);
  transform-origin: top;
}

.mobile-menu-item:active {
  background-color: rgba(var(--color-primary-rgb), 0.15);
  transform: translateX(var(--spacing-1)) scale(0.98);
}

.mobile-menu-item--active {
  background-color: rgba(var(--color-primary-rgb), 0.12);
  color: var(--color-primary);
  font-weight: 700;
}

.mobile-menu-item--active::before {
  transform: scaleY(1);
}

.mobile-menu-item:last-child {
  border-bottom: none;
}

.mobile-menu-item__icon {
  color: inherit;
  flex-shrink: 0;
}

/* 动画 */
.mobile-menu-enter-active {
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.mobile-menu-leave-active {
  transition: all 0.3s cubic-bezier(0.55, 0.06, 0.68, 0.19);
}

.mobile-menu-enter-from {
  opacity: 0;
  transform: translateY(calc(-1 * var(--spacing-4))) scale(0.95);
  backdrop-filter: blur(0px);
}

.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(calc(-1 * var(--spacing-2))) scale(0.98);
  backdrop-filter: blur(0px);
}

.mobile-menu-enter-to,
.mobile-menu-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
  backdrop-filter: blur(var(--spacing-4)) saturate(1.2);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .navbar {
    padding: 0 var(--spacing-4);
    height: var(--spacing-13);
  }

  .logo {
    padding: 0 var(--spacing-2);
  }

  .logo-img {
    height: var(--spacing-8);
  }

  .mobile-menu {
    margin: 0 var(--spacing-2);
    border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
  }
}

/* 中等屏幕平板设备 */
@media (max-width: 640px) {
  .navbar {
    padding: 0 var(--spacing-3);
  }

  .mobile-menu-item {
    padding: var(--spacing-4) var(--spacing-6);
    min-height: var(--spacing-12);
    font-size: var(--font-size-base);
  }

  .mobile-menu {
    margin: 0 var(--spacing-1);
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .navbar {
    padding: 0 var(--spacing-3);
    height: var(--spacing-12);
  }

  .logo-img {
    height: var(--spacing-7);
  }

  .mobile-menu-item {
    padding: var(--spacing-3) var(--spacing-5);
    font-size: var(--font-size-sm);
    min-height: var(--spacing-11);
  }

  .mobile-menu-btn {
    padding: var(--spacing-2);
    min-width: var(--spacing-9);
    min-height: var(--spacing-9);
  }

  .mobile-menu {
    margin: 0;
    border-radius: 0 0 var(--border-radius-lg) var(--border-radius-lg);
  }
}

/* 超小屏幕设备 */
@media (max-width: 360px) {
  .navbar {
    padding: 0 var(--spacing-2);
    height: var(--spacing-11);
  }

  .logo {
    padding: 0 var(--spacing-1);
  }

  .logo-img {
    height: var(--spacing-6);
  }

  .mobile-menu-item {
    padding: var(--spacing-2) var(--spacing-4);
    font-size: var(--font-size-xs);
    min-height: var(--spacing-10);
    gap: var(--spacing-2);
  }

  .mobile-menu-btn {
    padding: var(--spacing-1);
    min-width: var(--spacing-8);
    min-height: var(--spacing-8);
  }
}

/* 暗色主题 */
@media (prefers-color-scheme: dark) {
  .navbar {
    background-color: rgba(18, 18, 18, 0.95);
    border-color: rgba(255, 255, 255, 0.08);
  }

  .mobile-menu {
    background-color: rgba(18, 18, 18, 0.92);
    border-color: rgba(255, 255, 255, 0.08);
    box-shadow: 0 var(--spacing-2) var(--spacing-6) rgba(0, 0, 0, 0.3),
                0 var(--spacing-1) var(--spacing-4) rgba(0, 0, 0, 0.2);
    border-left-color: rgba(255, 255, 255, 0.05);
    border-right-color: rgba(255, 255, 255, 0.05);
  }

  .mobile-menu-item {
    color: rgba(255, 255, 255, 0.87);
    border-bottom-color: rgba(255, 255, 255, 0.06);
  }

  .mobile-menu-item:hover {
    background-color: rgba(var(--color-primary-rgb), 0.15);
    color: var(--color-primary-light);
    box-shadow: inset 0 0 0 1px rgba(var(--color-primary-rgb), 0.2);
  }

  .mobile-menu-item--active {
    background-color: rgba(var(--color-primary-rgb), 0.18);
    color: var(--color-primary-light);
  }

  .mobile-menu-item:active {
    background-color: rgba(var(--color-primary-rgb), 0.25);
  }

  .mobile-menu-btn {
    background-color: rgba(255, 255, 255, 0.06);
    border-color: rgba(255, 255, 255, 0.08);
  }

  .mobile-menu-btn::before {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.04));
  }

  .mobile-menu-btn:hover,
  .mobile-menu-btn--active {
    background-color: rgba(var(--color-primary-rgb), 0.18);
    border-color: rgba(var(--color-primary-rgb), 0.35);
    box-shadow: 0 2px 8px rgba(var(--color-primary-rgb), 0.25);
  }

  .mobile-menu-btn--active {
    background-color: rgba(var(--color-primary-rgb), 0.22);
  }
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .mobile-menu-item {
    min-height: var(--spacing-13);
    padding: var(--spacing-4) var(--spacing-5);
    font-size: var(--font-size-base);
  }

  .mobile-menu-btn {
    min-width: var(--spacing-11);
    min-height: var(--spacing-11);
    padding: var(--spacing-3);
  }

  .mobile-menu-item:hover {
    transform: none;
  }
}
</style>