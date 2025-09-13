<template>
  <PageContainer :show-card="false" container-class="content-page">
    <!-- 加载状态 -->
    <LoadingContainer v-if="loading" :loading="loading" description="正在加载题目..." height="300px" />

    <!-- 无题目数据 -->
    <EmptyState v-else-if="questionList.length === 0" description="暂无题目数据" icon-type="document"
      :show-default-action="true" action-text="返回上一页" action-type="default" @action="$router.go(-1)" />

    <!-- 主要内容区域 -->
    <template v-else>
      <!-- 移动端答题卡按钮 -->
      <div class="mobile-answer-card-toggle mobile-only">
        <BaseButton @click="showMobileAnswerCard = !showMobileAnswerCard" type="primary" size="small"
          class="answer-card-btn">
          答题卡 ({{ currentQuestionIndex + 1 }}/{{ questionList.length }})
        </BaseButton>
      </div>

      <div class="content-layout">
        <!-- 左侧：题目区域 -->
        <div class="main-content">
          <!-- 题目与作答区整合卡片 -->
          <n-card :title="`第 ${currentQuestionIndex + 1} 题`" size="small" class="question-card">
            <!-- 题目内容 -->
            <div class="question-stem" v-html="currentQuestion?.content || ''"></div>

            <!-- 分割线 -->
            <n-divider class="question-divider" />

            <!-- 答题区域 -->
            <QuestionAnswer :question="currentQuestion!" v-model="localAnswer" />
          </n-card>

          <!-- 答案与解析（提交后或已作答显示） -->
          <n-card v-if="showAnswer" title="答案与解析" size="small" class="answer-card">
            <n-alert type="success" title="正确答案" :show-icon="false" class="mb-4">
              {{ displayCorrectAnswer }}
            </n-alert>
            <n-alert v-if="currentQuestion?.analysis" type="info" title="解析" :show-icon="false">
              {{ currentQuestion.analysis }}
            </n-alert>
          </n-card>
        </div>

        <!-- 桌面端右侧答题卡 -->
        <div class="sidebar desktop-only">
          <AnswerCard :questions="questionList" :current-question-id="currentQuestion?.id"
            @question-click="jumpToQuestion" />
        </div>
      </div>

      <!-- 移动端答题卡抽屉 -->
      <n-drawer v-model:show="showMobileAnswerCard" :width="'90%'" placement="bottom" :height="'60%'"
        class="mobile-only">
        <n-drawer-content title="答题卡" closable>
          <AnswerCard :questions="questionList" :current-question-id="currentQuestion?.id"
            @question-click="handleMobileQuestionClick" :mobile-mode="true" />
        </n-drawer-content>
      </n-drawer>

      <!-- 底部固定操作按钮 -->
      <QuestionActions :has-prev="currentQuestionIndex > 0" :has-next="currentQuestionIndex < questionList.length - 1"
        :can-submit="canSubmit" :loading="submitting" @prev="prevQuestion" @next="nextQuestion"
        @submit="submitAnswer" />
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'
import {useMessage} from 'naive-ui'
import {useRoute} from 'vue-router'
import {AxiosError} from 'axios'
import {contentAPI} from '../api/config'
import AnswerCard from '../components/common/AnswerCard.vue'
import QuestionActions from '../components/common/QuestionActions.vue'
import QuestionAnswer from '../components/common/QuestionAnswer.vue'
import BaseButton from '../components/common/BaseButton.vue'
import LoadingContainer from '../components/common/LoadingContainer.vue'
import EmptyState from '../components/common/EmptyState.vue'
import PageContainer from '../components/common/PageContainer.vue'

/** 类型定义 */
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

type AnswerValue = string | number | boolean | (string | number)[] | null


interface AnswerVerificationResponse {
  isCorrect: boolean
  correctAnswer: string
  analysis: string
  message: string
  questionId: number
  userAnswer: string
}

/** 路由 params */
interface RouteParams {
  bankId: string
  type: string
}

/** API 响应中的题目数据结构 */
interface QuestionApiData {
  id: number
  content: string
  type: 'single' | 'multiple' | 'true_false' | 'fill_blank' | 'short_answer'
  options?: { label: string; text: string }[]
  userAnswer?: string | null
  correctAnswer?: string
  analysis?: string
  isCompleted?: number
  isCorrect?: number | null
}

/** ContentAPI 接口类型 */
interface ContentAPI {
  getQuestions: (bankId: number, type: string) => Promise<{ data: QuestionApiData[] }>
  verifyAnswer: (questionId: number, userAnswer: string) => Promise<{ data: AnswerVerificationResponse }>
}

/** 状态 */
const questionList = ref<Question[]>([])
const loading = ref(false)
const message = useMessage()
const route = useRoute()
const currentQuestionIndex = ref(0)
const isSubmitted = ref(false)
const submitting = ref(false)
const showMobileAnswerCard = ref(false)

// 本地作答状态（可与 QuestionAnswer.vue 双向绑定）
const localAnswer = ref<AnswerValue>(null)

/** 当前题目（可能 undefined） */
const currentQuestion = computed(() => questionList.value[currentQuestionIndex.value])

/** 是否显示答案（题目已作答） */
const showAnswer = computed(() => {
  return !!currentQuestion.value && currentQuestion.value.isCompleted === 1
})

/** 格式化显示正确答案 */
const displayCorrectAnswer = computed(() => {
  const q = currentQuestion.value
  if (!q) return ''
  const ans = q.correctAnswer
  if (q.type === 'multiple' && ans) {
    try {
      const arr = JSON.parse(ans)
      return Array.isArray(arr) ? arr.join(', ') : ans
    } catch {
      return ans
    }
  }
  if (q.type === 'true_false') {
    return ans === '1' ? '正确' : '错误'
  }
  return ans || '无'
})

/** 是否可提交 */
const canSubmit = computed(() => {
  const q = currentQuestion.value
  if (!q) return false
  const val = localAnswer.value
  if (['fill_blank', 'short_answer'].includes(q.type)) {
    return !!String(val ?? '').trim()
  }
  return Array.isArray(val) ? val.length > 0 : val !== null && val !== undefined && String(val) !== ''
})

/** 获取题目 */
async function fetchQuestions() {
  try {
    loading.value = true
    const params = route.params as unknown as RouteParams
    const bankId = parseInt(params.bankId)
    const type = params.type

    if (!bankId || !type) {
      message.error('参数错误，请重新进入')
      return
    }

    // 运行时校验，若导入路径或导出不匹配能更早定位
    if (!contentAPI || typeof (contentAPI as ContentAPI).getQuestions !== 'function') {
      console.error('contentAPI.getQuestions is not a function — 请检查 frontend/src/api/config.ts 的导出')
      message.error('内部调用错误：请检查 API 导出')
      return
    }

    // 注意：你在 config 中用 post('/content/questions', { bankId, type })
    const response = await contentAPI.getQuestions(bankId, type)

    if (response?.data && Array.isArray(response.data)) {
      questionList.value = response.data.map((item: QuestionApiData) => ({
        id: item.id,
        content: item.content,
        type: item.type,
        options: item.options || [],
        userAnswer: item.userAnswer ?? null,
        correctAnswer: item.correctAnswer ?? '',
        analysis: item.analysis ?? '',
        isCompleted: item.isCompleted ?? 0,
        isCorrect: item.isCorrect ?? null
      }))

      if (questionList.value.length === 0) {
        message.warning(type === 'wrong' ? '暂无错题数据' : '该题库暂无题目')
      } else {
        // 获取题库名称和练习类型信息
        const bankName = route.query.bankName as string || `题库 ID: ${bankId}`
        const practiceType = type === 'wrong' ? '错题集' : '全部练习'
        message.success(`当前题库为${bankName}-${practiceType}，共${questionList.value.length}题`)
        currentQuestionIndex.value = 0
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

/** 初始化 localAnswer，基于 currentQuestion.userAnswer */
function initLocalAnswer() {
  const q = currentQuestion.value
  isSubmitted.value = false

  if (!q) {
    localAnswer.value = null
    return
  }

  const ua = q.userAnswer

  if (!ua) {
    localAnswer.value = q.type === 'multiple' ? [] : ''
    return
  }

  if (q.type === 'multiple') {
    try {
      localAnswer.value = JSON.parse(ua)
    } catch {
      localAnswer.value = []
    }
  } else {
    localAnswer.value = ua
  }
}

/** 提交答案 */
async function submitAnswer() {
  if (!canSubmit.value || !currentQuestion.value) {
    message.warning('请先完成作答！')
    return
  }

  if (submitting.value) return

  let answerToSave = ''

  if (currentQuestion.value.type === 'multiple') {
    const arr = Array.isArray(localAnswer.value) ? (localAnswer.value as (string | number)[]) : []
    const sorted = arr.slice().sort()
    answerToSave = JSON.stringify(sorted)
  } else {
    answerToSave = localAnswer.value == null ? '' : String(localAnswer.value)
  }

  try {
    submitting.value = true

    if (!contentAPI || typeof (contentAPI as ContentAPI).verifyAnswer !== 'function') {
      console.error('contentAPI.verifyAnswer is not a function — 请检查 frontend/src/api/config.ts 的导出')
      message.error('内部调用错误：请检查 API 导出')
      return
    }

    // 你 config 中的 verifyAnswer 接口签名为 (questionId, userAnswer)
    const response = await contentAPI.verifyAnswer(currentQuestion.value.id, answerToSave)
    const result: AnswerVerificationResponse = response.data

    // 更新 questionList 中对应题目（避免直接修改 computed）
    const qIndex = currentQuestionIndex.value
    const q = questionList.value[qIndex]
    q.userAnswer = result.userAnswer
    q.correctAnswer = result.correctAnswer
    q.analysis = result.analysis
    q.isCompleted = 1
    q.isCorrect = result.isCorrect ? 1 : 0

    if (result.isCorrect) {
      message.success(result.message || '回答正确！🎉', { duration: 3000 })
    } else {
      message.error(result.message || '回答错误，继续加油！', { duration: 3000 })
    }

    // 触发响应式更新（浅拷贝数组）
    questionList.value = [...questionList.value]
  } catch (error) {
    console.error('提交答案失败:', error)
    if (error instanceof AxiosError) {
      if (error.response) {
        const status = error.response.status
        const errorMsg = error.response.data?.message || '服务器错误'
        switch (status) {
          case 400:
            message.error(`参数错误: ${errorMsg}`)
            break
          case 404:
            message.error('题目不存在，请刷新页面重试')
            break
          case 500:
            message.error('服务器内部错误，请稍后重试')
            break
          default:
            message.error(`提交失败: ${errorMsg}`)
        }
      } else if (error.request) {
        message.error('网络连接失败，请检查网络后重试')
      } else {
        message.error(`请求配置错误: ${error.message}`)
      }
    } else {
      message.error('提交答案失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

/** 跳题 / 翻页 */
function jumpToQuestion(id: number) {
  const index = questionList.value.findIndex(q => q.id === id)
  if (index !== -1) {
    currentQuestionIndex.value = index
    initLocalAnswer()
  }
}

function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    initLocalAnswer()
  }
}

function nextQuestion() {
  if (currentQuestionIndex.value < questionList.value.length - 1) {
    currentQuestionIndex.value++
    initLocalAnswer()
  } else {
    message.info('已是最后一题')
  }
}

/** 移动端答题卡处理 */
function handleMobileQuestionClick(id: number) {
  jumpToQuestion(id)
  showMobileAnswerCard.value = false
}

/** 生命周期 */
onMounted(() => {
  fetchQuestions()
})

/** 当题目切换时，重置本地答案状态 */
watch(currentQuestion, () => {
  initLocalAnswer()
})
</script>

<style scoped>
/* 页面容器样式已移至 PageContainer 组件 */

/* 响应式显示控制 */
.desktop-only {
  display: block;
}

.mobile-only {
  display: none;
}

@media (max-width: 768px) {
  .desktop-only {
    display: none;
  }

  .mobile-only {
    display: block;
  }
}

/* 移动端答题卡切换按钮 */
.mobile-answer-card-toggle {
  position: fixed;
  top: 80px;
  right: 16px;
  z-index: 999;

  border-radius: 20px;
  padding: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
}

.answer-card-btn {
  border-radius: 16px !important;
  font-size: 12px;
  padding: 8px 12px;
  font-weight: 600;
}

/* 主要内容布局 - 桌面端响应式设计 */
.content-layout {
  display: flex;
  gap: 32px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
  padding: 0 20px;
  position: relative;
}

/* 左侧主内容区 - 固定60%宽度 */
.main-content {
  width: 60%;
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;
  flex-shrink: 0;
  /* 固定宽度为页面的60% */
}

/* 右侧答题卡区域 - 占用剩余空间 */
.sidebar {
  flex: 1;
  min-width: 280px;
  max-width: 350px;
  position: sticky;
  top: 20px;
  height: fit-content;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  /* 占用剩余空间，但限制最大最小宽度 */
}

/* 题目相关样式 - 优化布局对齐 */
.question-card,
.answer-card {
  margin-bottom: 0;
  width: 100%;
  box-sizing: border-box;
  /* 确保所有卡片占满容器宽度并保持对齐 */
}

/* 答案解析卡片与答题区对齐 */
.answer-card {
  margin-top: 0;
  /* 与上方题目卡片保持一致的间距 */
}

.question-stem {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  box-sizing: border-box;
}

/* 题目与答题区域分割线 */
.question-divider {
  margin: 20px 0;
  border-color: #e8e8e8;
}

/* 为底部按钮留出空间 */
.content-layout {
  padding-bottom: 80px;
}

/* 通用样式 */
.mb-4 {
  margin-bottom: 16px;
}

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  width: 100%;

  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 卡片样式优化 - 统一固定宽度 */
:deep(.n-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: none;
  width: 100%;
  box-sizing: border-box;
}

:deep(.n-card .n-card-header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.n-card .n-card-content) {
  padding: 20px;
  word-wrap: break-word;
  overflow-wrap: break-word;
  /* 确保长文本不会破坏固定宽度布局 */
}

/* ===== 桌面端大屏幕优化 ===== */
@media (min-width: 1200px) {
  .content-layout {
    gap: 40px;
    padding: 0 24px;
  }

  .main-content {
    gap: 28px;
    width: 60%;
    /* 保持60%宽度 */
  }

  .sidebar {
    max-width: 380px;
  }
}

/* ===== 中等屏幕优化 ===== */
@media (min-width: 769px) and (max-width: 1199px) {
  .content-layout {
    gap: 28px;
    padding: 0 16px;
  }

  .main-content {
    width: 60%;
    /* 保持60%宽度 */
  }

  .sidebar {
    min-width: 260px;
    max-width: 320px;
  }
}

/* ===== 移动端响应式设计 ===== */
@media (max-width: 768px) {
  .page-container {
    padding: 12px 8px;
  }

  .mobile-answer-card-toggle {
    top: 70px;
    right: 12px;
  }

  .content-layout {
    flex-direction: column;
    gap: 16px;
    padding: 0 12px 100px 12px;
    /* 为移动端底部按钮留更多空间 */
  }

  .main-content {
    gap: 16px;
    width: 100%;
    max-width: 100%;
    margin-right: 0;
    /* 移动端占满宽度，重置桌面端的60%宽度设置 */
  }

  .sidebar {
    position: static;
    width: 100%;
    height: auto;
    max-height: none;
    overflow-y: visible;
    /* 移动端重置固定定位 */
  }

  .question-stem {
    font-size: 15px;
    line-height: 1.5;
  }

  /* 移动端卡片优化 */
  :deep(.n-card .n-card-header) {
    padding: 12px 16px;
    font-size: 14px;
  }

  :deep(.n-card .n-card-content) {
    padding: 16px;
  }

  /* 移动端抽屉样式 */
  :deep(.n-drawer .n-drawer-content) {
    padding: 0;
  }

  :deep(.n-drawer .n-drawer-header) {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  :deep(.n-drawer .n-drawer-body) {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 8px 4px;
  }

  .mobile-answer-card-toggle {
    top: 65px;
    right: 8px;
  }

  .answer-card-btn {
    font-size: 11px;
    padding: 6px 10px;
  }

  .content-layout {
    gap: 12px;
    padding: 0 8px 110px 8px;
  }

  .main-content {
    gap: 12px;
    width: 100%;
    max-width: 100%;
    margin-right: 0;
    /* 小屏幕设备占满宽度，重置桌面端60%宽度设置 */
  }

  .sidebar {
    position: static;
    width: 100%;
    height: auto;
    max-height: none;
    overflow-y: visible;
    /* 小屏幕设备重置固定定位 */
  }

  .question-stem {
    font-size: 14px;
  }

  :deep(.n-card .n-card-header) {
    padding: 10px 12px;
    font-size: 13px;
  }

  :deep(.n-card .n-card-content) {
    padding: 12px;
  }
}
</style>
