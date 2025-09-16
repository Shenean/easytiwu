<template>
  <n-layout class="app-layout" :style="layoutStyles">
    <!-- 导航栏 -->
    <n-layout-header class="app-header" bordered>
      <slot name="header">
        <NavigationBar />
      </slot>
    </n-layout-header>

    <!-- 主要内容区域 -->
    <n-layout-content class="app-content" :style="contentStyles">
      <div class="content-container" :class="containerClass">
        <slot />
      </div>
    </n-layout-content>

    <!-- 页脚（可选） -->
    <n-layout-footer v-if="$slots.footer" class="app-footer" bordered>
      <slot name="footer" />
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import NavigationBar from '../NavigationBar.vue'

interface Props {
  /** 容器最大宽度类型 */
  containerSize?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full'
  /** 内容区域内边距 */
  contentPadding?: 'none' | 'sm' | 'md' | 'lg'
  /** 是否启用响应式容器 */
  responsive?: boolean
  /** 自定义容器类名 */
  containerClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  containerSize: 'xl',
  contentPadding: 'md',
  responsive: true,
  containerClass: ''
})

// 布局样式
const layoutStyles = computed(() => ({
  minHeight: '100vh',
}))

// 内容区域样式
const contentStyles = computed(() => {
  const paddingMap = {
    none: '0',
    sm: 'var(--spacing-2)',
    md: 'var(--spacing-3)',
    lg: 'var(--spacing-4)'
  }
  
  return {
    padding: paddingMap[props.contentPadding],
    paddingTop: `calc(48px + ${paddingMap[props.contentPadding]})`, // 导航栏高度 + 内边距
    marginTop: '0'
  }
})

// 容器类名
const containerClass = computed(() => {
  const classes = []
  
  // 容器尺寸类
  if (props.containerSize !== 'full') {
    classes.push(`container-${props.containerSize}`)
  }
  
  // 响应式类
  if (props.responsive) {
    classes.push('container-responsive')
  }
  
  // 自定义类名
  if (props.containerClass) {
    classes.push(props.containerClass)
  }
  
  return classes.join(' ')
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background-color: var(--app-bg-color, var(--n-body-color));
}

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 48px;
  background-color: var(--n-card-color);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.app-content {
  flex: 1;
  transition: padding 0.3s ease;
}

.content-container {
  width: 100%;
  margin: 0 auto;
  transition: max-width 0.3s ease, padding 0.3s ease;
}

/* 容器尺寸样式 */
.content-container.container-sm {
  max-width: var(--container-max-width-sm);
}

.content-container.container-md {
  max-width: var(--container-max-width-md);
}

.content-container.container-lg {
  max-width: var(--container-max-width-lg);
}

.content-container.container-xl {
  max-width: var(--container-max-width-xl);
}

.content-container.container-2xl {
  max-width: var(--container-max-width-2xl);
}

/* 响应式容器 */
.content-container.container-responsive {
  padding: 0 var(--spacing-3);
}

@media (max-width: 767px) {
  .content-container.container-responsive {
    padding: 0 var(--spacing-2);
  }
}

@media (min-width: 1200px) {
  .content-container.container-responsive {
    padding: 0 var(--spacing-4);
  }
}

.app-footer {
  background-color: var(--n-card-color);
  border-top: 1px solid var(--n-border-color);
}
</style>