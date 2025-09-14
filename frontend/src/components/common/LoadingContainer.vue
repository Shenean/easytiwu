<template>
  <div v-if="loading" class="loading-container" :style="containerStyle">
    <n-spin :size="size" show>
      <template #description>
        {{ description }}
      </template>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {NSpin} from 'naive-ui'

interface Props {
  loading: boolean
  description?: string
  size?: 'small' | 'medium' | 'large'
  height?: string | number
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  description: '正在加载...',
  size: 'large',
  height: '200px'
})

const containerStyle = computed(() => {
  const height = typeof props.height === 'number' ? `${props.height}px` : props.height
  return {
    minHeight: height
  }
})
</script>

<style scoped>
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  position: relative;

  transition: all 0.3s ease;
}

:deep(.n-spin) {
  color: var(--n-text-color-2);
}

:deep(.n-spin-description) {
  margin-top: var(--spacing-3);
  font-size: var(--font-size-base);
  color: var(--n-text-color-2);
  font-weight: 500;
}

@media (max-width: 768px) {
  .loading-container {
    min-height: var(--spacing-38);
    padding: var(--spacing-5);
  }

  :deep(.n-spin-description) {
    font-size: var(--font-size-sm);
    margin-top: var(--spacing-3);
  }
}

.loading-container {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(var(--spacing-3));
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>