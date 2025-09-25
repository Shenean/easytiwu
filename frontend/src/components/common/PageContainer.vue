<template>
  <div class="page-container" :class="containerClass">
    <t-space direction="vertical" :size="spacing" class="page-content">
      <!-- 页面标题 -->
      <div v-if="title" class="page-header">
        <h1 class="page-title">{{ title }}</h1>
        <t-divider v-if="showTitleDivider" />
      </div>

      <!-- 简化的内容包装器 -->
      <component :is="wrapperComponent" v-bind="wrapperProps" :class="wrapperClass" :style="wrapperStyle">
        <slot />
      </component>
    </t-space>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'

interface Props {
  title?: string
  showCard?: boolean
  size?: 'small' | 'medium' | 'large'
  bordered?: boolean
  segmented?: boolean | { content?: boolean; footer?: boolean }
  useGrid?: boolean
  cols?: number | string
  xGap?: number
  yGap?: number
  responsive?: 'self' | 'screen'
  spacing?: 'small' | 'medium' | 'large' | number
  showTitleDivider?: boolean
  containerClass?: string
  cardClass?: string
  contentClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCard: true,
  size: 'medium',
  bordered: true,
  segmented: true,
  useGrid: false,
  cols: 'auto',
  xGap: 16,
  yGap: 16,
  responsive: 'self',
  spacing: 'medium',
  showTitleDivider: false,
  containerClass: '',
  cardClass: '',
  contentClass: ''
})

// 计算包装器组件类型
const wrapperComponent = computed(() => {
  if (props.showCard) {
    return 't-card'
  }
  if (props.useGrid) {
    return 't-row'
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
      headerBordered: typeof props.segmented === 'object' ? props.segmented.content : props.segmented
    }
  }

  if (props.useGrid) {
    return {
      gutter: [props.xGap, props.yGap]
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
const wrapperStyle = computed(() => ({}))
</script>

<style scoped>
.page-container {
  width: 100%;
}

.page-content {
  width: 100%;
}

.page-header {
  padding: 0;
}

.page-title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 639px) {
  .page-title {
    font-size: var(--font-size-lg);
  }
}
</style>