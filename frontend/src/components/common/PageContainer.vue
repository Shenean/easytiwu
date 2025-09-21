<template>
  <div class="page-container" :class="containerClass">
    <!-- 页面标题 -->
    <div v-if="title" class="page-header">
      <h1 class="page-title">{{ title }}</h1>
    </div>

    <!-- 简化的内容包装器 -->
    <component :is="wrapperComponent" v-bind="wrapperProps" :class="wrapperClass" :style="wrapperStyle">
      <slot />
    </component>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'

interface Props {
  title?: string
  showCard?: boolean
  maxWidth?: string
  size?: 'small' | 'medium' | 'large'
  bordered?: boolean
  segmented?: boolean | { content?: boolean; footer?: boolean }
  useGrid?: boolean
  cols?: number | string
  xGap?: number
  yGap?: number
  responsive?: 'self' | 'screen'
  containerClass?: string
  cardClass?: string
  contentClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCard: true,
  maxWidth: 'var(--container-medium-width)',
  size: 'large',
  bordered: true,
  segmented: true,
  useGrid: false,
  cols: 'auto',
  xGap: 16,
  yGap: 16,
  responsive: 'self',
  containerClass: '',
  cardClass: '',
  contentClass: ''
})

// 计算包装器组件类型
const wrapperComponent = computed(() => {
  if (props.showCard) {
    return 'n-card'
  }
  if (props.useGrid) {
    return 'n-grid'
  }
  return 'div'
})

// 计算包装器属性
const wrapperProps = computed(() => {
  if (props.showCard) {
    return {
      title: props.title,
      size: props.size,
      bordered: props.bordered,
      segmented: props.segmented
    }
  }

  if (props.useGrid) {
    return {
      cols: props.cols,
      xGap: props.xGap,
      yGap: props.yGap,
      responsive: props.responsive
    }
  }

  return {}
})

// 计算包装器类名
const wrapperClass = computed(() => {
  if (props.showCard) {
    return props.cardClass
  }
  return props.contentClass
})

// 计算包装器样式
const wrapperStyle = computed(() => ({
  maxWidth: props.maxWidth,
  margin: '0 auto'
}))
</script>

<style scoped>
.page-container {
  width: 100%;
  padding: var(--spacing-md);
  /* 16px */
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--n-text-color);
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 639px) {
  .page-container {
    padding: var(--spacing-sm);
    /* 8px */
  }

  .page-title {
    font-size: var(--font-size-lg);
  }
}
</style>