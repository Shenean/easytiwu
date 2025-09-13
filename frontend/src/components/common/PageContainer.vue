<template>
  <div class="page-container" :class="containerClass">
    <n-card v-if="showCard" :title="title" :class="cardClass" :segmented="segmented" :size="cardSize"
      :bordered="bordered">
      <template v-if="$slots.headerExtra" #header-extra>
        <slot name="headerExtra" />
      </template>

      <slot />
    </n-card>

    <!-- 无卡片模式，直接渲染内容 -->
    <div v-else :class="contentClass">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

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
  /** 容器背景色 */
  background?: string
  /** 自定义容器类名 */
  containerClass?: string
  /** 自定义卡片类名 */
  cardClass?: string
  /** 是否居中显示 */
  centered?: boolean
  /** 响应式断点 */
  responsive?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCard: true,
  maxWidth: '1080px',
  padding: '20px',
  cardSize: 'large',
  bordered: true,
  segmented: true,
  background: '#f5f5f5',
  containerClass: '',
  cardClass: '',
  centered: true,
  responsive: true
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
  min-height: calc(100vh - 140px);
  padding: v-bind(padding);
  background-color: v-bind(background);
}

.page-container--centered {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.page-card {
  width: 100%;
  max-width: v-bind(maxWidth);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  transition: box-shadow 0.3s ease;
  margin: 0 auto;
}

.page-card:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
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
    padding: 48px 24px;
  }

  /* 平板端 */
  @media (max-width: 1199px) and (min-width: 769px) {
    padding: 32px 20px;
  }

  /* 移动端 */
  @media (max-width: 768px) {
    padding: 16px 12px;
    min-height: calc(100vh - 120px);
  }

  @media (max-width: 480px) {
    padding: 12px 8px;
    min-height: calc(100vh - 100px);
  }
}

.page-container--responsive .page-card {

  /* 移动端卡片优化 */
  @media (max-width: 768px) {
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    margin: 0 -4px;
  }

  @media (max-width: 480px) {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    margin: 0 -4px;
  }
}

/* 卡片内容区域优化 */
:deep(.n-card__header) {
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
  font-size: 18px;
}

:deep(.n-card__content) {
  padding: 24px;
}

/* 移动端卡片内容优化 */
@media (max-width: 768px) {
  :deep(.n-card__header) {
    padding: 16px 20px 12px;
    font-size: 16px;
  }

  :deep(.n-card__content) {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  :deep(.n-card__header) {
    padding: 12px 16px 10px;
    font-size: 15px;
  }

  :deep(.n-card__content) {
    padding: 16px;
  }
}

/* 特殊页面样式适配 */
.page-container.statistics-container {
  background-color: transparent;
}

.page-container.settings-container {
  background-color: transparent;
}

.page-container.content-page {
  background-color: #f5f5f5;
  padding: 20px;
}

/* 动画效果 */
.page-card {
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 深色主题适配 */
@media (prefers-color-scheme: dark) {
  .page-container {
    background-color: #1a1a1a;
  }

  .page-card {
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.3);
  }

  .page-card:hover {
    box-shadow: 0 10px 28px rgba(0, 0, 0, 0.4);
  }
}
</style>