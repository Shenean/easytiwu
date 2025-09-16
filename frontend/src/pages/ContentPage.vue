<template>
  <PageContainer :show-card="false" container-class="content-page">
    <!-- 加载状态 -->
    <div
      v-if="loading"
      class="loading-container"
      style="
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;
      "
    >
      <n-spin size="large">
        <template #description>
          {{ t("content.loadingQuestions") }}
        </template>
      </n-spin>
    </div>

    <!-- 无题目数据 -->
    <n-empty
      v-else-if="questionList.length === 0"
      :description="t('content.noQuestions')"
      size="large"
    >
      <template #extra>
        <n-button @click="$router.go(-1)">
          {{ t("content.goBack") }}
        </n-button>
      </template>
    </n-empty>

    <!-- 主要内容区域 -->
    <template v-else>
      <!-- 移动端答题卡按钮 -->
      <div class="mobile-answer-card-toggle mobile-only">
        <n-button
          @click="showMobileAnswerCard = !showMobileAnswerCard"
          type="primary"
          size="small"
          class="answer-card-btn"
        >
          {{ t("content.answerSheet") }} ({{ currentQuestionIndex + 1 }}/{{
            questionList.length
          }})
        </n-button>
      </div>

      <div class="content-layout">
        <!-- 左侧：题目区域 -->
        <div class="main-content">
          <!-- 题目与作答区整合卡片 -->
          <n-card
            :title="
              t('content.questionNumber', { number: currentQuestionIndex + 1 })
            "
            size="small"
            class="question-card"
          >
            <!-- 题目内容 -->
            <div
              class="question-stem"
              v-html="sanitizeHtml(currentQuestion?.content || '')"
            ></div>

            <!-- 分割线 -->
            <n-divider class="question-divider" />

            <!-- 答题区域 -->
            <QuestionAnswer
              :question="currentQuestion!"
              v-model="localAnswer"
            />
          </n-card>

          <!-- 答案与解析（提交后或已作答显示） -->
          <n-card
            v-if="showAnswer"
            :title="t('content.answerAndAnalysis')"
            size="small"
            class="answer-card"
          >
            <n-alert
              type="success"
              :title="t('content.correctAnswer')"
              :show-icon="false"
              class="mb-4"
            >
              {{ displayCorrectAnswer }}
            </n-alert>
            <n-alert
              v-if="currentQuestion?.analysis"
              type="info"
              :title="t('content.analysis')"
              :show-icon="false"
            >
              {{ currentQuestion.analysis }}
            </n-alert>
          </n-card>
        </div>

        <!-- 桌面端右侧答题卡 -->
        <div class="sidebar desktop-only">
          <AnswerCard
            :questions="questionList"
            :current-question-id="currentQuestion?.id"
            @question-click="jumpToQuestion"
          />
        </div>
      </div>

      <!-- 移动端答题卡抽屉 -->
      <n-drawer
        v-model:show="showMobileAnswerCard"
        :width="'90%'"
        placement="bottom"
        :height="'60%'"
        class="mobile-only"
      >
        <n-drawer-content :title="t('content.answerSheet')" closable>
          <AnswerCard
            :questions="questionList"
            :current-question-id="currentQuestion?.id"
            @question-click="handleMobileQuestionClick"
            :mobile-mode="true"
          />
        </n-drawer-content>
      </n-drawer>

      <!-- 底部固定操作按钮 -->
      <div class="bottom-actions">
        <div class="action-buttons">
          <n-button
            :disabled="!(currentQuestionIndex > 0) || submitting"
            @click="prevQuestion"
            size="large"
            type="default"
          >
            {{ t("questionActions.previous") }}
          </n-button>

          <n-button
            :disabled="
              !(currentQuestionIndex < questionList.length - 1) || submitting
            "
            @click="nextQuestion"
            size="large"
            type="default"
          >
            {{ t("questionActions.next") }}
          </n-button>

          <n-button
            type="primary"
            size="large"
            @click="submitAnswer"
            :disabled="!canSubmit || submitting"
            :loading="submitting"
          >
            {{
              submitting
                ? t("questionActions.submitting")
                : t("questionActions.submit")
            }}
          </n-button>
        </div>
      </div>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {useMessage} from "naive-ui";
import {useRoute} from "vue-router";
import {useI18n} from "vue-i18n";
import {AxiosError} from "axios";
import DOMPurify from "dompurify";
import {contentAPI} from "../api/config";
import AnswerCard from "../components/common/AnswerCard.vue";

import QuestionAnswer from "../components/common/QuestionAnswer.vue";

import PageContainer from "../components/common/PageContainer.vue";

/** 类型定义 */
import type {AnswerValue, AnswerVerificationResponse, Question} from '@/types/common';

/** 路由 params */
interface RouteParams {
  bankId: string;
  type: string;
}

/** API 响应中的题目数据结构 */
interface QuestionApiData {
  id: number;
  content: string;
  type: "single" | "multiple" | "true_false" | "fill_blank" | "short_answer";
  options?: { label: string; text: string }[];
  userAnswer?: string | null;
  correctAnswer?: string;
  analysis?: string;
  isCompleted?: number;
  isCorrect?: number | null;
}

/** ContentAPI 接口类型 */
interface ContentAPI {
  getQuestions: (
    bankId: number,
    type: string
  ) => Promise<{ data: QuestionApiData[] }>;
  verifyAnswer: (
    questionId: number,
    userAnswer: string
  ) => Promise<{ data: AnswerVerificationResponse }>;
}

/** 状态 */
const questionList = ref<Question[]>([]);
const loading = ref(false);
const message = useMessage();
const route = useRoute();
const { t } = useI18n();
const currentQuestionIndex = ref(0);
const isSubmitted = ref(false);
const submitting = ref(false);
const showMobileAnswerCard = ref(false);

// 本地作答状态（可与 QuestionAnswer.vue 双向绑定）
const localAnswer = ref<AnswerValue>(null);

/** 当前题目（可能 undefined） */
const currentQuestion = computed(
  () => questionList.value[currentQuestionIndex.value]
);

/** 是否显示答案（题目已作答） */
const showAnswer = computed(() => {
  return !!currentQuestion.value && currentQuestion.value.isCompleted === 1;
});

/** 格式化显示正确答案 */
const displayCorrectAnswer = computed(() => {
  const q = currentQuestion.value;
  if (!q) return "";
  const ans = q.correctAnswer;
  if (q.type === "multiple" && ans) {
    try {
      const arr = JSON.parse(ans);
      return Array.isArray(arr) ? arr.join(", ") : ans;
    } catch {
      return ans;
    }
  }
  if (q.type === "true_false") {
    return ans === "1" ? t("content.correct") : t("content.incorrect");
  }
  return ans || t("content.none");
});

/** 是否可提交 */
const canSubmit = computed(() => {
  const q = currentQuestion.value;
  if (!q) return false;
  const val = localAnswer.value;
  if (["fill_blank", "short_answer"].includes(q.type)) {
    return !!String(val ?? "").trim();
  }
  return Array.isArray(val)
    ? val.length > 0
    : val !== null && val !== undefined && String(val) !== "";
});

/** HTML内容安全过滤 */
const sanitizeHtml = (html: string): string => {
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'span', 'div'],
    ALLOWED_ATTR: ['class', 'style'],
    FORBID_TAGS: ['script', 'object', 'embed', 'form', 'input']
  });
};

/** 获取题目 */
async function fetchQuestions() {
  try {
    loading.value = true;
    const params = route.params as unknown as RouteParams;
    const bankId = parseInt(params.bankId);
    const type = params.type;

    if (!bankId || !type) {
      message.error("参数错误，请重新进入");
      return;
    }

    // 运行时校验，若导入路径或导出不匹配能更早定位
    if (
      !contentAPI ||
      typeof (contentAPI as ContentAPI).getQuestions !== "function"
    ) {
      message.error(t("message.internalError"));
      return;
    }

    // 注意：你在 config 中用 post('/content/questions', { bankId, type })
    const response = await contentAPI.getQuestions(bankId, type);

    if (response?.data && Array.isArray(response.data)) {
      questionList.value = response.data.map((item: QuestionApiData) => ({
        id: item.id,
        content: item.content,
        type: item.type,
        options: item.options || [],
        userAnswer: item.userAnswer ?? null,
        correctAnswer: item.correctAnswer ?? "",
        analysis: item.analysis ?? "",
        isCompleted: item.isCompleted ?? 0,
        isCorrect: item.isCorrect ?? null,
      }));

      if (questionList.value.length === 0) {
        message.warning(
          type === "wrong"
            ? t("content.noWrongQuestions")
            : t("content.noBankQuestions")
        );
      } else {
        // 获取题库名称和练习类型信息
        const bankName =
          (route.query.bankName as string) ||
          `${t("content.bankId")}: ${bankId}`;
        const practiceType =
          type === "wrong"
            ? t("content.wrongQuestions")
            : t("content.allPractice");
        message.success(
          t("content.currentBank", {
            bankName,
            practiceType,
            count: questionList.value.length,
          })
        );
        currentQuestionIndex.value = 0;
        initLocalAnswer();
      }
    } else {
      message.error(t("message.dataFormatError"));
    }
  } catch (error) {
    if (error instanceof AxiosError) {
      message.error(
        `${t("content.fetchQuestionsFailed")}: ${
          error.response?.data?.message || error.message
        }`
      );
    } else {
      message.error(
        `${t("content.fetchQuestionsFailed")}，${t("message.retryLater")}`
      );
    }
  } finally {
    loading.value = false;
  }
}

/** 初始化 localAnswer，基于 currentQuestion.userAnswer */
function initLocalAnswer() {
  const q = currentQuestion.value;
  isSubmitted.value = false;

  if (!q) {
    localAnswer.value = null;
    return;
  }

  const ua = q.userAnswer;

  if (!ua) {
    localAnswer.value = q.type === "multiple" ? [] : "";
    return;
  }

  if (q.type === "multiple") {
    try {
      localAnswer.value = JSON.parse(ua);
    } catch {
      localAnswer.value = [];
    }
  } else {
    localAnswer.value = ua;
  }
}

/** 提交答案 */
async function submitAnswer() {
  if (!canSubmit.value || !currentQuestion.value) {
    message.warning(t("content.pleaseAnswer"));
    return;
  }

  if (submitting.value) return;

  let answerToSave = "";

  if (currentQuestion.value.type === "multiple") {
    const arr = Array.isArray(localAnswer.value)
      ? (localAnswer.value as (string | number)[])
      : [];
    const sorted = arr.slice().sort();
    answerToSave = JSON.stringify(sorted);
  } else {
    answerToSave = localAnswer.value == null ? "" : String(localAnswer.value);
  }

  try {
    submitting.value = true;

    if (
      !contentAPI ||
      typeof (contentAPI as ContentAPI).verifyAnswer !== "function"
    ) {
      message.error(t("message.internalError"));
      return;
    }

    // 你 config 中的 verifyAnswer 接口签名为 (questionId, userAnswer)
    const response = await contentAPI.verifyAnswer(
      currentQuestion.value.id,
      answerToSave
    );
    const result: AnswerVerificationResponse = response.data;

    // 更新 questionList 中对应题目（避免直接修改 computed）
    const qIndex = currentQuestionIndex.value;
    const q = questionList.value[qIndex];
    q.userAnswer = result.userAnswer;
    q.correctAnswer = result.correctAnswer;
    q.analysis = result.analysis;
    q.isCompleted = 1;
    q.isCorrect = result.isCorrect ? 1 : 0;

    if (result.isCorrect) {
      message.success(result.message || t("content.answerCorrect"), {
        duration: 3000,
      });
    } else {
      message.error(result.message || t("content.answerWrong"), {
        duration: 3000,
      });
    }

    // 触发响应式更新（浅拷贝数组）
    questionList.value = [...questionList.value];
  } catch (error) {
    if (error instanceof AxiosError) {
      if (error.response) {
        const status = error.response.status;
        const errorMsg =
          error.response.data?.message || t("message.serverError");
        switch (status) {
          case 400:
            message.error(`${t("message.paramError")}: ${errorMsg}`);
            break;
          case 404:
            message.error(t("content.questionNotFound"));
            break;
          case 500:
            message.error(t("message.serverInternalError"));
            break;
          default:
            message.error(`${t("content.submitFailed")}: ${errorMsg}`);
        }
      } else if (error.request) {
        message.error(t("message.networkError"));
      } else {
        message.error(`${t("message.requestConfigError")}: ${error.message}`);
      }
    } else {
      message.error(
        `${t("content.submitAnswerFailed")}，${t("message.retryLater")}`
      );
    }
  } finally {
    submitting.value = false;
  }
}

/** 跳题 / 翻页 */
function jumpToQuestion(id: number) {
  const index = questionList.value.findIndex((q) => q.id === id);
  if (index !== -1) {
    currentQuestionIndex.value = index;
    initLocalAnswer();
  }
}

function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
    initLocalAnswer();
  }
}

function nextQuestion() {
  if (currentQuestionIndex.value < questionList.value.length - 1) {
    currentQuestionIndex.value++;
    initLocalAnswer();
  } else {
    message.info(t("content.lastQuestion"));
  }
}

/** 移动端答题卡处理 */
function handleMobileQuestionClick(id: number) {
  jumpToQuestion(id);
  showMobileAnswerCard.value = false;
}

/** 生命周期 */
onMounted(() => {
  fetchQuestions();
});

/** 当题目切换时，重置本地答案状态 */
watch(currentQuestion, () => {
  initLocalAnswer();
});
</script>

<style scoped>
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
  top: var(--spacing-10);
  /* 80px */
  right: var(--spacing-2);
  z-index: 999;

  border-radius: var(--spacing-3);
  padding: var(--spacing-1);
  box-shadow: var(--shadow-card-light);
  backdrop-filter: blur(10px);
}

.answer-card-btn {
  border-radius: var(--spacing-2) !important;
  font-size: 12px;
  padding: var(--spacing-1) var(--spacing-2);
  font-weight: 600;
}

/* 主要内容布局 - 桌面端响应式设计 */
.content-layout {
  display: flex;
  gap: var(--spacing-4); /* 32px */
  max-width: 1400px;
  /* 350 * 4px */
  margin: 0 auto;
  min-height: calc(100vh - 140px);
  padding: 0 var(--spacing-3); /* 24px */
  position: relative;
}

/* 左侧主内容区 - 固定60%宽度 */
.main-content {
  width: 60%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3); /* 24px */
  min-width: 0;
  flex-shrink: 0;
  /* 固定宽度为页面的60% */
}

/* 右侧答题卡区域 - 占用剩余空间 */
.sidebar {
  flex: 1;
  min-width: 280px;
  /* 70 * 4px */
  max-width: 352px;
  /* 88 * 4px */
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
  font-size: var(--font-size-base);
  line-height: 1.6;
  color: var(--color-text-primary);
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  box-sizing: border-box;
}

/* 题目与答题区域分割线 */
.question-divider {
  margin: var(--spacing-5) 0;
  border-color: var(--color-border-secondary);
}

/* 为底部按钮留出空间 */
.content-layout {
  padding-bottom: var(--spacing-20);
}

/* 通用样式 */
.mb-4 {
  margin-bottom: var(--spacing-md);
}

/* 加载和空状态 */
.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  width: 100%;

  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xs);
}

/* 卡片样式优化 - 统一固定宽度 */
:deep(.n-card) {
  border-radius: var(--card-border-radius);
  box-shadow: var(--card-unified-shadow);
  border: none;
  width: var(--card-standard-width);
  max-width: var(--card-content-max-width);
  margin: 0 auto;
  box-sizing: border-box;
  transition: box-shadow 0.3s ease;
}

:deep(.n-card:hover) {
  box-shadow: var(--card-unified-shadow-hover);
}

:deep(.n-card .n-card-header) {
  padding: var(--card-padding-desktop) var(--card-padding-desktop)
    var(--spacing-4);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: 600;
}

:deep(.n-card .n-card-content) {
  padding: var(--card-padding-desktop);
  word-wrap: break-word;
  overflow-wrap: break-word;
  /* 确保长文本不会破坏固定宽度布局 */
}

/* ===== 桌面端大屏幕优化 ===== */
@media (min-width: 1200px) {
  .content-layout {
    gap: var(--spacing-10);
    /* 40px */
    padding: 0 var(--spacing-6);
  }

  .main-content {
    gap: var(--spacing-7);
    /* 28px */
    width: 60%;
    /* 保持60%宽度 */
  }

  .sidebar {
    max-width: var(--spacing-95);
    /* 95 * 4px */
  }
}

/* ===== 中等屏幕优化 ===== */
@media (min-width: 768px) and (max-width: 1199px) {
  .content-layout {
    gap: var(--spacing-7);
    /* 28px */
    padding: 0 var(--spacing-4);
  }

  .main-content {
    width: 60%;
    /* 保持60%宽度 */
  }

  .sidebar {
    min-width: var(--spacing-65);
    /* 65 * 4px */
    max-width: var(--spacing-80);
    /* 80 * 4px */
  }
}

/* ===== 移动端响应式设计 ===== */
@media (max-width: 767px) {
  .page-container {
    padding: var(--container-responsive-padding-mobile);
  }

  .mobile-answer-card-toggle {
    top: var(--spacing-18);
    /* 72px */
    right: var(--spacing-3);
  }

  .content-layout {
    flex-direction: column;
    gap: var(--spacing-md);
    padding: 0 var(--spacing-3) var(--spacing-25) var(--spacing-3);
    /* 为移动端底部按钮留更多空间 */
  }

  .main-content {
    gap: var(--spacing-md);
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
    font-size: var(--font-size-sm);
    line-height: 1.5;
  }

  /* 移动端卡片优化 */
  :deep(.n-card) {
    box-shadow: var(--card-unified-shadow-tablet);
    max-width: var(--card-max-width-tablet);
  }

  :deep(.n-card .n-card-header) {
    padding: var(--card-padding-tablet) var(--card-padding-tablet)
      var(--spacing-3);
    font-size: var(--font-size-sm);
  }

  :deep(.n-card .n-card-content) {
    padding: var(--card-padding-tablet);
  }

  /* 移动端抽屉样式 */
  :deep(.n-drawer .n-drawer-content) {
    padding: 0;
  }

  :deep(.n-drawer .n-drawer-header) {
    padding: var(--spacing-md);
    border-bottom: 1px solid var(--color-border-tertiary);
  }

  :deep(.n-drawer .n-drawer-body) {
    padding: var(--spacing-md);
  }
}

@media (max-width: 639px) {
  .page-container {
    padding: var(--container-responsive-padding-small);
  }

  .mobile-answer-card-toggle {
    top: var(--spacing-16);
    /* 64px */
    right: var(--spacing-2);
  }

  .answer-card-btn {
    font-size: var(--font-size-xs);
    padding: var(--spacing-2) var(--spacing-3);
    /* 8px 12px */
  }

  .content-layout {
    gap: var(--spacing-3);
    padding: 0 var(--spacing-2) var(--spacing-28) var(--spacing-2);
  }

  .main-content {
    gap: var(--spacing-3);
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
    font-size: var(--font-size-sm);
  }

  :deep(.n-card) {
    box-shadow: var(--card-unified-shadow-mobile);
  }
}

/* 底部固定操作区 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--n-color);
  border-top: 1px solid var(--n-border-color);
  padding: var(--spacing-4);
  z-index: 100;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-3);
  justify-content: center;
  align-items: center;
  max-width: 600px;
  margin: 0 auto;
}

@media (max-width: 1023px) {
  .action-buttons {
    gap: var(--spacing-2);
  }

  .action-buttons .n-button {
    flex: 1;
    min-width: 0;
  }
}

:deep(.n-card .n-card-header) {
  padding: var(--card-padding-mobile) var(--card-padding-mobile)
    var(--spacing-3);
  font-size: var(--font-size-sm);
}

:deep(.n-card .n-card-content) {
  padding: var(--card-padding-mobile);
}
</style>
