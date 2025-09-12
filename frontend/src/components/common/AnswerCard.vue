<template>
  <n-card title="答题卡" size="small" class="answer-card-container">
    <n-grid :cols="4" :x-gap="8" :y-gap="8">
      <n-grid-item v-for="(q, index) in questions" :key="q.id">
        <n-button
          :type="getCardButtonType(q)"
          :size="getCardButtonSize"
          block
          @click="jumpToQuestion(q.id)"
          :bordered="false"
          class="answer-card-btn"
          :class="{ active: q.id === currentQuestionId }"
        >
          {{ index + 1 }}
        </n-button>
      </n-grid-item>
    </n-grid>
  </n-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Question {
  id: number
  isCompleted: number
  isCorrect: number | null
}

interface Props {
  questions: Question[]
  currentQuestionId?: number
}

const { questions, currentQuestionId } = defineProps<Props>()

const emit = defineEmits<{
  questionClick: [id: number]
}>()

function getCardButtonType(q: Question) {
  if (q.isCompleted === 0) return 'default'
  if (q.isCorrect === 1) return 'success'
  return 'error'
}

const getCardButtonSize = computed(() => {
  return window.innerWidth < 768 ? 'small' : 'medium'
})

function jumpToQuestion(id: number) {
  emit('questionClick', id)
}
</script>

<style scoped>
.answer-card-container {
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  border-radius: 10px;
}

/* 自定义滚动条 */
.answer-card-container::-webkit-scrollbar {
  width: 6px;
}
.answer-card-container::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.15);
  border-radius: 4px;
}
.answer-card-container::-webkit-scrollbar-track {
  background: transparent;
}

/* 答题卡按钮 */
.answer-card-btn {
  height: 40px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 未作答 */
:deep(.n-button--default.answer-card-btn) {
  background-color: #fff !important;
  border: 2px solid #e0e0e0 !important;
  color: #666 !important;
}
/* 答对 */
:deep(.n-button--success.answer-card-btn) {
  background-color: #52c41a !important;
  border: 2px solid #52c41a !important;
  color: #fff !important;
}
/* 答错 */
:deep(.n-button--error.answer-card-btn) {
  background-color: #ff4d4f !important;
  border: 2px solid #ff4d4f !important;
  color: #fff !important;
}

/* 当前题目高亮（蓝色边框） */
.answer-card-btn.active {
  border: 2px solid #1890ff !important;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.25);
}

/* hover 动效 */
.answer-card-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}
</style>
