<template>
  <div class="page-container">
    <n-card
      v-if="showCard"
      :title="title"
      :size="size"
      :bordered="bordered"
      :segmented="segmented"
      :style="cardStyle"
    >
      <n-grid
        v-if="useGrid"
        :cols="cols"
        :x-gap="xGap"
        :y-gap="yGap"
        :responsive="responsive"
      >
        <slot />
      </n-grid>
      <template v-else>
        <slot />
      </template>
    </n-card>

    <template v-else>
      <n-grid
        v-if="useGrid"
        :cols="cols"
        :x-gap="xGap"
        :y-gap="yGap"
        :responsive="responsive"
        :style="contentStyle"
      >
        <slot />
      </n-grid>
      <div v-else :style="contentStyle">
        <slot />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'

interface Props {
  title?: string
  showCard?: boolean
  maxWidth?: string
  size?: 'small' | 'medium' | 'large'
  bordered?: boolean
  segmented?: boolean | { content?: boolean; footer?: boolean }
  useGrid?: boolean
  cols?: number | string
  xGap?: number
  yGap?: number
  responsive?: 'self' | 'screen'
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCard: true,
  maxWidth: 'var(--container-medium-width)',
  size: 'large',
  bordered: true,
  segmented: true,
  useGrid: false,
  cols: 'auto',
  xGap: 16,
  yGap: 16,
  responsive: 'self'
})

const cardStyle = computed(() => ({
  maxWidth: props.maxWidth,
  margin: '0 auto'
}))

const contentStyle = computed(() => ({
  maxWidth: props.maxWidth,
  margin: '0 auto'
}))
</script>

<style scoped>
.page-container {
  width: 100%;
  padding: var(--spacing-md); /* 16px */
}

@media (max-width: 639px) {
  .page-container {
    padding: var(--spacing-sm); /* 8px */
  }
}
</style>