<template>
  <n-input v-model:value="innerValue" :type="type" :placeholder="placeholder" :disabled="disabled"
    :clearable="clearable" :maxlength="maxlength" :show-count="showCount" :round="round" :readonly="readonly"
    :class="customClass" @input="handleInput" @blur="handleBlur" @focus="handleFocus">
    <!-- 前缀图标 -->
    <template v-if="prefixIcon" #prefix>
      <component :is="prefixIcon" />
    </template>

    <!-- 后缀图标 -->
    <template v-if="suffixIcon" #suffix>
      <component :is="suffixIcon" />
    </template>
  </n-input>
</template>

<script setup lang="ts">
import {type Component, computed, ref, watch} from "vue"
import {NInput} from "naive-ui"

interface Props {
  modelValue?: string
  type?: "text" | "password" | "textarea"
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
  maxlength?: number
  showCount?: boolean
  round?: boolean
  readonly?: boolean
  prefixIcon?: Component | string | null
  suffixIcon?: Component | string | null
  radius?: string | number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: "",
  type: "text",
  placeholder: "请输入内容",
  disabled: false,
  clearable: true,
  maxlength: undefined,
  showCount: false,
  round: false,
  readonly: false,
  prefixIcon: null,
  suffixIcon: null,
  radius: "var(--input-border-radius)"
})

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void
  (e: "blur", event: FocusEvent): void
  (e: "focus", event: FocusEvent): void
}>()

const innerValue = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    innerValue.value = val
  }
)

function handleInput(value: string) {
  emit("update:modelValue", value)
}
function handleBlur(event: FocusEvent) {
  emit("blur", event)
}
function handleFocus(event: FocusEvent) {
  emit("focus", event)
}

const customClass = computed(() => ({
  "base-input": true
}))
</script>

<style scoped>
.base-input {
  border-radius: v-bind("props.radius");
  transition: var(--transition-fast);
}

.base-input:focus-within {
  box-shadow: 0 0 0 var(--input-focus-ring-width) var(--input-focus-ring-color);
}

/* 减少动画偏好支持 */
@media (prefers-reduced-motion: reduce) {
  .base-input {
    transition: none;
  }
}
</style>
