<template>
  <t-card
    :title="t('answerCard.title')"
    size="small"
    class="answer-card-container"
  >
    <t-row :gutter="{ xs: 8, sm: 8, md: 16, lg: 16, xl: 16, xxl: 16 }">
      <t-col
        :xs="12"
        :sm="8"
        :md="6"
        :lg="4"
        :xl="4"
        :xxl="3"
        v-for="(q, index) in questions"
        :key="q.id"
      >
        <t-button
          :theme="getCardButtonTheme(q)"
          size="medium"
          @click="jumpToQuestion(q.id)"
          class="answer-card-btn"
          :style="getButtonStyle(q)"
        >
          {{ index + 1 }}
        </t-button>
      </t-col>
    </t-row>
  </t-card>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {Question} from "@/types/common";

const { t } = useI18n();

interface Props {
  questions: Question[];
  currentQuestionId?: number;
}

const { questions, currentQuestionId } = defineProps<Props>();

const emit = defineEmits<{
  questionClick: [id: number];
}>();

function getCardButtonTheme(q: Question) {
  if (q.isCompleted === 0) return "default";
  if (q.isCorrect === 1) return "success";
  return "danger";
}

function getButtonStyle(q: Question) {
  const baseStyle = {
    height: "var(--button-height-md)",
    fontSize: "var(--font-size-base)",
    padding: "0",
    borderRadius: "var(--border-radius-sm)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  };

  if (q.id === currentQuestionId) {
    return {
      ...baseStyle,
      border: `var(--border-width-2) solid var(--td-brand-color)`,
      borderStyle: "solid",
      boxShadow: `0 0 0 2px var(--td-brand-color-light)`,
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
  padding: var(--spacing-1);
}

.answer-card-container :deep(.t-card__body) {
  padding: var(--spacing-1);
}

.answer-card-btn {
  transition: all 0.2s ease-in-out;
  aspect-ratio: 1;
}

.answer-card-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
