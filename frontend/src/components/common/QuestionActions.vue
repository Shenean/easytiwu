<template>
  <div class="bottom-actions">
    <div class="action-buttons">
      <BaseButton :disabled="!hasPrev || loading" @click="$emit('prev')" size="large" type="default">
        上一题
      </BaseButton>

      <BaseButton :disabled="!hasNext || loading" @click="$emit('next')" size="large" type="default">
        下一题
      </BaseButton>

      <BaseButton type="primary" size="large" @click="$emit('submit')" :disabled="!canSubmit || loading"
        :loading="loading">
        {{ loading ? '提交中...' : '提交答案' }}
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import BaseButton from './BaseButton.vue'

// 定义props
interface Props {
  hasPrev: boolean
  hasNext: boolean
  canSubmit: boolean
  loading: boolean
}

defineProps<Props>()

defineEmits<{
  prev: []
  next: []
  submit: []
}>()

</script>

<style scoped>
/* 底部固定操作区 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  border-top: var(--border-default);
  padding: var(--spacing-4) var(--spacing-5);
  box-shadow: var(--shadow-lg);
  z-index: var(--z-index-sticky);
  backdrop-filter: blur(10px);
  background-color: var(--color-surface);
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4);
  max-width: var(--max-width-7xl);
  margin: 0 auto;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .bottom-actions {
    padding: var(--spacing-3) var(--spacing-4);
    box-shadow: var(--shadow-xl);
  }

  .action-buttons {
    gap: var(--spacing-3);
    flex-wrap: wrap;
  }

  /* 移动端按钮优化 */
  :deep(.n-button) {
    flex: 1;
    max-width: 120px;
  }

  /* 主要按钮（提交）更突出 */
  :deep(.n-button--primary) {
    min-width: 100px;
  }
}

@media (max-width: 480px) {
  .bottom-actions {
    padding: var(--spacing-2) var(--spacing-3);
  }

  .action-buttons {
    gap: var(--spacing-2);
  }

  :deep(.n-button) {
    min-width: 70px;
    max-width: 100px;
  }

  :deep(.n-button--primary) {
    min-width: 90px;
  }
}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .bottom-actions {
    padding: var(--spacing-2) var(--spacing-4);
  }
}

/* 安全区域适配（iPhone X等） */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .bottom-actions {
    padding-bottom: calc(var(--spacing-4) + env(safe-area-inset-bottom));
  }

  @media (max-width: 768px) {
    .bottom-actions {
      padding-bottom: calc(var(--spacing-3) + env(safe-area-inset-bottom));
    }
  }

  @media (max-width: 480px) {
    .bottom-actions {
      padding-bottom: calc(var(--spacing-2) + env(safe-area-inset-bottom));
    }
  }
}
</style>