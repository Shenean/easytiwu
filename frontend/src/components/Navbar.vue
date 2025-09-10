<template>
  <n-layout-header bordered class="navbar">
    <div class="navbar-container">
      <!-- Logo / 标题 -->
      <router-link to="/" class="logo" aria-label="返回首页">
        EasyTiwu
      </router-link>

      <!-- 菜单 -->
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
          @select="handleMenuSelect"
          placement="bottom-end"
      >
        <n-button text>
          <n-icon size="24">
            <i class="i-ion-menu-outline"></i>
          </n-icon>
        </n-button>
      </n-dropdown>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useWindowSize } from '@vueuse/core'

const route = useRoute()
const router = useRouter()

// 当前路由名（带默认 fallback）
const currentRouteName = computed(() => {
  return (route.name?.toString() || 'upload') as string
})

// 禁用当前路由对应的菜单项（避免重复跳转）
const disabledKeys = computed(() => [currentRouteName.value])

// 菜单配置
const menuOptions = ref<MenuOption[]>([
  {
    label: '上传题库',
    key: 'upload',
    icon: () => h('i', { class: 'i-ion-cloud-upload-outline' })
  },
  {
    label: '题库列表',
    key: 'bank',
    icon: () => h('i', { class: 'i-ion-library-outline' })
  }
])

// 菜单点击处理
const handleMenuSelect = (key: string) => {
  if (key === currentRouteName.value) return // 避免重复跳转
  router.push({ name: key }).catch(err => {
    console.warn('[Router] Navigation cancelled or duplicated:', err)
  })
}

// 响应式：判断是否为移动端
const { width } = useWindowSize()
const MOBILE_BREAKPOINT = 768
const isMobile = computed(() => width.value < MOBILE_BREAKPOINT)

// 可选：监听页面返回/前进，同步菜单选中状态（如手动改 URL）
let unwatchRoute: (() => void) | null = null

onMounted(() => {
  // 监听路由变化，确保菜单同步（虽然 computed 已响应，但加强健壮性）
  unwatchRoute = router.afterEach(() => {
    // 无需操作，currentRouteName 已自动更新
  })
})

onUnmounted(() => {
  if (unwatchRoute) unwatchRoute()
})

// 暴露方法供外部控制
defineExpose({
  refreshMenu: () => {
    // 强制刷新菜单状态（极端情况备用）
  }
})
</script>

<style scoped>
.navbar {
  background-color: rgba(255, 255, 255, 0.7);
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 64px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 15px 15px 0 0;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px); /* Safari 兼容 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
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
  transition: all 0.2s ease;
}

.logo:hover {
  color: #18a058;
  transform: scale(1.03);
}

.menu {
  background-color: transparent;
  height: 100%;
}

:deep(.n-menu-item-content) {
  font-weight: 500;
  transition: all 0.2s ease;
}

:deep(.n-menu-item-content:hover),
:deep(.n-menu-item.n-menu-item--active .n-menu-item-content) {
  color: #18a058 !important;
}

/* 毛玻璃增强：加一层微妙渐变 */
.navbar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0.05) 100%);
  pointer-events: none;
  border-radius: 15px 15px 0 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 12px;
    height: 56px;
  }

  .logo {
    font-size: 20px;
    padding: 0 8px;
  }
}
</style>