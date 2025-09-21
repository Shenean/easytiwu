<template>
  <PageContainer :title="t('bank.title')" :show-card="false" container-class="bank-container">
    <!-- 顶部操作栏 -->
    <div v-if="!loading && banks.length > 1" class="top-actions">
      <n-button type="primary" size="medium" @click="showMergeModal = true" :disabled="banks.length < 2">
        {{ t("bank.merge") }}
      </n-button>
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
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, h, onMounted, ref} from "vue";
import type {DataTableColumns} from "naive-ui";
import {
  NButton,
  NCheckbox,
  NDataTable,
  NFormItem,
  NInput,
  NModal,
  NSpace,
  NText,
  useDialog,
  useMessage,
} from "naive-ui";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import axios, {AxiosError} from "axios";

import PageContainer from "../components/common/PageContainer.vue";
import type {ApiResponse, QuestionBank} from "@/types/common";

// ================== 状态管理 ==================
const message = useMessage();
const dialog = useDialog();
const router = useRouter();
const { t } = useI18n();
const banks = ref<QuestionBank[]>([]);
const loading = ref(false);

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
    width: 280,
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
/* 顶部操作栏样式 */
.top-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: var(--spacing-4);
}

/* 表格容器样式 */
.table-container {
  background: var(--color-bg-base);
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.bank-table {
  background: var(--color-bg-base);
}

/* 合并弹窗样式 */
.merge-form {
  padding: var(--spacing-2) 0;
}

.form-section {
  margin-bottom: var(--spacing-4);
}

.form-label {
  display: block;
  margin-bottom: var(--spacing-1);
  font-weight: 600;
  font-size: var(--font-size-sm);
}

.form-hint {
  display: block;
  margin-bottom: var(--spacing-2);
  font-size: var(--font-size-xs);
}

.bank-list {
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius-md);
  max-height: 200px;
  overflow-y: auto;
}

.bank-option {
  display: flex;
  align-items: center;
  padding: var(--spacing-2) var(--spacing-3);
  border-bottom: 1px solid var(--color-border-soft);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.bank-option:last-child {
  border-bottom: none;
}

.bank-option:hover {
  background-color: var(--color-bg-soft);
}

.bank-option.selected {
  background-color: var(--color-primary-light);
}

.bank-info {
  margin-left: var(--spacing-2);
  flex: 1;
}

.bank-option-name {
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 2px;
}

.bank-option-id {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

/* 表格样式优化 */
:deep(.n-data-table-th) {
  background-color: var(--color-bg-soft);
  font-weight: 600;
  color: var(--color-text-primary);
}

:deep(.n-data-table-td) {
  vertical-align: middle;
}

:deep(.n-data-table-tr:hover .n-data-table-td) {
  background-color: var(--color-bg-soft);
}

/* 空状态动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
  border-radius: var(--border-radius-md);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(var(--spacing-2));
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
