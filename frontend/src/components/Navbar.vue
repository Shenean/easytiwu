<template>
  <n-layout-header bordered class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/" class="logo" aria-label="返回首页">
        EasyTiwu
      </router-link>

      <!-- 桌面端菜单 -->
      <n-menu
        :value="currentRouteName"
        mode="horizontal"
        :options="menuOptions"
        @update:value="handleMenuSelect"
        class="menu desktop-nav"
        :disabled-keys="disabledKeys"
      />
      
      <!-- 移动端汉堡菜单 -->
      <div class="mobile-nav">
        <n-button text @click="showMobileMenu = !showMobileMenu" class="mobile-menu-btn">
          <template #icon>
            <n-icon size="20">
              <MenuOutline />
            </n-icon>
          </template>
        </n-button>
      </div>
    </div>
    
    <!-- 移动端下拉菜单 -->
    <div v-if="showMobileMenu" class="mobile-menu">
      <div 
        v-for="option in menuOptions" 
        :key="option.key"
        class="mobile-menu-item"
        @click="navigateAndClose(option.key as string)"
      >
        <span>{{ option.label }}</span>
      </div>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { MenuOutline } from '@vicons/ionicons5'
import { mainMenuOptions } from '../config/menuOptions'

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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 999;
}

.mobile-menu-item {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 500;
}

.mobile-menu-item:hover {
  background-color: rgba(24, 160, 88, 0.1);
  color: #18a058;
}

.mobile-menu-item:last-child {
  border-bottom: none;
}

.mobile-menu-btn {
  padding: 8px;
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
  }
  
  .logo {
    font-size: 20px;
    padding: 0 8px;
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


</style>
