<template>
  <n-card
    :title="t('answerCard.title')"
    size="small"
    class="answer-card-container"
    :class="{ 'mobile-mode': mobileMode }"
  >
    <n-grid
      :cols="mobileMode ? '5' : '8'"
      :x-gap="mobileMode ? 8 : 12"
      :y-gap="mobileMode ? 8 : 12"
      responsive="screen"
    >
      <n-grid-item v-for="(q, index) in questions" :key="q.id">
        <n-button
          :type="getCardButtonType(q)"
          :size="mobileMode ? 'small' : 'medium'"
          @click="jumpToQuestion(q.id)"
          :focusable="true"
          class="answer-card-btn"
          :style="getButtonStyle(q)"
        >
          {{ index + 1 }}
        </n-button>
      </n-grid-item>
    </n-grid>
  </n-card>
</template>

<script setup lang="ts">
import {NGrid, NGridItem, useThemeVars} from "naive-ui";
import {useI18n} from "vue-i18n";
import type {Question} from '@/types/common';

const { t } = useI18n();
const themeVars = useThemeVars();

interface Props {
  questions: Question[];
  currentQuestionId?: number;
  mobileMode?: boolean;
}

const {
  questions,
  currentQuestionId,
  mobileMode = false,
} = defineProps<Props>();

const emit = defineEmits<{
  questionClick: [id: number];
}>();

function getCardButtonType(q: Question) {
  if (q.isCompleted === 0) return "default";
  if (q.isCorrect === 1) return "success";
  return "error";
}

function getButtonStyle(q: Question) {
  const baseStyle = {
    width: '100%',
    height: mobileMode ? 'var(--button-height-lg)' : 'var(--button-height-md)',
        minWidth: mobileMode ? 'var(--button-height-lg)' : 'var(--button-height-md)',
        fontSize: mobileMode ? 'var(--font-size-sm)' : 'var(--font-size-base)',
        padding: '0',
        borderRadius: mobileMode ? 'var(--border-radius-lg)' : 'var(--border-radius-sm)',
  };

  // 当前题目选中状态：仅改变边框样式，保持背景色不变
  if (q.id === currentQuestionId) {
    return {
      ...baseStyle,
      border: `var(--border-width-2) solid ${themeVars.value.primaryColor}`,
      borderStyle: 'solid',
    };
  }

  return baseStyle;
}

function jumpToQuestion(id: number) {
  emit("questionClick", id);
}
</script>

<style scoped>
.answer-card-container {
  position: sticky;
  top: var(--spacing-3);
  max-height: calc(100vh - var(--spacing-25));
  overflow-y: auto;
}

/* 自定义滚动条样式 */
.answer-card-container::-webkit-scrollbar {
  width: var(--spacing-1);
}

.answer-card-container::-webkit-scrollbar-thumb {
  background-color: var(--color-black-15);
  border-radius: var(--border-radius-sm);
}

.answer-card-container::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-black-25);
}

/* 移动端模式样式调整 */
.mobile-mode {
  position: static;
  max-height: none;
}

.mobile-mode :deep(.n-card__header) {
  padding: var(--spacing-1-5) var(--spacing-2);
  font-size: var(--font-size-base);
  font-weight: 500;
  border-bottom: var(--border-width-1) solid var(--color-border);
}

.mobile-mode :deep(.n-card__content) {
  padding: var(--spacing-2);
}

/* 响应式隐藏 */
@media (max-width: 768px) {
  .answer-card-container:not(.mobile-mode) {
    display: none;
  }
}

@media (max-width: 480px) {
  .mobile-mode :deep(.n-card__header) {
    padding: var(--spacing-1-5);
    font-size: var(--font-size-sm);
  }

  .mobile-mode :deep(.n-card__content) {
    padding: var(--spacing-1-5);
  }
}
</style>
