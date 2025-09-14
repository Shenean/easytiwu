<template>
  <n-button :type="type" :size="size" :loading="loading" :disabled="disabled" :secondary="secondary"
    :tertiary="tertiary" :strong="strong" :ghost="ghost" :round="round" :circle="circle" :class="customClass"
    @click="handleClick">
    <template v-if="icon" #icon>
      <component :is="icon" />
    </template>
    <slot />
  </n-button>
</template>

<script setup lang="ts">
import type {ButtonProps} from "naive-ui"
import {NButton} from "naive-ui"
import {type Component, computed} from "vue"

interface Props {
  type?: ButtonProps["type"]
  size?: ButtonProps["size"]
  loading?: boolean
  disabled?: boolean
  secondary?: boolean
  tertiary?: boolean
  strong?: boolean
  ghost?: boolean
  round?: boolean
  circle?: boolean
  icon?: Component | string | null
  radius?: string | number
}

const props = withDefaults(defineProps<Props>(), {
  type: "default",
  size: "medium",
  loading: false,
  disabled: false,
  secondary: false,
  tertiary: false,
  strong: false,
  ghost: false,
  round: false,
  circle: false,
  icon: null,
  radius: "var(--button-border-radius)"
})

const customClass = computed(() => ({
  "base-button": true
}))

const emit = defineEmits<{
  click: [MouseEvent]
}>()

function handleClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit("click", event)
  }
}
</script>

<style scoped>
.base-button {
  border-radius: v-bind("props.radius");
  transition: var(--transition-base);
  font-weight: var(--button-font-weight);
}

.base-button:hover {
  transform: translateY(calc(-1 * var(--spacing-1)));
  box-shadow: var(--shadow-md);
}

.base-button:active {
  transform: translateY(0);
  box-shadow: var(--shadow-none);
}

/* 减少动画偏好支持 */
@media (prefers-reduced-motion: reduce) {
  .base-button {
    transition: none;
  }
  
  .base-button:hover {
    transform: none;
  }
  
  .base-button:active {
    transform: none;
  }
}
</style>
