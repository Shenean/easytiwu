<template>
  <div class="empty-state" :class="containerClass">
    <div class="empty-state__content">
      <!-- 图标区域 -->
      <div class="empty-state__icon">
        <component v-if="customIcon" :is="customIcon" class="custom-icon" />
        <n-icon v-else :size="iconSize" :color="iconColor">
          <component :is="defaultIcon" />
        </n-icon>
      </div>

      <!-- 描述文本 -->
      <div class="empty-state__description">
        <h3 v-if="title" class="empty-state__title">{{ title }}</h3>
        <p class="empty-state__text">{{ description }}</p>
      </div>

      <!-- 操作按钮区域 -->
      <div v-if="$slots.actions || showDefaultAction" class="empty-state__actions">
        <slot name="actions">
          <BaseButton v-if="showDefaultAction && actionText" :type="actionType" :size="actionSize"
            @click="handleAction">
            {{ actionText }}
          </BaseButton>
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {type Component, computed} from 'vue'
import {NIcon} from 'naive-ui'
import {
  BarChartOutline as ChartIcon,
  DocumentTextOutline as DocumentIcon,
  FolderOpenOutline as FolderIcon,
  PinOutline as DefaultEmptyIcon
} from '@vicons/ionicons5'
import BaseButton from './BaseButton.vue'

interface Props {
  /** 描述文本 */
  description: string
  /** 标题文本 */
  title?: string
  /** 自定义图标组件 */
  customIcon?: Component
  /** 预设图标类型 */
  iconType?: 'default' | 'folder' | 'document' | 'chart'
  /** 图标大小 */
  iconSize?: number
  /** 图标颜色 */
  iconColor?: string
  /** 是否显示默认操作按钮 */
  showDefaultAction?: boolean
  /** 操作按钮文本 */
  actionText?: string
  /** 操作按钮类型 */
  actionType?: 'default' | 'primary' | 'info' | 'success' | 'warning' | 'error'
  /** 操作按钮大小 */
  actionSize?: 'tiny' | 'small' | 'medium' | 'large'
  /** 容器大小 */
  size?: 'small' | 'medium' | 'large'
  /** 是否居中显示 */
  centered?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  description: '暂无数据',
  title: '',
  iconType: 'default',
  iconSize: 64,
  iconColor: 'var(--n-text-color-3)',
  showDefaultAction: false,
  actionText: '',
  actionType: 'primary',
  actionSize: 'medium',
  size: 'medium',
  centered: true
})

const emit = defineEmits<{
  action: []
}>()

// 默认图标映射
const iconMap = {
  default: DefaultEmptyIcon,
  folder: FolderIcon,
  document: DocumentIcon,
  chart: ChartIcon
}

// 计算默认图标
const defaultIcon = computed(() => iconMap[props.iconType])

// 计算容器样式类
const containerClass = computed(() => ({
  [`empty-state--${props.size}`]: true,
  'empty-state--centered': props.centered
}))

// 处理操作按钮点击
const handleAction = () => {
  emit('action')
}
</script>

<style scoped>
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: var(--spacing-50);
  padding: var(--spacing-8) var(--spacing-4);
  text-align: center;
}

.empty-state--centered {
  flex-direction: column;
}

.empty-state--small {
  min-height: var(--spacing-30);
  padding: var(--spacing-5) var(--spacing-3);
}

.empty-state--medium {
  min-height: var(--spacing-50);
  padding: var(--spacing-8) var(--spacing-4);
}

.empty-state--large {
  min-height: var(--spacing-75);
  padding: var(--spacing-12) var(--spacing-6);
}

.empty-state__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-4);
  max-width: var(--spacing-100);
}

.empty-state__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-2);
}

.custom-icon {
  width: var(--spacing-16);
  height: var(--spacing-16);
  color: var(--n-text-color-3);
}

.empty-state__description {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.empty-state__title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--n-text-color-1);
  line-height: 1.4;
}

.empty-state__text {
  margin: 0;
  font-size: var(--font-size-base);
  color: var(--n-text-color-2);
  line-height: 1.5;
  word-break: break-word;
}

.empty-state__actions {
  display: flex;
  gap: var(--spacing-3);
  flex-wrap: wrap;
  justify-content: center;
  margin-top: var(--spacing-2);
}

/* 小尺寸样式调整 */
.empty-state--small .empty-state__content {
  gap: var(--spacing-3);
}

.empty-state--small .custom-icon {
  width: var(--spacing-12);
  height: var(--spacing-12);
}

.empty-state--small .empty-state__title {
  font-size: var(--font-size-base);
}

.empty-state--small .empty-state__text {
  font-size: var(--font-size-sm);
}

/* 大尺寸样式调整 */
.empty-state--large .empty-state__content {
  gap: var(--spacing-5);
}

.empty-state--large .custom-icon {
  width: var(--spacing-20);
  height: var(--spacing-20);
}

.empty-state--large .empty-state__title {
  font-size: var(--font-size-xl);
}

.empty-state--large .empty-state__text {
  font-size: var(--font-size-lg);
}

/* 移动端优化 */
@media (max-width: 768px) {
  .empty-state {
    min-height: var(--spacing-40);
    padding: var(--spacing-6) var(--spacing-3);
  }

  .empty-state__content {
    gap: var(--spacing-3);
    max-width: var(--spacing-75);
  }

  .custom-icon {
    width: var(--spacing-12);
    height: var(--spacing-12);
  }

  .empty-state__title {
    font-size: var(--font-size-base);
  }

  .empty-state__text {
    font-size: var(--font-size-sm);
  }

  .empty-state__actions {
    gap: var(--spacing-2);
  }
}

@media (max-width: 480px) {
  .empty-state {
    min-height: var(--spacing-35);
    padding: var(--spacing-5) var(--spacing-2);
  }

  .empty-state__content {
    gap: var(--spacing-3);
    max-width: var(--spacing-70);
  }

  .custom-icon {
    width: var(--spacing-10);
    height: var(--spacing-10);
  }

  .empty-state__title {
    font-size: var(--font-size-sm);
  }

  .empty-state__text {
    font-size: var(--font-size-xs);
  }
}

/* 动画效果 */
.empty-state {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(var(--spacing-5));
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 深色主题适配 */
[data-theme="dark"] .empty-state__title {
  color: var(--n-text-color-1);
}

[data-theme="dark"] .empty-state__text {
  color: var(--n-text-color-2);
}

[data-theme="dark"] .custom-icon {
  color: var(--n-text-color-3);
}
</style>