<template>
  <PageContainer :title="t('bank.title')" :show-card="false" container-class="bank-container">
    <!-- 顶部操作栏 -->
    <div v-if="!loading" class="top-actions">
      <n-space size="medium">
        <n-button type="primary" size="medium" @click="showUploadModal = true">
          {{ t("bank.upload") }}
        </n-button>
        <n-button v-if="banks.length > 1" type="primary" size="medium" @click="showMergeModal = true"
          :disabled="banks.length < 2">
          {{ t("bank.merge") }}
        </n-button>
      </n-space>
    </div>

    <!-- 空状态占位 -->
    <n-empty v-if="!loading && banks.length === 0" :description="t('bank.noData')" size="large">
      <template #extra>
        <n-button type="primary" size="small" @click="fetchBanks">
          {{ t("bank.refresh") }}
        </n-button>
      </template>
    </n-empty>

    <!-- 题库表格 -->
    <div v-else class="table-container">
      <n-data-table :columns="tableColumns" :data="banks" :loading="loading" :pagination="false" :bordered="true"
        :single-line="false" size="medium" class="bank-table" />
    </div>

    <!-- 上传题库弹窗 -->
    <n-modal v-model:show="showUploadModal" preset="dialog" :title="t('bank.upload')"
      :positive-text="t('common.submit')" :negative-text="t('message.cancel')" @positive-click="handleUploadSubmit"
      @negative-click="resetUploadForm" :loading="uploadLoading" style="width: 90%; max-width: 600px">
      <div class="upload-form">
        <n-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-placement="left" label-width="80"
          size="medium">
          <n-form-item :label="t('bank.name')" path="name">
            <n-input v-model:value="uploadForm.name" :placeholder="t('bank.newBankNamePlaceholder')" maxlength="15"
              show-count clearable :aria-label="t('bank.name')" />
          </n-form-item>

          <n-form-item :label="t('bank.description')" path="description">
            <n-input v-model:value="uploadForm.description" :placeholder="t('bank.descriptionPlaceholder')"
              type="textarea" maxlength="30" show-count clearable autosize :aria-label="t('bank.description')" />
          </n-form-item>

          <n-form-item :label="t('bank.file')" path="file">
            <n-upload v-model:file-list="uploadForm.file" :accept="'.docx,.pdf,.txt'" :max="1" :multiple="false"
              action="#" :custom-request="handleUploadCustomRequest" @before-upload="handleUploadBeforeUpload">
              <n-upload-dragger>
                <div style="margin-bottom: var(--spacing-3)">
                  <n-icon size="48" :depth="3">
                    <ArchiveIcon />
                  </n-icon>
                </div>
                <n-text style="font-size: var(--font-size-base)">
                  {{ t("bank.uploadText") }}
                </n-text>
                <n-p depth="3" style="margin: var(--spacing-2) 0 0 0">
                  {{ t("bank.uploadHint") }}
                </n-p>
              </n-upload-dragger>
            </n-upload>
          </n-form-item>
        </n-form>
      </div>
    </n-modal>

    <!-- 合并题库弹窗 -->
    <n-modal v-model:show="showMergeModal" preset="dialog" :title="t('bank.mergeTitle')"
      :positive-text="t('bank.confirmMerge')" :negative-text="t('message.cancel')" @positive-click="handleMergeSubmit"
      @negative-click="resetMergeForm" :loading="mergeLoading" style="width: 90%; max-width: 500px">
      <div class="merge-form">
        <!-- 题库选择 -->
        <div class="form-section">
          <n-text class="form-label">{{ t("bank.selectBanks") }}</n-text>
          <n-text depth="3" class="form-hint">{{
            t("bank.selectTwoBanksHint")
          }}</n-text>
          <div class="bank-list">
            <div v-for="bank in banks" :key="bank.id" class="bank-option"
              :class="{ selected: selectedBanks.includes(bank.id) }" @click="toggleBankSelection(bank.id)">
              <n-checkbox :checked="selectedBanks.includes(bank.id)" :disabled="!selectedBanks.includes(bank.id) && selectedBanks.length >= 2
                " @update:checked="(checked) => handleBankCheck(bank.id, checked)" />
              <div class="bank-info">
                <div class="bank-option-name">{{ bank.name }}</div>
                <div class="bank-option-id">ID: {{ bank.id }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 新题库名称 -->
        <div class="form-section">
          <n-form-item :label="t('bank.newBankName')" :validation-status="nameError ? 'error' : undefined"
            :feedback="nameError">
            <n-input v-model:value="mergeForm.name" :placeholder="t('bank.newBankNamePlaceholder')" @blur="validateName"
              @input="nameError = ''" />
          </n-form-item>
        </div>

        <!-- 描述信息 -->
        <div class="form-section">
          <n-form-item :label="t('bank.description')">
            <n-input v-model:value="mergeForm.description" type="textarea"
              :placeholder="t('bank.descriptionPlaceholder')" :autosize="{ minRows: 3, maxRows: 5 }" />
          </n-form-item>
        </div>
      </div>
    </n-modal>

    <!-- 题型练习选择弹窗 -->
    <n-modal v-model:show="showQuestionTypeModal" preset="dialog" :title="t('bank.questionTypePractice')"
      :positive-text="t('common.confirm')" :negative-text="t('message.cancel')"
      @positive-click="handleQuestionTypeSubmit" @negative-click="resetQuestionTypeForm"
      style="width: 90%; max-width: 500px">
      <div class="question-type-form">
        <n-form ref="questionTypeFormRef" :model="questionTypeForm">
          <n-form-item :label="t('bank.selectQuestionType')">
            <n-select v-model:value="questionTypeForm.questionType" :options="questionTypeOptions" />
          </n-form-item>
        </n-form>
      </div>
    </n-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, h, onMounted, ref} from "vue";
import type {DataTableColumns, FormInst, UploadCustomRequestOptions, UploadFileInfo} from "naive-ui";
import {
  NButton,
  NCheckbox,
  NDataTable,
  NEmpty,
  NForm,
  NFormItem,
  NIcon,
  NInput,
  NModal,
  NP,
  NSelect,
  NSpace,
  NText,
  NUpload,
  NUploadDragger,
  useDialog,
  useMessage,
} from "naive-ui";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import axios, {AxiosError} from "axios";
import {ArchiveOutline as ArchiveIcon} from "@vicons/ionicons5";

import PageContainer from "../components/common/PageContainer.vue";
import type {ApiResponse, QuestionBank} from "@/types/common";
import {bankFormRules} from "../validation/rulesBank";
import {uploadAPI} from "../api/config";

// ================== 状态管理 ==================
const message = useMessage();
const dialog = useDialog();
const router = useRouter();
const { t } = useI18n();
const banks = ref<QuestionBank[]>([]);
const loading = ref(false);

// ================== 上传功能状态 ==================
interface UploadForm {
  name: string;
  description: string;
  file: UploadFileInfo[];
}

const showUploadModal = ref(false);
const uploadLoading = ref(false);
const uploadFormRef = ref<FormInst | null>(null);
const uploadForm = ref<UploadForm>({
  name: "",
  description: "",
  file: [],
});
const uploadRules = bankFormRules;

// ================== 表格配置 ==================
const tableColumns = computed<DataTableColumns<QuestionBank>>(() => [
  {
    title: t("bank.id"),
    key: "id",
    width: 80,
    align: "center",
  },
  {
    title: t("bank.name"),
    key: "name",
    minWidth: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: t("bank.description"),
    key: "description",
    minWidth: 250,
    ellipsis: {
      tooltip: true
    },
    render(row) {
      return row.description || "-";
    }
  },
  {
    title: t("bank.totalQuestions"),
    key: "totalCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.completedQuestions"),
    key: "completedCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.errorQuestions"),
    key: "wrongCount",
    width: 120,
    align: "center",
  },
  {
    title: t("bank.actions"),
    key: "actions",
    width: 350,
    align: "center",
    render(row) {
      return h(NSpace, { size: "small" }, {
        default: () => [
          h(NButton, {
            type: "primary",
            size: "small",
            onClick: () => handlePractice(row.id)
          }, { default: () => t("bank.startPractice") }),
          h(NButton, {
            type: "default",
            size: "small",
            onClick: () => handleQuestionTypePractice(row.id)
          }, { default: () => t("bank.questionTypePractice") }),
          h(NButton, {
            type: "default",
            size: "small",
            onClick: () => handleWrongSet(row.id)
          }, { default: () => t("bank.errorCollection") }),
          h(NButton, {
            type: "error",
            size: "small",
            ghost: true,
            onClick: () => confirmDelete(row.id)
          }, { default: () => t("bank.delete") })
        ]
      });
    }
  }
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
const questionTypeFormRef = ref<FormInst | null>(null);
const questionTypeForm = ref({
  questionType: ""
});

// 题型选项
const questionTypeOptions = computed(() => [
  { label: t('statistics.questionTypes.single'), value: 'single' },
  { label: t('statistics.questionTypes.multiple'), value: 'multiple' },
  { label: t('statistics.questionTypes.fillBlank'), value: 'fill_blank' },
  { label: t('statistics.questionTypes.trueFalse'), value: 'true_false' },
  { label: t('statistics.questionTypes.shortAnswer'), value: 'short_answer' }
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
        `${t("message.requestFailed")}：${err.response?.data?.message || t("message.unknownError")
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

function confirmDelete(id: number) {
  const targetBank = banks.value.find((b) => b.id === id);
  const bankName = targetBank?.name || `ID: ${id}`;

  dialog.warning({
    title: t("message.confirmDelete"),
    content: t("message.deleteConfirmContent", { name: bankName }),
    positiveText: t("bank.delete"),
    negativeText: t("message.cancel"),
    onPositiveClick: async () => {
      await handleDelete(id);
    },
    onNegativeClick: () => {
      message.info(t("message.cancelDelete"));
    },
  });
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
        `${t("message.deleteFailed")}：${err.response?.data?.message || t("message.unknownError")
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
 * 处理复选框状态变化
 */
function handleBankCheck(bankId: number, checked: boolean) {
  if (
    checked &&
    !selectedBanks.value.includes(bankId) &&
    selectedBanks.value.length < 2
  ) {
    selectedBanks.value.push(bankId);
  } else if (!checked) {
    const index = selectedBanks.value.indexOf(bankId);
    if (index > -1) {
      selectedBanks.value.splice(index, 1);
    }
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
        `${t("bank.mergeFailed")}：${err.response?.data?.message || t("message.unknownError")
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
 * 上传前校验文件
 */
function handleUploadBeforeUpload(data: {
  file: UploadFileInfo;
  fileList: UploadFileInfo[];
}) {
  const file = data.file;
  const fileName = file.file?.name || file.name || "未知文件";

  // 文件类型校验
  const allowedTypes = [".docx", ".pdf", ".txt"];
  const fileExtension = fileName
    .toLowerCase()
    .substring(fileName.lastIndexOf("."));
  if (!allowedTypes.includes(fileExtension)) {
    message.error(
      `不支持的文件格式，请选择 ${allowedTypes.join("、")} 格式的文件`
    );
    return false;
  }

  // 文件大小校验（20MB）
  const maxSize = 20 * 1024 * 1024;
  if (file.file && file.file.size > maxSize) {
    message.error("文件大小不能超过 20MB");
    return false;
  }

  return true;
}

/**
 * 自定义上传请求（阻止默认上传行为）
 */
function handleUploadCustomRequest(options: UploadCustomRequestOptions) {
  // 阻止默认上传，文件将在表单提交时统一处理
  options.onFinish();
}

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
    uploadFormRef.value!.validate(async (errors) => {
      if (errors) {
        const firstError =
          Object.values(errors)
            .flat()
            .find((err) => err.message)?.message || "请检查表单输入";
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
    questionType: ""
  };
  currentBankId.value = null;
}

/**
 * 处理题型练习提交
 */
async function handleQuestionTypeSubmit() {
  if (!questionTypeForm.value.questionType) {
    message.error(t('bank.selectQuestionTypeError'));
    return false;
  }

  if (!currentBankId.value) {
    message.error(t('message.unknownError'));
    return false;
  }

  try {
    const targetBank = banks.value.find((b) => b.id === currentBankId.value);
    const bankName = targetBank?.name || `ID: ${currentBankId.value}`;

    router.push({
      name: "content",
      params: {
        bankId: currentBankId.value.toString(),
        type: "question-type"
      },
      query: {
        bankName: bankName,
        questionType: questionTypeForm.value.questionType
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
  padding: var(--spacing-sm);
}

.top-actions {
  margin-bottom: var(--spacing-4);
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
  margin-bottom: var(--spacing-4);
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

.bank-option.selected :deep(.n-checkbox-box) {
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
    margin-bottom: var(--spacing-3);
  }
}
</style>
