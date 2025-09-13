<template>
  <div class="bottom-actions">
    <div class="action-buttons">
      <BaseButton
          :disabled="!hasPrev || loading"
          @click="$emit('prev')"
          size="large"
          type="default"
      >
        上一题
      </BaseButton>

      <BaseButton
          :disabled="!hasNext || loading"
          @click="$emit('next')"
          size="large"
          type="default"
      >
        下一题
      </BaseButton>

      <BaseButton
          type="primary"
          size="large"
          @click="$emit('submit')"
          :disabled="!canSubmit || loading"
          :loading="loading"
      >
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
  background: white;
  border-top: 1px solid #e0e0e0;
  padding: 16px 20px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .bottom-actions {
    padding: 12px 16px;
    border-top: 1px solid #e0e0e0;
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
  }
  
  .action-buttons {
    gap: 12px;
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
    padding: 10px 12px;
  }
  
  .action-buttons {
    gap: 8px;
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
    padding: 8px 16px;
  }
}

/* 安全区域适配（iPhone X等） */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .bottom-actions {
    padding-bottom: calc(16px + env(safe-area-inset-bottom));
  }
  
  @media (max-width: 768px) {
    .bottom-actions {
      padding-bottom: calc(12px + env(safe-area-inset-bottom));
    }
  }
  
  @media (max-width: 480px) {
    .bottom-actions {
      padding-bottom: calc(10px + env(safe-area-inset-bottom));
    }
  }
}
</style>