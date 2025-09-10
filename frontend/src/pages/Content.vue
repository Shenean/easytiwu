<template>
  <n-layout class="content-layout">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <n-spin size="large">
        <template #description>正在加载题目...</template>
      </n-spin>
    </div>
    
    <!-- 无题目数据 -->
    <div v-else-if="questionList.length === 0" class="empty-container">
      <n-empty description="暂无题目数据">
        <template #extra>
          <n-button @click="$router.go(-1)">返回上一页</n-button>
        </template>
      </n-empty>
    </div>
    
    <!-- 题目内容 -->
    <template v-else>
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
    </template>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { useRoute } from 'vue-router'
import { AxiosError } from 'axios'
import api from '../api/config'

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

interface QuestionQueryRequest {
  bankId: number
  type: string
}

// 定义路由参数类型
interface RouteParams {
  bankId: string
  type: string
}

// 状态管理
const questionList = ref<Question[]>([])
const loading = ref(false)
const message = useMessage()
const route = useRoute()
const currentQuestionIndex = ref(0)
const isSubmitted = ref(false)

// 本地作答状态（响应式，用于绑定输入控件）
const localAnswer = ref<string | string[]>('')

// 计算当前题目
const currentQuestion = computed(() => questionList.value[currentQuestionIndex.value])

// 是否显示答案（提交后或已作答）
const showAnswer = computed(() => {
  return isSubmitted.value || (currentQuestion.value && currentQuestion.value.isCompleted === 1)
})

// 格式化显示正确答案（多选转为 A,B,C 格式）
const displayCorrectAnswer = computed(() => {
  if (!currentQuestion.value) return ''
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
  if (!currentQuestion.value) return false
  if (['fill_blank', 'short_answer'].includes(currentQuestion.value.type)) {
    return !!localAnswer.value?.toString().trim()
  }
  return Array.isArray(localAnswer.value)
    ? localAnswer.value.length > 0
    : !!localAnswer.value
})

// 从后端获取题目数据
async function fetchQuestions() {
  try {
    loading.value = true
    const params = route.params as unknown as RouteParams
    const bankId = parseInt(params.bankId)
    const type = params.type // 'all' 或 'wrong'
    
    if (!bankId || !type) {
      message.error('参数错误，请重新进入')
      return
    }

    const requestData: QuestionQueryRequest = {
      bankId,
      type
    }

    const response = await api.post('/content/questions', requestData)
    
    if (response.data && Array.isArray(response.data)) {
      questionList.value = response.data.map((item: any) => ({
        id: item.id,
        content: item.content,
        type: item.type,
        options: item.options || [],
        userAnswer: null,
        correctAnswer: item.correctAnswer || '',
        analysis: item.analysis || '',
        isCompleted: 0,
        isCorrect: null
      }))
      
      if (questionList.value.length === 0) {
        message.warning(type === 'wrong' ? '暂无错题数据' : '该题库暂无题目')
      } else {
        message.success(`成功加载 ${questionList.value.length} 道题目`)
        initLocalAnswer()
      }
    } else {
      message.error('数据格式错误')
    }
  } catch (error) {
    console.error('获取题目失败:', error)
    if (error instanceof AxiosError) {
      message.error(`获取题目失败: ${error.response?.data?.message || error.message}`)
    } else {
      message.error('获取题目失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 初始化：根据 userAnswer 设置 localAnswer
function initLocalAnswer() {
  if (!currentQuestion.value) return
  
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
  if (!canSubmit.value || !currentQuestion.value) {
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

// 初始化
onMounted(() => {
  fetchQuestions()
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

.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  width: 100%;
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