<template>
  <div class="page-container">
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
    
    <!-- 主要内容区域 -->
    <template v-else>
      <div class="content-layout">
        <!-- 左侧：题目区域 -->
        <div class="main-content">
          <!-- 题干 -->
          <n-card :title="`第 ${currentQuestionIndex + 1} 题`" size="small" class="question-card">
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
                  :disabled="false"
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
                  :disabled="false"
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
              <n-radio :value="'1'" :disabled="false">正确</n-radio>
              <n-radio :value="'0'" :disabled="false">错误</n-radio>
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
              :disabled="false"
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
        </div>

        <!-- 右侧：答题卡 -->
        <div class="sidebar">
          <n-card title="答题卡" size="small" class="answer-card-container">
            <n-grid :cols="4" :x-gap="8" :y-gap="8">
              <n-grid-item v-for="(q, index) in questionList" :key="q.id">
                <n-button
                    :type="getCardButtonType(q)"
                    :size="getCardButtonSize"
                    block
                    @click="jumpToQuestion(q.id)"
                    :bordered="false"
                >
                  {{ index + 1 }}
                </n-button>
              </n-grid-item>
            </n-grid>
          </n-card>
        </div>
      </div>

      <!-- 底部固定操作按钮 -->
       <div class="bottom-actions">
         <div class="action-buttons">
           <n-button
               :disabled="currentQuestionIndex === 0 || submitting"
               @click="prevQuestion"
               size="large"
               type="default"
           >
             上一题
           </n-button>

           <n-button
               :disabled="currentQuestionIndex >= questionList.length - 1 || submitting"
               @click="nextQuestion"
               size="large"
               type="default"
           >
             下一题
           </n-button>

           <n-button
               type="primary"
               size="large"
               @click="submitAnswer"
               :disabled="!canSubmit || submitting"
               :loading="submitting"
           >
             {{ submitting ? '提交中...' : '提交答案' }}
           </n-button>
         </div>
       </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { useRoute } from 'vue-router'
import { AxiosError } from 'axios'
import api, { contentAPI } from '../api/config'

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

interface AnswerVerificationResponse {
  isCorrect: boolean
  correctAnswer: string
  analysis: string
  message: string
  questionId: number
  userAnswer: string
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

// 是否显示答案（已作答时显示）
const showAnswer = computed(() => {
  return currentQuestion.value && currentQuestion.value.isCompleted === 1
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

    const response = await contentAPI.getQuestions(bankId, type)
    
    if (response.data && Array.isArray(response.data)) {
      questionList.value = response.data.map((item: any) => ({
        id: item.id,
        content: item.content,
        type: item.type,
        options: item.options || [],
        userAnswer: item.userAnswer || null,
        correctAnswer: item.correctAnswer || '',
        analysis: item.analysis || '',
        isCompleted: item.isCompleted || 0,
        isCorrect: item.isCorrect || null
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
  
  // 重置提交状态，允许重新作答
  isSubmitted.value = false
  
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

// 提交答案状态
const submitting = ref(false)

// 提交答案
async function submitAnswer() {
  if (!canSubmit.value || !currentQuestion.value) {
    message.warning('请先完成作答！')
    return
  }

  if (submitting.value) {
    return // 防止重复提交
  }

  let answerToSave: string

  if (currentQuestion.value.type === 'multiple') {
    const sorted = (localAnswer.value as string[]).sort()
    answerToSave = JSON.stringify(sorted)
  } else {
    answerToSave = localAnswer.value.toString()
  }

  try {
    submitting.value = true
    
    // 调用后端验证接口
     const response = await contentAPI.verifyAnswer(
       currentQuestion.value.id,
       answerToSave
     )

    const result: AnswerVerificationResponse = response.data
    
    // 更新题目状态
    currentQuestion.value.userAnswer = result.userAnswer
    currentQuestion.value.correctAnswer = result.correctAnswer
    currentQuestion.value.analysis = result.analysis
    currentQuestion.value.isCompleted = 1
    currentQuestion.value.isCorrect = result.isCorrect ? 1 : 0

    // 显示验证结果
     if (result.isCorrect) {
       message.success(result.message || '回答正确！🎉', {
         duration: 3000
       })
     } else {
       message.error(result.message || '回答错误，继续加油！', {
         duration: 3000
       })
     }
     
     // 强制更新答题卡显示
     setTimeout(() => {
       // 触发响应式更新
       questionList.value = [...questionList.value]
     }, 100)
    
  } catch (error) {
     console.error('提交答案失败:', error)
     
     if (error instanceof AxiosError) {
       if (error.response) {
         // 服务器返回错误
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
         // 网络错误
         message.error('网络连接失败，请检查网络后重试')
       } else {
         // 其他错误
         message.error(`请求配置错误: ${error.message}`)
       }
     } else {
       message.error('提交答案失败，请稍后重试')
     }
   } finally {
     submitting.value = false
   }
}

// 跳转题目
function jumpToQuestion(id: number) {
  const index = questionList.value.findIndex(q => q.id === id)
  if (index !== -1) {
    currentQuestionIndex.value = index
    initLocalAnswer() // initLocalAnswer会根据题目状态设置isSubmitted
  }
}

// 上一题
function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    initLocalAnswer() // initLocalAnswer会根据题目状态设置isSubmitted
  }
}

// 下一题
function nextQuestion() {
  if (currentQuestionIndex.value < questionList.value.length - 1) {
    currentQuestionIndex.value++
    initLocalAnswer() // initLocalAnswer会根据题目状态设置isSubmitted
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
  initLocalAnswer() // initLocalAnswer会根据题目状态设置isSubmitted
})
</script>

<style scoped>
/* 页面容器 */
.page-container {
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f5f5;
}

/* 主要内容布局 */
.content-layout {
  display: flex;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 左侧主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

/* 右侧答题卡区域 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.answer-card-container {
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

/* 题目相关样式 */
.question-card,
.answer-area,
.answer-card {
  margin-bottom: 0;
}

.question-stem {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
}

/* 底部固定操作区 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #e0e0e0;
  padding: 16px 20px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 为底部按钮留出空间 */
.content-layout {
  padding-bottom: 80px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-layout {
    flex-direction: column;
    gap: 20px;
  }
  
  .sidebar {
    width: 100%;
    order: -1;
  }
  
  .answer-card-container {
    position: static;
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }
  
  .content-layout {
    gap: 16px;
  }
  
  .bottom-actions {
    padding: 12px 16px;
  }
  
  .action-buttons {
    gap: 12px;
  }
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
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 表单元素样式 */
:deep(.n-radio-content),
:deep(.n-checkbox-content) {
  font-size: 15px;
  line-height: 1.5;
}

:deep(.n-radio),
:deep(.n-checkbox) {
  margin-bottom: 8px;
}

/* 答题卡按钮样式优化 */
:deep(.answer-card-container .n-button) {
  height: 40px;
  font-weight: 600;
  transition: all 0.3s ease;
  border-radius: 6px;
}

:deep(.answer-card-container .n-button--default) {
  background-color: #ffffff !important;
  border: 2px solid #e0e0e0 !important;
  color: #666 !important;
}

:deep(.answer-card-container .n-button--success) {
  background-color: #52c41a !important;
  border: 2px solid #52c41a !important;
  color: #fff !important;
}

:deep(.answer-card-container .n-button--error) {
  background-color: #ff4d4f !important;
  border: 2px solid #ff4d4f !important;
  color: #fff !important;
}

:deep(.answer-card-container .n-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.answer-card-container .n-button--default:hover) {
  border-color: #1890ff !important;
  color: #1890ff !important;
}

/* 卡片样式优化 */
:deep(.n-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: none;
}

:deep(.n-card .n-card-header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.n-card .n-card-content) {
  padding: 20px;
}
</style>