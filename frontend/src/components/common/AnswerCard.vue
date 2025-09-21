<template>
  <n-card :title="t('answerCard.title')" size="small" class="answer-card-container">
    <n-grid :cols="5" :x-gap="12" :y-gap="12">
      <n-grid-item v-for="(q, index) in questions" :key="q.id">
        <n-button :type="getCardButtonType(q)" size="medium" @click="jumpToQuestion(q.id)" :focusable="true"
          class="answer-card-btn" :style="getButtonStyle(q)">
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
}

const {
  questions,
  currentQuestionId,
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
    height: 'var(--button-height-md)',
    minWidth: 'var(--button-height-md)',
    fontSize: 'var(--font-size-base)',
    padding: '0',
    borderRadius: 'var(--border-radius-sm)',
  };

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
</style>
