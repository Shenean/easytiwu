<template>
  <div class="answer-area">
    <!-- 单选题 -->
    <div v-if="question.type === 'single'">
      <t-radio-group v-model="radioValue" name="single-choice" size="large">
        <t-space direction="vertical">
          <t-radio
            v-for="opt in question.options"
            :key="opt.label"
            :value="opt.label"
            :disabled="false"
          >
            <strong>{{ opt.label }}.</strong> {{ opt.text }}
          </t-radio>
        </t-space>
      </t-radio-group>
    </div>

    <!-- 多选题 -->
    <div v-else-if="question.type === 'multiple'">
      <t-checkbox-group v-model="checkboxValue" size="large">
        <t-space direction="vertical">
          <t-checkbox
            v-for="opt in question.options"
            :key="opt.label"
            :value="opt.label"
            :disabled="false"
          >
            <strong>{{ opt.label }}.</strong> {{ opt.text }}
          </t-checkbox>
        </t-space>
      </t-checkbox-group>
    </div>

    <!-- 判断题 -->
    <div v-else-if="question.type === 'true_false'">
      <t-radio-group v-model="radioValue" name="true-false" size="large">
        <t-space>
          <t-radio :value="'1'" :disabled="false">{{
            t("questionAnswer.correct")
          }}</t-radio>
          <t-radio :value="'0'" :disabled="false">{{
            t("questionAnswer.incorrect")
          }}</t-radio>
        </t-space>
      </t-radio-group>
    </div>

    <!-- 填空 / 简答 -->
    <div
      v-else-if="
        question.type === 'fill_blank' || question.type === 'short_answer'
      "
    >
      <t-textarea
        v-model="inputValue"
        :placeholder="t('questionAnswer.inputPlaceholder')"
        :autosize="{ minRows: 2, maxRows: 6 }"
        :disabled="false"
        clearable
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from "vue";
import {useI18n} from "vue-i18n";
/** 类型声明 */
import type {AnswerValue, Question} from "@/types/common";

const { t } = useI18n();

/** props / emits */
const props = defineProps<{
  question: Question;
  modelValue: AnswerValue;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: AnswerValue): void;
}>();

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
    const v = props.modelValue;
    if (Array.isArray(v)) return null;
    return (v as string | number | boolean | null) ?? null;
  },
  set(v) {
    emit("update:modelValue", v as AnswerValue);
  },
});

const checkboxValue = computed<(string | number)[] | null>({
  get() {
    // 多选期待数组；若 modelValue 不是数组，返回空数组以便 checkbox-group 正常工作
    return Array.isArray(props.modelValue)
      ? (props.modelValue as (string | number)[])
      : [];
  },
  set(v) {
    emit("update:modelValue", v as AnswerValue);
  },
});

const inputValue = computed<string>({
  get() {
    const v = props.modelValue;
    if (Array.isArray(v)) return "";
    return v == null ? "" : String(v);
  },
  set(v) {
    emit("update:modelValue", v as AnswerValue);
  },
});
</script>

<style scoped>
.answer-area {
  margin-bottom: 0;
  width: 100%;
  /* 确保答题区占满容器宽度 */
  box-sizing: border-box;
}

/* 表单元素样式 */
:deep(.t-radio__content),
:deep(.t-checkbox__content) {
  font-size: var(--font-size-sm);
  line-height: 1.5;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

:deep(.t-radio),
:deep(.t-checkbox) {
  margin-bottom: var(--spacing-2);
  width: 100%;
  box-sizing: border-box;
}

/* 确保输入框也遵循固定宽度布局 */
:deep(.t-textarea) {
  width: 100%;
  box-sizing: border-box;
}

/* 选项文本换行处理 */
:deep(.t-radio__content),
:deep(.t-checkbox__content) {
  max-width: 100%;
  white-space: normal;
}
</style>
