<template>
  <n-button
    :type="type"
    :size="size"
    :loading="loading"
    :disabled="disabled"
    :secondary="secondary"
    :tertiary="tertiary"
    :strong="strong"
    :ghost="ghost"
    :round="round"
    :circle="circle"
    :class="customClass"
    @click="handleClick"
  >
    <template v-if="icon" #icon>
      <component :is="icon" />
    </template>
    <slot />
  </n-button>
</template>

<script setup lang="ts">
import { NButton } from "naive-ui"
import type { ButtonProps } from "naive-ui"
import { computed, type Component } from "vue"

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
  radius: "8px"
})

const customClass = computed(() => ({
  "base-button": true
}))

const emit = defineEmits<{
  (e: "click", event: MouseEvent): void
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
  transition: all 0.2s ease;
}

.base-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.base-button:active {
  transform: translateY(0);
  box-shadow: none;
}
</style>
