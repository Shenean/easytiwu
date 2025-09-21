<template>
  <PageContainer :show-card="false" container-class="content-page">

    <div v-if="loading" class="loading-container" style="
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;
      ">
      <n-spin size="large">
        <template #description>
          {{ t("content.loadingQuestions") }}
        </template>
      </n-spin>
    </div>


    <n-empty v-else-if="questionList.length === 0" :description="t('content.noQuestions')" size="large">
      <template #extra>
        <n-button @click="router.go(-1)">
          {{ t("content.goBack") }}
        </n-button>
      </template>
    </n-empty>


    <template v-else>

      <div class="content-layout">

        <div class="main-content">

          <n-card :title="t('content.questionNumber', { number: currentQuestionIndex + 1 })
            " size="small" class="question-card">

            <div class="question-stem" v-html="sanitizeHtml(currentQuestion?.content || '')"></div>


            <n-divider class="question-divider" />


            <QuestionAnswer :question="currentQuestion!" v-model="localAnswer" />
          </n-card>


          <n-card v-if="showAnswer" :title="t('content.answerAndAnalysis')" size="small" class="answer-card">
            <n-alert type="success" :title="t('content.correctAnswer')" :show-icon="false" class="mb-4">
              {{ displayCorrectAnswer }}
            </n-alert>
            <n-alert v-if="currentQuestion?.analysis" type="info" :title="t('content.analysis')" :show-icon="false">
              {{ currentQuestion.analysis }}
            </n-alert>
          </n-card>
        </div>


        <div class="sidebar">
          <AnswerCard :questions="questionList" :current-question-id="currentQuestion?.id"
            @question-click="jumpToQuestion" />
        </div>
      </div>


      <div class="bottom-actions">
        <div class="action-buttons">
          <n-button :disabled="!(currentQuestionIndex > 0) || submitting" @click="prevQuestion" size="large"
            type="default">
            {{ t("questionActions.previous") }}
          </n-button>

          <n-button :disabled="!(currentQuestionIndex < questionList.length - 1) || submitting
            " @click="nextQuestion" size="large" type="default">
            {{ t("questionActions.next") }}
          </n-button>

          <n-button type="primary" size="large" @click="submitAnswer" :disabled="!canSubmit || submitting"
            :loading="submitting">
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
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {AxiosError} from "axios";
import DOMPurify from "dompurify";
import {contentAPI} from "../api/config";
import AnswerCard from "../components/common/AnswerCard.vue";

import QuestionAnswer from "../components/common/QuestionAnswer.vue";

import PageContainer from "../components/common/PageContainer.vue";


import type {AnswerValue, AnswerVerificationResponse, Question} from '@/types/common';


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
const message = useMessage();
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
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
    ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'span', 'div'],
    ALLOWED_ATTR: ['class', 'style'],
    FORBID_TAGS: ['script', 'object', 'embed', 'form', 'input']
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
        `${t("content.fetchQuestionsFailed")}: ${error.response?.data?.message || error.message
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
      message.success(result.message || t("content.answerCorrect"), {
        duration: 3000,
      });
    } else {
      message.error(result.message || t("content.answerWrong"), {
        duration: 3000,
      });
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

.content-layout {
  display: flex;
  gap: 24px;
  padding: 0 16px;
  align-items: flex-start;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex: 1;
}

.sidebar {
  position: sticky;
  top: 80px;
  width: 300px;
  height: calc(100vh - 80px);
  overflow-y: auto;
  flex-shrink: 0;
}


.question-card,
.answer-card {
  margin-bottom: 0;
  width: 100%;
  box-sizing: border-box;

}


.answer-card {
  margin-top: 0;

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


.question-divider {
  margin: var(--spacing-5) 0;
  border-color: var(--color-border-secondary);
}


.content-layout {
  padding-bottom: var(--spacing-20);
}


.mb-4 {
  margin-bottom: var(--spacing-md);
}


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
  padding: var(--card-padding-desktop) var(--card-padding-desktop) var(--spacing-4);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: 600;
}

:deep(.n-card .n-card-content) {
  padding: var(--card-padding-desktop);
  word-wrap: break-word;
  overflow-wrap: break-word;
}




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
</style>
