<template>
  <PageContainer :show-card="false" container-class="content-page">
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
      <t-loading size="large" :text="t('content.loadingQuestions')" />
    </div>

    <t-empty
      v-else-if="questionList.length === 0"
      :description="t('content.noQuestions')"
      size="large"
    >
      <template #action>
        <t-button @click="router.go(-1)">
          {{ t("content.goBack") }}
        </t-button>
      </template>
    </t-empty>

    <template v-else>
      <div class="content-layout">
        <div class="main-content">
          <t-card
            :title="
              t('content.questionNumber', { number: currentQuestionIndex + 1 })
            "
            size="small"
            class="question-card"
          >
            <div
              class="question-stem"
              v-html="sanitizeHtml(currentQuestion?.content || '')"
            ></div>

            <t-divider class="question-divider" />

            <QuestionAnswer
              :question="currentQuestion!"
              v-model="localAnswer"
            />
          </t-card>

          <t-card
            v-if="showAnswer"
            :title="t('content.answerAndAnalysis')"
            size="small"
            class="answer-card"
          >
            <t-alert
              theme="success"
              :title="t('content.correctAnswer')"
              class="mb-4"
            >
              {{ displayCorrectAnswer }}
            </t-alert>
            <t-alert
              v-if="currentQuestion?.analysis"
              theme="info"
              :title="t('content.analysis')"
            >
              {{ currentQuestion.analysis }}
            </t-alert>
          </t-card>
        </div>

        <div class="sidebar">
          <AnswerCard
            :questions="questionList"
            :current-question-id="currentQuestion?.id"
            @question-click="jumpToQuestion"
          />
        </div>
      </div>

      <t-divider />

      <div class="bottom-actions">
        <div class="action-buttons">
          <t-button
            :disabled="!(currentQuestionIndex > 0) || submitting"
            @click="prevQuestion"
            size="large"
            variant="outline"
          >
            {{ t("questionActions.previous") }}
          </t-button>

          <t-button
            :disabled="
              !(currentQuestionIndex < questionList.length - 1) || submitting
            "
            @click="nextQuestion"
            size="large"
            variant="outline"
          >
            {{ t("questionActions.next") }}
          </t-button>

          <t-button
            theme="primary"
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
          </t-button>
        </div>
      </div>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {useMessage} from "../utils/message";
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {AxiosError} from "axios";
import DOMPurify from "dompurify";
import {contentAPI} from "../api/config";
import AnswerCard from "../components/common/AnswerCard.vue";

import QuestionAnswer from "../components/common/QuestionAnswer.vue";

import PageContainer from "../components/common/PageContainer.vue";

import type {AnswerValue, AnswerVerificationResponse, Question,} from "@/types/common";

interface RouteParams {
  bankId: string;
  type: string;
}

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

const questionList = ref<Question[]>([]);
const loading = ref(false);
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const message = useMessage();
const currentQuestionIndex = ref(0);
const isSubmitted = ref(false);
const submitting = ref(false);

const localAnswer = ref<AnswerValue>(null);

const currentQuestion = computed(
  () => questionList.value[currentQuestionIndex.value]
);

const showAnswer = computed(() => {
  return !!currentQuestion.value && currentQuestion.value.isCompleted === 1;
});

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

const sanitizeHtml = (html: string): string => {
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ["p", "br", "strong", "em", "u", "span", "div"],
    ALLOWED_ATTR: ["class", "style"],
    FORBID_TAGS: ["script", "object", "embed", "form", "input"],
  });
};

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

    if (
      !contentAPI ||
      typeof (contentAPI as ContentAPI).getQuestions !== "function"
    ) {
      message.error(t("message.internalError"));
      return;
    }

    let response;
    if (type === "question-type") {
      // 题型练习需要额外传递题型参数
      const questionType = route.query.questionType as string;
      if (!questionType) {
        message.error("题型参数错误，请重新进入");
        return;
      }

      // 调用新的API接口获取指定题型的题目
      response = await contentAPI.getQuestions(bankId, `type:${questionType}`);
    } else {
      response = await contentAPI.getQuestions(bankId, type);
    }

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
        const bankName =
          (route.query.bankName as string) ||
          `${t("content.bankId")}: ${bankId}`;

        let practiceType;
        if (type === "question-type") {
          const questionType = route.query.questionType as string;
          practiceType = getQuestionTypeDisplayName(questionType);
        } else {
          practiceType =
            type === "wrong"
              ? t("content.wrongQuestions")
              : t("content.allPractice");
        }

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

// 获取题型显示名称
function getQuestionTypeDisplayName(type: string): string {
  const typeMap: Record<string, string> = {
    single: t("statistics.questionTypes.single"),
    fill_blank: t("statistics.questionTypes.fillBlank"),
    true_false: t("statistics.questionTypes.trueFalse"),
    multiple: t("statistics.questionTypes.multiple"),
    short_answer: t("statistics.questionTypes.shortAnswer"),
  };
  return typeMap[type] || type;
}

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

    const response = await contentAPI.verifyAnswer(
      currentQuestion.value.id,
      answerToSave
    );
    const result: AnswerVerificationResponse = response.data;

    const qIndex = currentQuestionIndex.value;
    const q = questionList.value[qIndex];
    q.userAnswer = result.userAnswer;
    q.correctAnswer = result.correctAnswer;
    q.analysis = result.analysis;
    q.isCompleted = 1;
    q.isCorrect = result.isCorrect ? 1 : 0;

    if (result.isCorrect) {
      message.success(result.message || t("content.answerCorrect"));
    } else {
      message.error(result.message || t("content.answerWrong"));
    }

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

onMounted(() => {
  fetchQuestions();
});

watch(currentQuestion, () => {
  initLocalAnswer();
});
</script>

<style scoped>
.content-page {
  padding: var(--spacing-sm);
  min-height: 100vh;
}

.loading-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content-layout {
  display: flex;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-4);
  min-height: calc(100vh - 200px);
}

.main-content {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 380px);
}

.sidebar {
  width: 360px;
  flex-shrink: 0;
}

.question-card {
  margin-bottom: var(--spacing-4);
}

.question-stem {
  font-size: var(--font-size-base);
  line-height: 1.6;
  margin-bottom: var(--spacing-3);
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.question-divider {
  margin: var(--spacing-3) 0;
}

.answer-card {
  margin-top: var(--spacing-4);
}

.answer-card .mb-4 {
  margin-bottom: var(--spacing-3);
}

.bottom-actions {
  position: sticky;
  bottom: 0;
  background: var(--color-bg-base);
  padding: var(--spacing-3) 0;
  z-index: 10;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: var(--spacing-3);
  flex-wrap: wrap;
}

@media (max-width: 1200px) {
  .content-layout {
    flex-direction: column;
  }

  .main-content {
    max-width: 100%;
  }

  .sidebar {
    width: 100%;
    order: -1;
  }
}

@media (max-width: 768px) {
  .content-page {
    padding: var(--spacing-xs);
  }

  .content-layout {
    gap: var(--spacing-3);
    margin-bottom: var(--spacing-3);
  }

  .action-buttons {
    gap: var(--spacing-2);
    padding: 0 var(--spacing-2);
  }

  .action-buttons .t-button {
    flex: 1;
    min-width: 80px;
  }
}

@media (max-width: 480px) {
  .question-stem {
    font-size: var(--font-size-sm);
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .t-button {
    width: 100%;
  }
}
</style>
