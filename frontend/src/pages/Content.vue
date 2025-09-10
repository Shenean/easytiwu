<template>
  <n-layout class="content-layout">
    <!-- 左侧：题目区域 -->
    <n-layout-content class="question-area">
      <!-- 题干 -->
      <n-card :title="`第 ${currentQuestion.id} 题`" size="small" class="question-card">
        <div class="question-stem" v-html="currentQuestion.content"></div>
      </n-card>

      <!-- 作答区 -->
      <n-card title="作答区" size="small" class="answer-area">
        <!-- 单选 -->
        <div v-if="currentQuestion.type === 'single'">
          <n-radio-group v-model:value="localAnswer" name="single-choice" size="large">
            <n-space vertical>
              <n-radio
                  v-for="opt in currentQuestion.options"
                  :key="opt.label"
                  :value="opt.label"
                  :disabled="isSubmitted"
              >
                <strong>{{ opt.label }}.</strong> {{ opt.text }}
              </n-radio>
            </n-space>
          </n-radio-group>
        </div>

        <!-- 多选 -->
        <div v-else-if="currentQuestion.type === 'multiple'">
          <n-checkbox-group v-model:value="localAnswer" size="large">
            <n-space vertical>
              <n-checkbox
                  v-for="opt in currentQuestion.options"
                  :key="opt.label"
                  :value="opt.label"
                  :disabled="isSubmitted"
              >
                <strong>{{ opt.label }}.</strong> {{ opt.text }}
              </n-checkbox>
            </n-space>
          </n-checkbox-group>
        </div>

        <!-- 判断题 -->
        <div v-else-if="currentQuestion.type === 'true_false'">
          <n-radio-group v-model:value="localAnswer" name="true-false" size="large">
            <n-space>
              <n-radio :value="'1'" :disabled="isSubmitted">正确</n-radio>
              <n-radio :value="'0'" :disabled="isSubmitted">错误</n-radio>
            </n-space>
          </n-radio-group>
        </div>

        <!-- 填空 / 简答 -->
        <div v-else-if="currentQuestion.type === 'fill_blank' || currentQuestion.type === 'short_answer'">
          <n-input
              v-model:value="localAnswer"
              type="textarea"
              placeholder="请输入你的答案"
              :autosize="{ minRows: 2, maxRows: 6 }"
              :disabled="isSubmitted"
              clearable
          />
        </div>
      </n-card>

      <!-- 答案与解析（提交后或已作答显示） -->
      <n-card v-if="showAnswer" title="答案与解析" size="small" class="answer-card">
        <n-alert type="success" title="正确答案" :show-icon="false" class="mb-4">
          {{ displayCorrectAnswer }}
        </n-alert>
        <n-alert
            v-if="currentQuestion.analysis"
            type="info"
            title="解析"
            :show-icon="false"
        >
          {{ currentQuestion.analysis }}
        </n-alert>
      </n-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <n-button
            :disabled="currentQuestionIndex === 0"
            @click="prevQuestion"
            size="large"
            type="default"
        >
          上一题
        </n-button>

        <n-button
            v-if="!isSubmitted"
            type="primary"
            size="large"
            @click="submitAnswer"
            :disabled="!canSubmit"
        >
          提交答案
        </n-button>

        <n-button
            v-else
            type="success"
            size="large"
            @click="nextQuestion"
            :disabled="currentQuestionIndex >= questionList.length - 1"
        >
          下一题
        </n-button>
      </div>
    </n-layout-content>

    <!-- 右侧：答题卡 -->
    <n-layout-sider
        bordered
        :width="300"
        content-style="padding: 16px;"
        class="answer-card-sider"
    >
      <n-card title="答题卡" size="small">
        <n-grid :cols="5" :x-gap="8" :y-gap="8">
          <n-grid-item v-for="q in questionList" :key="q.id">
            <n-button
                :type="getCardButtonType(q)"
                :size="getCardButtonSize"
                block
                @click="jumpToQuestion(q.id)"
                :bordered="false"
            >
              {{ q.id }}
            </n-button>
          </n-grid-item>
        </n-grid>
      </n-card>
    </n-layout-sider>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'

interface Question {
  id: number
  content: string
  type: 'single' | 'multiple' | 'true_false' | 'fill_blank' | 'short_answer'
  options: { label: string; text: string }[]
  userAnswer: string | null
  correctAnswer: string
  analysis: string
  isCompleted: number
  isCorrect: number | null 
}

// 模拟从后端获取的数据（实际应替换为 API 请求）
const questionList = ref<Question[]>([
  {
    "id": 201,
    "content": "HTTP协议中，哪个状态码表示“未授权”（Unauthorized）？",
    "type": "single",
    "options": [
      {"label": "A", "text": "404 Not Found"},
      {"label": "B", "text": "403 Forbidden"},
      {"label": "C", "text": "401 Unauthorized"},
      {"label": "D", "text": "500 Internal Server Error"}
    ],
    "userAnswer": "C",
    "correctAnswer": "C",
    "analysis": "401 Unauthorized 表示请求需要身份验证。虽然 403 Forbidden 也是访问被拒绝，但它表示服务器理解请求但拒绝授权，与身份验证无关。",
    "isCompleted": 1,
    "isCorrect": 1
  },
  {
    "id": 202,
    "content": "在Git中，以下哪些命令会直接修改提交历史？",
    "type": "multiple",
    "options": [
      {"label": "A", "text": "git rebase"},
      {"label": "B", "text": "git reset --hard"},
      {"label": "C", "text": "git merge"},
      {"label": "D", "text": "git revert"}
    ],
    "userAnswer": "[\"A\",\"C\"]",
    "correctAnswer": "[\"A\",\"B\"]",
    "analysis": "git rebase 和 git reset --hard 都会重写提交历史，是危险操作。git merge 和 git revert 会创建新的提交记录来合并或撤销更改，而不会修改已有的历史。",
    "isCompleted": 1,
    "isCorrect": 0
  },
  {
    "id": 203,
    "content": "SQL中的 TRUNCATE TABLE 命令会触发DELETE触发器。",
    "type": "true_false",
    "options": [],
    "userAnswer": "1",
    "correctAnswer": "0",
    "analysis": "TRUNCATE 是一个DDL（数据定义语言）操作，它会快速删除所有行且通常不能回滚，也不会触发DML（数据操作语言）的DELETE触发器。",
    "isCompleted": 1,
    "isCorrect": 0
  },
  {
    "id": 204,
    "content": "在MVC设计模式中，负责处理业务逻辑和数据的是 ___ 层。",
    "type": "fill_blank",
    "options": [],
    "userAnswer": null,
    "correctAnswer": "",
    "analysis": "",
    "isCompleted": 0,
    "isCorrect": null
  },
  {
    "id": 205,
    "content": "请简述什么是“依赖注入”（Dependency Injection）。",
    "type": "short_answer",
    "options": [],
    "userAnswer": null,
    "correctAnswer": "",
    "analysis": "",
    "isCompleted": 0,
    "isCorrect": null
  }
])

const message = useMessage()
const currentQuestionIndex = ref(0)
const isSubmitted = ref(false)

// 本地作答状态（响应式，用于绑定输入控件）
const localAnswer = ref<string | string[]>('')

// 计算当前题目
const currentQuestion = computed(() => questionList.value[currentQuestionIndex.value])

// 是否显示答案（提交后或已作答）
const showAnswer = computed(() => {
  return isSubmitted.value || currentQuestion.value.isCompleted === 1
})

// 格式化显示正确答案（多选转为 A,B,C 格式）
const displayCorrectAnswer = computed(() => {
  const ans = currentQuestion.value.correctAnswer
  if (currentQuestion.value.type === 'multiple' && ans) {
    try {
      const arr = JSON.parse(ans)
      return Array.isArray(arr) ? arr.join(', ') : ans
    } catch {
      return ans
    }
  }
  if (currentQuestion.value.type === 'true_false') {
    return ans === '1' ? '正确' : '错误'
  }
  return ans || '无'
})

// 是否可提交（填空/简答需非空，选择题需有选项）
const canSubmit = computed(() => {
  if (['fill_blank', 'short_answer'].includes(currentQuestion.value.type)) {
    return !!localAnswer.value?.toString().trim()
  }
  return Array.isArray(localAnswer.value)
    ? localAnswer.value.length > 0
    : !!localAnswer.value
})

// 初始化：根据 userAnswer 设置 localAnswer
function initLocalAnswer() {
  const ua = currentQuestion.value.userAnswer
  if (!ua) {
    localAnswer.value = currentQuestion.value.type === 'multiple' ? [] : ''
    return
  }

  if (currentQuestion.value.type === 'multiple') {
    try {
      localAnswer.value = JSON.parse(ua)
    } catch {
      localAnswer.value = []
    }
  } else {
    localAnswer.value = ua
  }
}

// 提交答案
function submitAnswer() {
  if (!canSubmit.value) {
    message.warning('请先完成作答！')
    return
  }

  let answerToSave: string

  if (currentQuestion.value.type === 'multiple') {
    const sorted = (localAnswer.value as string[]).sort()
    answerToSave = JSON.stringify(sorted)
  } else {
    answerToSave = localAnswer.value.toString()
  }

  // 更新题目状态
  currentQuestion.value.userAnswer = answerToSave
  currentQuestion.value.isCompleted = 1

  // 判断是否正确
  const isCorrect = answerToSave === currentQuestion.value.correctAnswer
  currentQuestion.value.isCorrect = isCorrect ? 1 : 0

  isSubmitted.value = true

  message.success(isCorrect ? '回答正确！🎉' : '回答错误，继续加油！')
}

// 跳转题目
function jumpToQuestion(id: number) {
  const index = questionList.value.findIndex(q => q.id === id)
  if (index !== -1) {
    currentQuestionIndex.value = index
    isSubmitted.value = false
    initLocalAnswer()
  }
}

// 上一题
function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    isSubmitted.value = false
    initLocalAnswer()
  }
}

// 下一题
function nextQuestion() {
  if (currentQuestionIndex.value < questionList.value.length - 1) {
    currentQuestionIndex.value++
    isSubmitted.value = false
    initLocalAnswer()
  } else {
    message.info('已是最后一题')
  }
}

// 答题卡按钮类型（控制颜色）
function getCardButtonType(q: Question) {
  if (q.isCompleted === 0) return 'default' // 未作答 → 白色
  if (q.isCorrect === 1) return 'success'   // 答对 → 绿色
  return 'error'                            // 答错 → 红色
}

// 答题卡按钮大小（响应式）
const getCardButtonSize = computed(() => {
  return window.innerWidth < 768 ? 'small' : 'medium'
})

// 初始化第一题
onMounted(() => {
  initLocalAnswer()
})

// 当题目切换时，重置本地答案状态
watch(currentQuestion, () => {
  initLocalAnswer()
  isSubmitted.value = false
})
</script>

<style scoped>
.content-layout {
  display: flex;
  height: calc(100vh - 120px);
  margin: 20px;
  gap: 20px;
}

.question-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.answer-card-sider {
  flex-shrink: 0;
  height: fit-content;
  max-height: 100%;
  overflow-y: auto;
}

.question-card,
.answer-area,
.answer-card {
  flex-shrink: 0;
}

.question-stem {
  font-size: 16px;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  gap: 12px;
}

/* 响应式：小屏堆叠 */
@media (max-width: 768px) {
  .content-layout {
    flex-direction: column;
    height: auto;
  }

  .answer-card-sider {
    width: 100% !important;
    max-height: 300px;
  }
}

.mb-4 {
  margin-bottom: 16px;
}

:deep(.n-radio-content),
:deep(.n-checkbox-content) {
  font-size: 15px;
}

/* ============= 答题卡按钮样式强化 ============= */
:deep(.answer-card-sider .n-button--default) {
  background-color: #ffffff !important;
  border-color: #dcdcdc !important;
  color: #333 !important;
}

:deep(.answer-card-sider .n-button--success) {
  background-color: #18a058 !important;
  border-color: #18a058 !important;
  color: #fff !important;
}

:deep(.answer-card-sider .n-button--error) {
  background-color: #dd2c2c !important;
  border-color: #dd2c2c !important;
  color: #fff !important;
}

/* 按钮悬停微调 */
:deep(.answer-card-sider .n-button) {
  transition: all 0.2s ease;
}

:deep(.answer-card-sider .n-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}
</style>