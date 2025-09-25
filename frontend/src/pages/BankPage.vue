<template>
  <PageContainer
    :title="t('bank.title')"
    :show-card="false"
    container-class="bank-container"
  >
    <!-- 顶部操作栏 -->
    <div v-if="!loading" class="top-actions">
      <t-space size="small">
        <t-button theme="primary" size="small" @click="showUploadModal = true">
          {{ t("bank.upload") }}
        </t-button>
        <t-button
          v-if="banks.length > 1"
          theme="primary"
          size="small"
          @click="showMergeModal = true"
          :disabled="banks.length < 2"
        >
          {{ t("bank.merge") }}
        </t-button>
      </t-space>
    </div>

    <!-- 空状态占位 -->
    <t-empty
      v-if="!loading && banks.length === 0"
      :description="t('bank.noData')"
      size="large"
    >
      <template #action>
        <t-button theme="primary" size="small" @click="fetchBanks">
          {{ t("bank.refresh") }}
        </t-button>
      </template>
    </t-empty>

    <!-- 题库表格 -->
    <div v-else class="table-container">
      <t-table
        :columns="tableColumns"
        :data="banks"
        :loading="loading"
        :pagination="{
          pageSize: 12,
          showJumper: false,
          total: banks.length,
          showTotal: true,
          showPageSize: false,
        }"
        :row-key="'id'"
        bordered
        size="small"
        class="bank-table"
      >
        <template #actions="{ row }">
          <t-space size="small">
            <t-button
              theme="primary"
              size="small"
              @click="handlePractice(row.id)"
            >
              {{ t("bank.startPractice") }}
            </t-button>
            <t-dropdown
              :options="[
                { content: t('bank.questionTypePractice'), value: 'type', onClick: () => handleQuestionTypePractice(row.id) },
                { content: t('bank.errorCollection'), value: 'error', onClick: () => handleWrongSet(row.id) },
                { content: t('bank.delete'), value: 'delete', theme: 'error', onClick: () => confirmDelete(row.id) }
              ]"
            >
              <t-button theme="default" size="small" variant="outline">
                {{ t("common.more") }}
                <template #suffix>
                  <t-icon name="chevron-down" size="14px" />
                </template>
              </t-button>
            </t-dropdown>
          </t-space>
        </template>
      </t-table>
    </div>

    <!-- 上传题库弹窗 -->
    <t-dialog
      v-model:visible="showUploadModal"
      :header="t('bank.upload')"
      :confirm-btn="t('common.submit')"
      :cancel-btn="t('message.cancel')"
      @confirm="handleUploadSubmit"
      @cancel="resetUploadForm"
      width="600px"
    >
      <div class="upload-form">
        <t-form
          ref="uploadFormRef"
          :data="uploadForm"
          :rules="uploadRules"
          label-align="left"
          label-width="80px"
          size="medium"
        >
          <t-form-item :label="t('bank.name')" name="name">
            <t-input
              v-model="uploadForm.name"
              :placeholder="t('bank.newBankNamePlaceholder')"
              :maxlength="15"
              show-word-limit
              clearable
            />
          </t-form-item>

          <t-form-item :label="t('bank.description')" name="description">
            <t-textarea
              v-model="uploadForm.description"
              :placeholder="t('bank.descriptionPlaceholder')"
              :maxlength="30"
              show-word-limit
              clearable
              :autosize="{ minRows: 2, maxRows: 4 }"
            />
          </t-form-item>

          <t-form-item :label="t('bank.file')" name="file">
            <t-upload
              v-model="uploadForm.file"
              :accept="'.docx,.pdf,.txt'"
              :max="1"
              :multiple="false"
              theme="file-flow"
              drag
            >
              <template #drag-content>
                <div style="margin-bottom: 12px">
                  <t-icon name="folder" size="48px" />
                </div>
                <div style="font-size: 14px">
                  {{ t("bank.uploadText") }}
                </div>
                <div style="margin: 8px 0 0 0; color: #999">
                  {{ t("bank.uploadHint") }}
                </div>
              </template>
            </t-upload>
          </t-form-item>
        </t-form>
      </div>
    </t-dialog>

    <!-- 合并题库弹窗 -->
    <t-dialog
      v-model:visible="showMergeModal"
      :header="t('bank.mergeTitle')"
      :confirm-btn="t('bank.confirmMerge')"
      :cancel-btn="t('message.cancel')"
      @confirm="handleMergeSubmit"
      @cancel="resetMergeForm"
      width="500px"
    >
      <div class="merge-form">
        <!-- 题库选择 -->
        <div class="form-section">
          <div class="form-label">{{ t("bank.selectBanks") }}</div>
          <div class="form-hint">{{ t("bank.selectTwoBanksHint") }}</div>
          <div class="bank-list">
            <div
              v-for="bank in banks"
              :key="bank.id"
              class="bank-option"
              :class="{ selected: selectedBanks.includes(bank.id) }"
              @click="toggleBankSelection(bank.id)"
            >
              <t-checkbox
                :checked="selectedBanks.includes(bank.id)"
                @change="() => toggleBankSelection(bank.id)"
                :disabled="
                  !selectedBanks.includes(bank.id) && selectedBanks.length >= 2
                "
              />
              <div class="bank-info">
                <div class="bank-option-name">{{ bank.name }}</div>
                <div class="bank-option-id">ID: {{ bank.id }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 新题库名称 -->
        <div class="form-section">
          <t-form-item
            :label="t('bank.newBankName')"
            :status="nameError ? 'error' : undefined"
            :help="nameError"
          >
            <t-input
              v-model="mergeForm.name"
              :placeholder="t('bank.newBankNamePlaceholder')"
              @blur="validateName"
              @input="nameError = ''"
            />
          </t-form-item>
        </div>

        <!-- 描述信息 -->
        <div class="form-section">
          <t-form-item :label="t('bank.description')">
            <t-textarea
              v-model="mergeForm.description"
              :placeholder="t('bank.descriptionPlaceholder')"
              :autosize="{ minRows: 3, maxRows: 5 }"
            />
          </t-form-item>
        </div>
      </div>
    </t-dialog>

    <!-- 题型练习选择弹窗 -->
    <t-dialog
      v-model:visible="showQuestionTypeModal"
      :header="t('bank.questionTypePractice')"
      :confirm-btn="t('common.confirm')"
      :cancel-btn="t('message.cancel')"
      @confirm="handleQuestionTypeSubmit"
      @cancel="resetQuestionTypeForm"
      width="500px"
    >
      <div class="question-type-form">
        <t-form ref="questionTypeFormRef" :data="questionTypeForm">
          <t-form-item :label="t('bank.selectQuestionType')">
            <t-select
              v-model="questionTypeForm.questionType"
              :options="questionTypeOptions"
            />
          </t-form-item>
        </t-form>
      </div>
    </t-dialog>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import type {FormInstanceFunctions, UploadFile} from "tdesign-vue-next";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {useMessage} from "../utils/message";
import axios, {AxiosError} from "axios";

import PageContainer from "../components/common/PageContainer.vue";
import type {ApiResponse, QuestionBank} from "@/types/common";
import {bankFormRules} from "../validation/rulesBank";
import {uploadAPI} from "../api/config";

// ================== 状态管理 ==================
const message = useMessage();
const router = useRouter();
const { t } = useI18n();
const banks = ref<QuestionBank[]>([]);
const loading = ref(false);

// ================== 上传功能状态 ==================
interface UploadForm {
  name: string;
  description: string;
  file: UploadFile[];
}

const showUploadModal = ref(false);
const uploadLoading = ref(false);
const uploadFormRef = ref<FormInstanceFunctions | null>(null);
const uploadForm = ref<UploadForm>({
  name: "",
  description: "",
  file: [],
});
const uploadRules = bankFormRules;

// ================== 表格配置 ==================
const tableColumns = computed(() => [
  {
    title: t("bank.id"),
    colKey: "id",
    width: 80,
    align: "center",
  },
  {
    title: t("bank.name"),
    colKey: "name",
    minWidth: 200,
    ellipsis: true,
  },
  {
    title: t("bank.description"),
    colKey: "description",
    minWidth: 250,
    ellipsis: true,
    cell: ({ row }: { row: any }) => {
      return row?.description || "-";
    },
  },
  {
    title: t("bank.totalQuestions"),
    colKey: "totalCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.completedQuestions"),
    colKey: "completedCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.errorQuestions"),
    colKey: "wrongCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.actions"),
    colKey: "actions",
    width: 350,
    align: "center",
  },
]);

// ================== 合并功能状态 ==================
const showMergeModal = ref(false);
const mergeLoading = ref(false);
const selectedBanks = ref<number[]>([]);
const mergeForm = ref({
  name: "",
  description: "",
});
const nameError = ref("");

// ================== 题型练习状态 ==================
const showQuestionTypeModal = ref(false);
const currentBankId = ref<number | null>(null);
const questionTypeFormRef = ref<FormInstanceFunctions | null>(null);
const questionTypeForm = ref({
  questionType: "",
});

// 题型选项
const questionTypeOptions = computed(() => [
  { label: t("statistics.questionTypes.single"), value: "single" },
  { label: t("statistics.questionTypes.multiple"), value: "multiple" },
  { label: t("statistics.questionTypes.fillBlank"), value: "fill_blank" },
  { label: t("statistics.questionTypes.trueFalse"), value: "true_false" },
  { label: t("statistics.questionTypes.shortAnswer"), value: "short_answer" },
]);

// ================== 数据获取 ==================
/**
 * 获取题库数据
 */
async function fetchBanks() {
  loading.value = true;
  try {
    const res = await axios.get<ApiResponse<QuestionBank[]>>("/api/bank");

    // 检查响应格式并正确处理数据
    if (res.data.code === 200) {
      banks.value = res.data.data || [];
    } else {
      message.error(res.data.message || t("message.fetchBanksFailed"));
    }
  } catch (err) {
    if (err instanceof AxiosError) {
      message.error(
        `${t("message.requestFailed")}：${
          err.response?.data?.message || t("message.unknownError")
        }`
      );
    } else {
      message.error(
        `${t("message.fetchBanksFailed")}，${t("message.retryLater")}`
      );
    }
  } finally {
    loading.value = false;
  }
}

// ================== 操作处理 ==================
function handlePractice(id: number) {
  try {
    const targetBank = banks.value.find((b) => b.id === id);
    const bankName = targetBank?.name || `ID: ${id}`;
    router.push({
      name: "content",
      params: { bankId: id.toString(), type: "all" },
      query: { bankName: bankName },
    });
    // 移除这里的成功提示，将在ContentPage中统一显示
  } catch (error) {
    message.error(t("message.routeJumpFailed"));
  }
}

function handleWrongSet(id: number) {
  try {
    const targetBank = banks.value.find((b) => b.id === id);
    const bankName = targetBank?.name || `ID: ${id}`;
    router.push({
      name: "content",
      params: { bankId: id.toString(), type: "wrong" },
      query: { bankName: bankName },
    });
    // 移除这里的成功提示，将在ContentPage中统一显示
  } catch (error) {
    message.error(t("message.routeJumpFailed"));
  }
}

async function confirmDelete(id: number) {
  const targetBank = banks.value.find((b) => b.id === id);
  const bankName = targetBank?.name || `ID: ${id}`;

  try {
    await message.confirm({
      header: t("message.confirmDelete"),
      body: t("message.deleteConfirmContent", { name: bankName }),
    });

    await handleDelete(id);
  } catch {
    // 用户取消删除
    message.info(t("message.cancelDelete"));
  }
}

async function handleDelete(id: number) {
  try {
    const res = await axios.delete<ApiResponse<void>>(`/api/bank/${id}`);

    // 检查响应格式
    if (res.data.code === 200) {
      message.success(t("message.deleteSuccess"));
      // 优化：前端移除，避免重新拉取全部数据
      banks.value = banks.value.filter((b) => b.id !== id);
    } else {
      message.error(res.data.message || t("message.deleteFailed"));
    }
  } catch (err) {
    if (err instanceof AxiosError) {
      message.error(
        `${t("message.deleteFailed")}：${
          err.response?.data?.message || t("message.unknownError")
        }`
      );
    } else {
      message.error(`${t("message.deleteFailed")}，${t("message.retryLater")}`);
    }
  }
}

// ================== 合并功能处理 ==================
/**
 * 切换题库选择状态
 */
function toggleBankSelection(bankId: number) {
  const index = selectedBanks.value.indexOf(bankId);
  if (index > -1) {
    selectedBanks.value.splice(index, 1);
  } else if (selectedBanks.value.length < 2) {
    selectedBanks.value.push(bankId);
  }
}

/**
 * 验证题库名称
 */
function validateName() {
  if (!mergeForm.value.name.trim()) {
    nameError.value = t("bank.nameRequired");
    return false;
  }
  if (mergeForm.value.name.trim().length < 2) {
    nameError.value = t("bank.nameTooShort");
    return false;
  }
  nameError.value = "";
  return true;
}

/**
 * 验证表单
 */
function validateMergeForm() {
  let isValid = true;

  // 验证题库选择 - 必须选择2个
  if (selectedBanks.value.length !== 2) {
    message.error(t("bank.selectTwoBanksError"));
    isValid = false;
  }

  // 验证名称
  if (!validateName()) {
    isValid = false;
  }

  return isValid;
}

/**
 * 重置合并表单
 */
function resetMergeForm() {
  selectedBanks.value = [];
  mergeForm.value = {
    name: "",
    description: "",
  };
  nameError.value = "";
}

/**
 * 处理合并提交
 */
async function handleMergeSubmit() {
  if (!validateMergeForm()) {
    return false;
  }

  mergeLoading.value = true;
  try {
    const requestData = {
      bankId1: selectedBanks.value[0],
      bankId2: selectedBanks.value[1],
      name: mergeForm.value.name.trim(),
      description: mergeForm.value.description.trim(),
    };

    const res = await axios.post<ApiResponse<QuestionBank>>(
      "/api/bank/merge",
      requestData
    );

    if (res.data.code === 200) {
      message.success(t("bank.mergeSuccess"));
      showMergeModal.value = false;
      resetMergeForm();
      // 刷新题库列表
      await fetchBanks();
      return true;
    } else {
      message.error(res.data.message || t("bank.mergeFailed"));
      return false;
    }
  } catch (err) {
    if (err instanceof AxiosError) {
      message.error(
        `${t("bank.mergeFailed")}：${
          err.response?.data?.message || t("message.unknownError")
        }`
      );
    } else {
      message.error(`${t("bank.mergeFailed")}，${t("message.retryLater")}`);
    }
    return false;
  } finally {
    mergeLoading.value = false;
  }
}

// ================== 上传功能处理 ==================
/**
 * 重置上传表单
 */
function resetUploadForm() {
  uploadForm.value = {
    name: "",
    description: "",
    file: [],
  };
}

/**
 * 处理上传提交
 */
async function handleUploadSubmit() {
  if (!uploadFormRef.value) {
    return false;
  }

  return new Promise<boolean>((resolve) => {
    uploadFormRef.value!.validate().then(async (errors) => {
      if (errors !== true) {
        const firstError =
          Object.values(errors)
            .flat()
            .find(
              (err: any) => err && typeof err === "object" && "message" in err
            )?.message || "请检查表单输入";
        message.error(firstError);
        resolve(false);
        return;
      }

      uploadLoading.value = true;
      try {
        if (!uploadForm.value.file[0]?.file) {
          message.error("请选择有效的文件");
          resolve(false);
          return;
        }

        const formData = new FormData();
        formData.append("name", uploadForm.value.name);
        formData.append("description", uploadForm.value.description);
        formData.append("file", uploadForm.value.file[0].file as Blob);

        await uploadAPI.uploadFile(formData);

        message.success("上传成功 🎉");
        showUploadModal.value = false;
        resetUploadForm();
        // 刷新题库列表
        await fetchBanks();
        resolve(true);
      } catch (err: unknown) {
        if (err && typeof err === "object" && "response" in err) {
          const axiosErr = err as { response: { data?: string } };
          message.error(axiosErr.response?.data || "上传失败，请重试");
        } else {
          message.error("上传失败，请重试");
        }
        resolve(false);
      } finally {
        uploadLoading.value = false;
      }
    });
  });
}

// ================== 题型练习功能处理 ==================
/**
 * 处理题型练习
 */
function handleQuestionTypePractice(bankId: number) {
  currentBankId.value = bankId;
  showQuestionTypeModal.value = true;
}

/**
 * 重置题型练习表单
 */
function resetQuestionTypeForm() {
  questionTypeForm.value = {
    questionType: "",
  };
  currentBankId.value = null;
}

/**
 * 处理题型练习提交
 */
async function handleQuestionTypeSubmit() {
  if (!questionTypeForm.value.questionType) {
    message.error(t("bank.selectQuestionTypeError"));
    return false;
  }

  if (!currentBankId.value) {
    message.error(t("message.unknownError"));
    return false;
  }

  try {
    const targetBank = banks.value.find((b) => b.id === currentBankId.value);
    const bankName = targetBank?.name || `ID: ${currentBankId.value}`;

    router.push({
      name: "content",
      params: {
        bankId: currentBankId.value.toString(),
        type: "question-type",
      },
      query: {
        bankName: bankName,
        questionType: questionTypeForm.value.questionType,
      },
    });

    showQuestionTypeModal.value = false;
    resetQuestionTypeForm();
    return true;
  } catch (error) {
    message.error(t("message.routeJumpFailed"));
    return false;
  }
}

// ================== 生命周期 ==================
onMounted(() => {
  fetchBanks();
});

// ================== 暴露方法（供父组件调用） ==================
defineExpose({
  refresh: fetchBanks,
  getBanks: () => [...banks.value],
});
</script>

<style scoped>
.bank-container {
  padding: var(--spacing-xs);
}

.top-actions {
  margin-bottom: var(--spacing-3);
}

.table-container {
  overflow-x: auto;
}

.bank-table {
  width: 100%;
  min-width: 800px;
}

.merge-form,
.upload-form,
.question-type-form {
  padding: var(--spacing-2) 0;
}

.form-section {
  margin-bottom: var(--spacing-3);
}

.form-section:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  margin-bottom: var(--spacing-2);
  font-weight: 500;
}

.form-hint {
  display: block;
  margin-bottom: var(--spacing-3);
  font-size: var(--font-size-sm);
}

.bank-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: var(--spacing-2);
}

.bank-option {
  display: flex;
  align-items: center;
  padding: var(--spacing-2);
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: var(--spacing-1);
  transition: background-color 0.2s;
}

.bank-option:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.bank-option.selected {
  background-color: #18a058;
  color: white;
}

.bank-option.selected :deep(.t-checkbox__input) {
  border-color: white;
  background-color: #18a058;
}

.bank-info {
  margin-left: var(--spacing-2);
}

.bank-option-name {
  font-weight: 500;
}

.bank-option-id {
  font-size: var(--font-size-xs);
  opacity: 0.8;
}

@media (max-width: 768px) {
  .bank-container {
    padding: var(--spacing-xs);
  }

  .top-actions {
    margin-bottom: var(--spacing-2);
  }

  .bank-table {
    min-width: 600px;
  }
}
</style>
