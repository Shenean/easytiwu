<template>
  <div class="answer-area">
    <!-- 单选题 -->
    <div v-if="question.type === 'single'">
      <n-radio-group v-model:value="radioValue" name="single-choice" size="large">
        <n-space vertical>
          <n-radio v-for="opt in question.options" :key="opt.label" :value="opt.label" :disabled="false">
            <strong>{{ opt.label }}.</strong> {{ opt.text }}
          </n-radio>
        </n-space>
      </n-radio-group>
    </div>

    <!-- 多选题 -->
    <div v-else-if="question.type === 'multiple'">
      <n-checkbox-group v-model:value="checkboxValue" size="large">
        <n-space vertical>
          <n-checkbox v-for="opt in question.options" :key="opt.label" :value="opt.label" :disabled="false">
            <strong>{{ opt.label }}.</strong> {{ opt.text }}
          </n-checkbox>
        </n-space>
      </n-checkbox-group>
    </div>

    <!-- 判断题 -->
    <div v-else-if="question.type === 'true_false'">
      <n-radio-group v-model:value="radioValue" name="true-false" size="large">
        <n-space>
          <n-radio :value="'1'" :disabled="false">{{ t('questionAnswer.correct') }}</n-radio>
          <n-radio :value="'0'" :disabled="false">{{ t('questionAnswer.incorrect') }}</n-radio>
        </n-space>
      </n-radio-group>
    </div>

    <!-- 填空 / 简答 -->
    <div v-else-if="question.type === 'fill_blank' || question.type === 'short_answer'">
      <n-input v-model:value="inputValue" type="textarea" :placeholder="t('questionAnswer.inputPlaceholder')" :autosize="{ minRows: 2, maxRows: 6 }"
        :disabled="false" clearable />
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {NCheckbox, NCheckboxGroup, NInput, NRadio, NRadioGroup, NSpace} from 'naive-ui'
import {useI18n} from 'vue-i18n'
/** 类型声明 */
import type {AnswerValue, Question} from '@/types/common'

const { t } = useI18n()

/** props / emits */
const props = defineProps<{
  question: Question
  modelValue: AnswerValue
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: AnswerValue): void
}>()

/**
 * 为不同控件提供严格类型的 v-model computed：
 * - radioValue: 单选 / 判断题 -> string | number | boolean | null
 * - checkboxValue: 多选 -> (string|number)[] | null
 * - inputValue: 文本 -> string
 *
 * 在 setter 中统一 emit 回广义的 AnswerValue。
 */

const radioValue = computed<string | number | boolean | null>({
  get() {
    // 单选 / 判断题 使用字符串或数字或 boolean；若 modelValue 是数组则返回 null（类型不匹配）
    const v = props.modelValue
    if (Array.isArray(v)) return null
    return (v as string | number | boolean | null) ?? null
  },
  set(v) {
    emit('update:modelValue', v as AnswerValue)
  }
})

const checkboxValue = computed<(string | number)[] | null>({
  get() {
    // 多选期待数组；若 modelValue 不是数组，返回空数组以便 checkbox-group 正常工作
    return Array.isArray(props.modelValue) ? (props.modelValue as (string | number)[]) : []
  },
  set(v) {
    emit('update:modelValue', v as AnswerValue)
  }
})

const inputValue = computed<string>({
  get() {
    const v = props.modelValue
    if (Array.isArray(v)) return ''
    return v == null ? '' : String(v)
  },
  set(v) {
    emit('update:modelValue', v as AnswerValue)
  }
})
</script>

<style scoped>
.answer-area {
  margin-bottom: 0;
  width: 100%;
  /* 确保答题区占满容器宽度 */
  box-sizing: border-box;
}

/* 表单元素样式 */
:deep(.n-radio-content),
:deep(.n-checkbox-content) {
  font-size: var(--font-size-sm);
  line-height: 1.5;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

:deep(.n-radio),
:deep(.n-checkbox) {
  margin-bottom: var(--spacing-2);
  width: 100%;
  box-sizing: border-box;
}

/* 确保输入框也遵循固定宽度布局 */
:deep(.n-input) {
  width: 100%;
  box-sizing: border-box;
}

/* 选项文本换行处理 */
:deep(.n-radio-content),
:deep(.n-checkbox-content) {
  max-width: 100%;
  white-space: normal;
}
</style>
