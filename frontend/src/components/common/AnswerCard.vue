<template>
  <n-card
    :title="t('answerCard.title')"
    size="small"
    class="answer-card-container"
    :class="{ 'mobile-mode': mobileMode }"
  >
    <n-grid
      :cols="mobileMode ? 5 : 8"
      :x-gap="mobileMode ? 8 : 12"
      :y-gap="mobileMode ? 8 : 12"
    >
      <n-grid-item v-for="(q, index) in questions" :key="q.id">
        <n-button
          :type="getCardButtonType(q)"
          :size="mobileMode ? 'small' : getCardButtonSize"
          @click="jumpToQuestion(q.id)"
          class="answer-card-btn"
          :class="{
            active: q.id === currentQuestionId,
            'mobile-btn': mobileMode,
          }"
        >
          {{ index + 1 }}
        </n-button>
      </n-grid-item>
    </n-grid>
  </n-card>
</template>

<script setup lang="ts">
import {NGrid, NGridItem} from "naive-ui";
import {useI18n} from "vue-i18n";

const { t } = useI18n();

interface Question {
  id: number;
  isCompleted: number;
  isCorrect: number | null;
}

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

const getCardButtonSize = "medium";

function jumpToQuestion(id: number) {
  emit("questionClick", id);
}
</script>

<style scoped>
.answer-card-container {
  position: sticky;
  top: var(--spacing-5);
  max-height: calc(100vh - var(--spacing-50));
  overflow-y: auto;
  border-radius: var(--border-radius-md);
}

/* 自定义滚动条 */
.answer-card-container::-webkit-scrollbar {
  width: var(--spacing-2);
}

.answer-card-container::-webkit-scrollbar-thumb {
  background-color: var(--color-black-15);
  border-radius: var(--border-radius-sm);
}

/* 答题卡按钮 */
.answer-card-btn {
  height: var(--spacing-10);
  width: 100%;
}

/* 当前题目高亮（蓝色边框） */
.answer-card-btn.active {
  border: var(--spacing-1) solid var(--color-info) !important;
  box-shadow: 0 0 0 var(--spacing-1) var(--color-info-25);
}

/* 移动端模式样式 */
.mobile-mode {
  position: static;
  max-height: none;
  border-radius: 0;
  box-shadow: none;
}

.mobile-mode :deep(.n-card__header) {
  padding: var(--spacing-3) var(--spacing-4);
  font-size: var(--font-size-base);
  font-weight: 600;
  border-bottom: 1px solid var(--color-border-light);
}

.mobile-mode :deep(.n-card__content) {
  padding: var(--spacing-4);
}

.mobile-btn {
  height: var(--spacing-11) !important;
  min-width: var(--spacing-11);
  font-size: var(--font-size-base);
  font-weight: 600;
  border-radius: var(--border-radius-md);
  touch-action: manipulation;
}

.mobile-btn:active {
  transform: scale(0.95) !important;
  transition: transform 0.1s ease;
}

.mobile-btn.active {
  box-shadow: 0 0 0 var(--spacing-1) var(--color-primary),
    var(--shadow-focus-ring);
  transform: scale(1.02);
}

/* 移动端响应式优化 */
@media (max-width: 768px) {
  .answer-card-container:not(.mobile-mode) {
    display: none;
  }
}

@media (max-width: 480px) {
  .mobile-mode :deep(.n-card__header) {
    padding: var(--spacing-3) var(--spacing-3);
    font-size: var(--font-size-sm);
  }

  .mobile-mode :deep(.n-card__content) {
    padding: var(--spacing-3);
  }

  .mobile-btn {
    height: var(--spacing-11) !important;
    min-width: var(--spacing-11);
    font-size: var(--font-size-sm);
  }
}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .mobile-mode :deep(.n-card__header) {
    padding: var(--spacing-2) var(--spacing-3);
    font-size: var(--font-size-base);
  }

  .mobile-mode :deep(.n-card__content) {
    padding: var(--spacing-3);
  }

  .mobile-btn {
    height: var(--spacing-10) !important;
    min-width: var(--spacing-10);
    font-size: var(--font-size-xs);
  }
}
</style>
