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
import { computed } from 'vue'
import { NSpin } from 'naive-ui'

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
  margin-top: 12px;
  font-size: 14px;
  color: var(--n-text-color-2);
  font-weight: 500;
}

@media (max-width: 768px) {
  .loading-container {
    min-height: 150px;
    padding: 20px;
  }
  
  :deep(.n-spin-description) {
    font-size: 13px;
    margin-top: 10px;
  }
}

.loading-container {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>