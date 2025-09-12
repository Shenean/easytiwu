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
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .bottom-actions {
    padding: 12px 16px;
  }
  
  .action-buttons {
    gap: 12px;
  }
}
</style>