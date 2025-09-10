<template>
  <n-layout-header bordered class="navbar">
    <div class="navbar-container">
      <!-- Logo / 标题 -->
      <div class="logo">EasyTiwu</div>

      <!-- 菜单 -->
      <n-menu
          :value="(route.name ?? 'upload') as string"
          mode="horizontal"
          :options="menuOptions"
          :router="true"
          class="menu"
      />
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'

const route = useRoute()

const menuOptions: MenuOption[] = [
  {
    label: '上传',
    key: 'upload',
    to: { name: 'upload' }
  },
  {
    label: '题库',
    key: 'bank',
    to: { name: 'bank' }
  }
]
</script>

<style scoped>
.navbar {
  background-color: rgba(255, 255, 255, 0.4);  /* Transparent background for frosted glass effect */
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 64px;
  border-bottom: 1px solid #ddd; /* Subtle separator line */
  border-radius: 15px 15px 0 0;  /* Rounded top corners */
  backdrop-filter: blur(10px); /* Blur effect for frosted glass */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
}

.navbar-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-weight: bold;
  font-size: 20px;
  color: #333;
  text-align: center;
  padding: 0 15px;
}

.menu {
  background-color: transparent;
}

</style>
