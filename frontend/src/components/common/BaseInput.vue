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
import { ref, watch, computed, type Component } from "vue"
import { NInput } from "naive-ui"

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
  radius: "8px"
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
}
</style>
