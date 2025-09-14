<template>
  <div class="page-container" :class="containerClass">
    <n-card v-if="showCard" :title="title" :class="cardClass" :segmented="segmented" :size="cardSize"
      :bordered="bordered">
      <template v-if="$slots.headerExtra" #header-extra>
        <slot name="headerExtra" />
      </template>

      <!-- 网格布局模式 -->
      <n-grid v-if="useGrid" :cols="gridCols" :x-gap="gridXGap" :y-gap="gridYGap" :responsive="gridResponsive">
        <slot />
      </n-grid>
      <!-- 普通布局模式 -->
      <div v-else>
        <slot />
      </div>
    </n-card>

    <!-- 无卡片模式 -->
    <div v-else :class="contentClass">
      <!-- 网格布局模式 -->
      <n-grid v-if="useGrid" :cols="gridCols" :x-gap="gridXGap" :y-gap="gridYGap" :responsive="gridResponsive">
        <slot />
      </n-grid>
      <!-- 普通布局模式 -->
      <div v-else>
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'

interface Props {
  /** 页面标题 */
  title?: string
  /** 是否显示卡片容器 */
  showCard?: boolean
  /** 容器最大宽度 */
  maxWidth?: string
  /** 容器内边距 */
  padding?: string
  /** 卡片大小 */
  cardSize?: 'small' | 'medium' | 'large'
  /** 是否显示边框 */
  bordered?: boolean
  /** 卡片分段样式 */
  segmented?: boolean | { content?: boolean; footer?: boolean }

  /** 自定义容器类名 */
  containerClass?: string
  /** 自定义卡片类名 */
  cardClass?: string
  /** 是否居中显示 */
  centered?: boolean
  /** 响应式断点 */
  responsive?: boolean

  /** 是否使用网格布局 */
  useGrid?: boolean
  /** 网格列数配置 */
  gridCols?: number | string
  /** 网格水平间距 */
  gridXGap?: number
  /** 网格垂直间距 */
  gridYGap?: number
  /** 网格响应式配置 */
  gridResponsive?: 'self' | 'screen'
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCard: true,
  maxWidth: 'var(--container-default-max-width)',
  padding: 'var(--container-default-padding)',
  cardSize: 'large',
  bordered: true,
  segmented: true,

  containerClass: '',
  cardClass: '',
  centered: true,
  responsive: true,

  useGrid: false,
  gridCols: 'auto',
  gridXGap: parseInt(getComputedStyle(document.documentElement).getPropertyValue('--spacing-4').trim()),
  gridYGap: parseInt(getComputedStyle(document.documentElement).getPropertyValue('--spacing-4').trim()),
  gridResponsive: 'self'
})

// 计算容器样式类
const containerClass = computed(() => [
  'page-container',
  {
    'page-container--centered': props.centered,
    'page-container--responsive': props.responsive
  },
  props.containerClass
])

// 计算卡片样式类
const cardClass = computed(() => [
  'page-card',
  props.cardClass
])

// 计算内容区域样式类
const contentClass = computed(() => [
  'page-content',
  {
    'page-content--centered': props.centered,
    'page-content--responsive': props.responsive
  }
])
</script>

<style scoped>
.page-container {
  width: 100%;
  min-height: var(--page-default-min-height);
  padding: v-bind(padding);
}

.page-container--centered {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.page-card {
  width: var(--card-standard-width);
  max-width: v-bind(maxWidth);
  box-shadow: var(--card-unified-shadow);
  border-radius: var(--card-border-radius);
  transition: box-shadow 0.3s ease;
  margin: 0 auto;
  border: none;
}

.page-card:hover {
  box-shadow: var(--card-unified-shadow-hover);
}

.page-content {
  width: 100%;
  max-width: v-bind(maxWidth);
  margin: 0 auto;
}

.page-content--centered {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 响应式设计 */
.page-container--responsive {

  /* 桌面端 */
  @media (min-width: 1200px) {
    padding: var(--container-responsive-padding-desktop);
  }

  /* 平板端 */
  @media (max-width: 1199px) and (min-width: 769px) {
    padding: var(--container-responsive-padding-tablet);
  }

  /* 移动端 */
  @media (max-width: 768px) {
    padding: var(--container-responsive-padding-mobile);
    min-height: var(--page-min-height-tablet);
  }

  @media (max-width: 480px) {
    padding: var(--container-responsive-padding-small);
    min-height: var(--page-min-height-mobile);
  }
}

.page-container--responsive .page-card {

  /* 移动端卡片优化 */
  @media (max-width: 768px) {
    max-width: var(--card-max-width-tablet);
    border-radius: var(--card-border-radius);
    box-shadow: var(--card-unified-shadow-tablet);
    margin: 0 calc(-1 * var(--spacing-1));
  }

  @media (max-width: 480px) {
    max-width: var(--card-max-width-mobile);
    border-radius: var(--card-border-radius);
    box-shadow: var(--card-unified-shadow-mobile);
    margin: 0 calc(-1 * var(--spacing-1));
  }
}

/* 卡片内容区域优化 */
:deep(.n-card__header) {
  padding: var(--card-padding-desktop) var(--card-padding-desktop) var(--spacing-4);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: 600;
  font-size: var(--font-size-lg);
}

:deep(.n-card__content) {
  padding: var(--card-padding-desktop);
  max-width: var(--card-content-max-width);
  margin: 0 auto;
}

/* 移动端卡片内容优化 */
@media (max-width: 768px) {
  :deep(.n-card__header) {
    padding: var(--card-padding-tablet) var(--card-padding-tablet) var(--spacing-3);
    font-size: var(--font-size-base);
  }

  :deep(.n-card__content) {
    padding: var(--card-padding-tablet);
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  :deep(.n-card__header) {
    padding: var(--card-padding-mobile) var(--card-padding-mobile) var(--spacing-3);
    font-size: var(--font-size-sm);
  }

  :deep(.n-card__content) {
    padding: var(--card-padding-mobile);
    max-width: 100%;
  }
}

/* 特殊页面样式适配 */
.page-container.content-page {
  padding: var(--spacing-5);
}

/* 动画效果 */
.page-card {
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(var(--spacing-5));
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>