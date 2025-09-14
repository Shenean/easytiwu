<template>
  <div class="form-actions">
    <!-- 重置按钮 -->
    <BaseButton v-if="props.showReset" @click="emit('reset')">
      重置
    </BaseButton>

    <!-- 提交按钮 -->
    <BaseButton type="primary" :loading="props.loading" :disabled="props.disabled" @click="emit('submit')">
      提交
    </BaseButton>
  </div>
</template>

<script setup lang="ts">
import BaseButton from '../common/BaseButton.vue'

interface Props {
  loading?: boolean
  disabled?: boolean
  showReset?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  disabled: false,
  showReset: true
})

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'reset'): void
}>()
</script>

<style scoped>
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-3);
  width: 100%;
  margin-top: var(--form-item-margin);
}

/* 移动端优化 */
@media (max-width: 768px) {
  .form-actions {
    gap: var(--spacing-2);
    margin-top: var(--spacing-3);
  }
}
</style>
