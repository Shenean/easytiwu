<template>
  <PageContainer 
    :title="$t('bank.title')" 
    :show-card="false"
    container-class="bank-container"
  >
    <!-- 顶部操作栏 -->
    <div v-if="!loading && banks.length > 1" class="top-actions">
      <n-button 
        type="primary" 
        size="medium" 
        @click="showMergeModal = true"
        :disabled="banks.length < 2"
      >
        {{ $t("bank.merge") }}
      </n-button>
    </div>

    <!-- 空状态占位 -->
    <n-empty
      v-if="!loading && banks.length === 0"
      :description="$t('bank.noData')"
      size="large"
    >
      <template #extra>
        <n-button type="primary" size="small" @click="fetchBanks">
          {{ $t("bank.refresh") }}
        </n-button>
      </template>
    </n-empty>

    <!-- 移动端卡片列表 -->
    <div v-else class="banks-container">
      <div v-for="bank in banks" :key="bank.id" class="bank-card-item">
        <div class="bank-header">
          <div class="bank-id">{{ $t("bank.id") }}: {{ bank.id }}</div>
          <div class="bank-name">{{ bank.name }}</div>
        </div>

        <div class="bank-description" v-if="bank.description">
          {{ bank.description }}
        </div>

        <n-grid :cols="3" :x-gap="8" :y-gap="8" class="bank-stats">
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">{{ $t("bank.totalQuestions") }}</div>
              <div class="stat-value">{{ bank.totalCount }}</div>
            </div>
          </n-grid-item>
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">{{ $t("bank.completedQuestions") }}</div>
              <div class="stat-value">{{ bank.completedCount }}</div>
            </div>
          </n-grid-item>
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">{{ $t("bank.errorQuestions") }}</div>
              <div class="stat-value">{{ bank.wrongCount }}</div>
            </div>
          </n-grid-item>
        </n-grid>

        <div class="bank-actions">
          <n-button
            type="primary"
            size="medium"
            class="action-button"
            @click="handlePractice(bank.id)"
            :aria-label="`练习题库 ${bank.name}`"
          >
            {{ $t("bank.startPractice") }}
          </n-button>
          <n-button
            type="default"
            size="medium"
            class="action-button"
            @click="handleWrongSet(bank.id)"
            :aria-label="`查看错题集 ${bank.name}`"
          >
            {{ $t("bank.errorCollection") }}
          </n-button>
          <n-button
            type="default"
            ghost
            size="medium"
            class="action-button"
            @click="confirmDelete(bank.id)"
            :aria-label="`删除题库 ${bank.name}`"
          >
            {{ $t("bank.delete") }}
          </n-button>
        </div>
      </div>
    </div>

    <!-- 合并题库弹窗 -->
    <n-modal 
      v-model:show="showMergeModal" 
      preset="dialog" 
      :title="$t('bank.mergeTitle')"
      :positive-text="$t('bank.confirmMerge')"
      :negative-text="$t('message.cancel')"
      @positive-click="handleMergeSubmit"
      @negative-click="resetMergeForm"
      :loading="mergeLoading"
      style="width: 90%; max-width: 500px;"
    >
      <div class="merge-form">
        <!-- 题库选择 -->
        <div class="form-section">
          <n-text class="form-label">{{ $t("bank.selectBanks") }}</n-text>
          <n-text depth="3" class="form-hint">{{ $t("bank.selectBanksHint") }}</n-text>
          <div class="bank-list">
            <div 
              v-for="bank in banks" 
              :key="bank.id" 
              class="bank-option"
              :class="{ 'selected': selectedBanks.includes(bank.id) }"
              @click="toggleBankSelection(bank.id)"
            >
              <n-checkbox 
                :checked="selectedBanks.includes(bank.id)"
                :disabled="!selectedBanks.includes(bank.id) && selectedBanks.length >= 2"
                @update:checked="(checked) => handleBankCheck(bank.id, checked)"
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
          <n-form-item 
            :label="$t('bank.newBankName')"
            :validation-status="nameError ? 'error' : undefined"
            :feedback="nameError"
          >
            <n-input 
              v-model:value="mergeForm.name"
              :placeholder="$t('bank.newBankNamePlaceholder')"
              @blur="validateName"
              @input="nameError = ''"
            />
          </n-form-item>
        </div>

        <!-- 描述信息 -->
        <div class="form-section">
          <n-form-item :label="$t('bank.description')">
            <n-input 
              v-model:value="mergeForm.description"
              type="textarea"
              :placeholder="$t('bank.descriptionPlaceholder')"
              :autosize="{ minRows: 3, maxRows: 5 }"
            />
          </n-form-item>
        </div>
      </div>
    </n-modal>
  </PageContainer>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {NCheckbox, NFormItem, NGrid, NGridItem, NInput, NModal, NText, useDialog, useMessage} from "naive-ui";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import axios, {AxiosError} from "axios";

import PageContainer from "../components/common/PageContainer.vue";
import type {ApiResponse, QuestionBank} from '@/types/common';

// ================== 状态管理 ==================
const message = useMessage();
const dialog = useDialog();
const router = useRouter();
const { t } = useI18n();
const banks = ref<QuestionBank[]>([]);
const loading = ref(false);

// ================== 合并功能状态 ==================
const showMergeModal = ref(false);
const mergeLoading = ref(false);
const selectedBanks = ref<number[]>([]);
const mergeForm = ref({
  name: '',
  description: ''
});
const nameError = ref('');

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
 * 处理复选框状态变化
 */
function handleBankCheck(bankId: number, checked: boolean) {
  if (checked && !selectedBanks.value.includes(bankId) && selectedBanks.value.length < 2) {
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
    nameError.value = t('bank.nameRequired');
    return false;
  }
  if (mergeForm.value.name.trim().length < 2) {
    nameError.value = t('bank.nameTooShort');
    return false;
  }
  nameError.value = '';
  return true;
}

/**
 * 验证表单
 */
function validateMergeForm() {
  let isValid = true;
  
  // 验证题库选择
  if (selectedBanks.value.length < 1 || selectedBanks.value.length > 2) {
    message.error(t('bank.selectBanksError'));
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
    name: '',
    description: ''
  };
  nameError.value = '';
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
      bankId2: selectedBanks.value[1] || selectedBanks.value[0], // 如果只选择一个，则复制自己
      name: mergeForm.value.name.trim(),
      description: mergeForm.value.description.trim()
    };
    
    const res = await axios.post<ApiResponse<QuestionBank>>('/api/bank/merge', requestData);
    
    if (res.data.code === 200) {
      message.success(t('bank.mergeSuccess'));
      showMergeModal.value = false;
      resetMergeForm();
      // 刷新题库列表
      await fetchBanks();
      return true;
    } else {
      message.error(res.data.message || t('bank.mergeFailed'));
      return false;
    }
  } catch (err) {
    if (err instanceof AxiosError) {
      message.error(
        `${t('bank.mergeFailed')}：${
          err.response?.data?.message || t('message.unknownError')
        }`
      );
    } else {
      message.error(`${t('bank.mergeFailed')}，${t('message.retryLater')}`);
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
/* 页面容器样式已移至 PageContainer 组件 */

/* 顶部操作栏样式 */
.top-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: var(--spacing-4);
  padding: 0 var(--spacing-2);
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

/* 移动端适配 */
@media (max-width: 768px) {
  .top-actions {
    padding: 0;
    margin-bottom: var(--spacing-3);
  }
  
  .bank-option {
    padding: var(--spacing-2);
  }
  
  .bank-info {
    margin-left: var(--spacing-2);
  }
}

@media (max-width: var(--breakpoint-mobile)) {
  .top-actions {
    margin-bottom: var(--spacing-2);
  }
  
  .form-section {
    margin-bottom: var(--spacing-3);
  }
  
  .bank-list {
    max-height: 150px;
  }
  
  .bank-option {
    padding: var(--spacing-2);
  }
}

.empty-state {
  padding: var(--spacing-12) 0;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
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

/* 卡片容器样式 - 紧凑型布局 */
.banks-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2); /* 16px - 更紧凑的卡片间距 */
  padding: 0;
}

.bank-card-item {
  border-radius: var(--card-border-radius);
  padding: var(--spacing-4); /* 32px - 紧凑型内边距 */
  box-shadow: var(--card-unified-shadow);
  border: none;
  position: relative;
  overflow: hidden;
  width: var(--card-standard-width);
  max-width: var(--card-content-max-width);
  margin: 0 auto;
  transition: all 0.3s ease; /* 包含变形动画 */
  background: var(--color-surface);
}

.bank-card-item:hover {
  box-shadow: var(--card-unified-shadow-hover);
  transform: translateY(-2px); /* 轻微上浮效果 */
}

.bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-2); /* 16px */
  flex-wrap: wrap;
  gap: var(--spacing-1); /* 8px - 紧凑间距 */
}

.bank-id {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  padding: var(--spacing-1) var(--spacing-2);
  /* 4px 8px */
  border-radius: var(--border-radius-xs);
  font-weight: 500;
}

.bank-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-primary);
  flex: 1;
  min-width: 0;
  word-break: break-word;
  line-height: 1.3;
}

.bank-description {
  color: var(--color-text-secondary);
  font-size: var(--font-size-xs);
  line-height: 1.4;
  margin-bottom: var(--spacing-2); /* 16px - 紧凑间距 */
  word-break: break-word;
}

.bank-stats {
  margin-bottom: var(--spacing-3); /* 24px */
  padding: var(--spacing-2); /* 16px - 紧凑内边距 */
  background: var(--color-bg-soft);
  border-radius: var(--border-radius-sm);
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin-bottom: 4px; /* 4px - 固定紧凑间距 */
  font-weight: 500;
}

.stat-value {
  font-size: var(--font-size-base); /* 16px - 紧凑字体大小 */
  font-weight: 600; /* 稍微减轻字重 */
  color: var(--color-text-primary);
}

.bank-actions {
  display: flex;
  gap: var(--spacing-1); /* 8px - 紧凑按钮间距 */
  flex-wrap: wrap;
}

.action-button {
  flex: 1;
  min-width: 80px; /* 紧凑最小宽度 */
  height: 36px; /* 紧凑高度 */
  font-size: var(--font-size-sm);
  padding: var(--spacing-1) var(--spacing-2); /* 8px 16px */
}

/* 空状态提示按钮动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
  border-radius: var(--border-radius-md);
}

/* 移动端适配 - 紧凑型布局 */
@media (max-width: 768px) {
  .banks-container {
    gap: var(--spacing-2); /* 16px - 保持紧凑间距 */
  }

  .bank-card-item {
    padding: var(--spacing-3); /* 24px - 平板紧凑内边距 */
    border-radius: var(--card-border-radius);
    width: var(--card-tablet-width);
    box-shadow: var(--card-tablet-shadow);
  }

  .bank-header {
    margin-bottom: var(--spacing-2); /* 16px */
    gap: var(--spacing-1); /* 8px */
  }

  .bank-name {
    font-size: var(--font-size-base);
  }

  .bank-description {
    font-size: var(--font-size-xs);
    margin-bottom: var(--spacing-2); /* 16px - 保持紧凑 */
  }

  .bank-stats {
    padding: var(--spacing-2); /* 16px - 紧凑内边距 */
    margin-bottom: var(--spacing-3); /* 24px */
  }

  .stat-value {
    font-size: var(--font-size-base);
  }

  .bank-actions {
    gap: var(--spacing-1); /* 8px - 紧凑按钮间距 */
  }

  .action-button {
    min-width: 72px; /* 紧凑最小宽度 */
    height: 32px; /* 紧凑高度 */
    font-size: var(--font-size-xs);
    padding: 6px 12px; /* 紧凑内边距 */
  }

  .bank-card {
    margin: var(--spacing-4);
    border-radius: var(--border-radius-lg);
  }

  .table-container {
    margin: 0 calc(-1 * var(--spacing-4));
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  .mobile-table {
    font-size: var(--font-size-sm);
    min-width: var(--spacing-163);
    /* 652px */
  }

  :deep(.n-data-table-th) {
    padding: var(--spacing-3) var(--spacing-2) !important;
    /* 12px 8px */
    font-size: var(--font-size-xs);
    white-space: nowrap;
  }

  :deep(.n-data-table-td) {
    padding: var(--spacing-3) var(--spacing-2) !important;
    font-size: var(--font-size-xs);
    vertical-align: middle;
  }

  :deep(.action-btn) {
    min-width: var(--spacing-13);
    padding: var(--spacing-2) var(--spacing-2);
    /* 8px 8px */
    font-size: var(--font-size-xs);
    margin: 0 var(--spacing-1);
  }

  .empty-state {
    padding: var(--spacing-10) var(--spacing-5);
  }

  /* 优化表格行高 */
  :deep(.n-data-table-tr) {
    min-height: var(--spacing-15);
  }
}

@media (max-width: var(--breakpoint-mobile)) {
  .banks-container {
    gap: var(--spacing-2); /* 16px - 保持紧凑间距 */
  }

  .bank-card-item {
    padding: var(--spacing-3); /* 24px - 移动端紧凑内边距 */
    width: var(--card-mobile-width);
    box-shadow: var(--card-mobile-shadow);
  }

  .bank-name {
    font-size: var(--font-size-sm);
  }

  .bank-description {
    font-size: var(--font-size-xs);
    margin-bottom: var(--spacing-2); /* 16px */
  }

  .bank-stats {
    padding: var(--spacing-2); /* 16px */
    margin-bottom: var(--spacing-2); /* 16px - 更紧凑 */
  }

  .stat-label {
    font-size: var(--font-size-xs);
  }

  .stat-value {
    font-size: var(--font-size-sm);
  }

  .action-button {
    min-width: 64px; /* 移动端最小宽度 */
    height: 28px; /* 移动端紧凑高度 */
    font-size: var(--font-size-xs);
    padding: 4px 8px; /* 移动端紧凑内边距 */
  }

  .bank-card {
    margin: var(--spacing-3);
    width: var(--card-mobile-width);
    box-shadow: var(--card-mobile-shadow);
  }

  .mobile-table {
    font-size: var(--font-size-xs);
    min-width: var(--spacing-150);
    /* 600px */
  }

  :deep(.n-data-table-th) {
    padding: var(--spacing-2) var(--spacing-2) !important;
    font-size: var(--font-size-xs);
  }

  :deep(.n-data-table-td) {
    padding: var(--spacing-3) var(--spacing-2) !important;
    font-size: var(--font-size-xs);
  }

  :deep(.action-btn) {
    min-width: var(--spacing-11);
    padding: var(--spacing-1) var(--spacing-2);
    font-size: var(--font-size-xs);
    margin: 0 var(--spacing-1);
  }

  .empty-state {
    padding: var(--spacing-8) var(--spacing-md);
  }

  /* 进一步优化小屏幕表格 */
  :deep(.n-data-table-tr) {
    min-height: var(--spacing-14);
  }
}

/* 超小屏幕优化 - 紧凑型布局 */
@media (max-width: 360px) {
  .banks-container {
    gap: var(--spacing-1); /* 8px - 超紧凑间距 */
  }

  .bank-card-item {
    padding: var(--spacing-2); /* 16px - 超紧凑内边距 */
  }

  .bank-stats {
    padding: var(--spacing-1); /* 8px - 超紧凑统计区域 */
    margin-bottom: var(--spacing-2); /* 16px */
  }

  .action-button {
    min-width: 56px; /* 超小屏最小宽度 */
    height: 24px; /* 超小屏高度 */
    font-size: 10px; /* 更小字体 */
    padding: 2px 6px; /* 超紧凑内边距 */
  }

  .mobile-table {
    min-width: var(--spacing-138);
    /* 552px */
  }

  :deep(.action-btn) {
    min-width: var(--spacing-10);
    padding: var(--spacing-1) var(--spacing-1);
    font-size: var(--font-size-2xs);
  }

  :deep(.n-data-table-th),
  :deep(.n-data-table-td) {
    padding: var(--spacing-2) var(--spacing-1) !important;
    font-size: var(--font-size-xs);
  }
}

/* 横屏模式优化 - 紧凑型布局 */
@media (max-height: 500px) and (orientation: landscape) {
  .banks-container {
    gap: var(--spacing-1); /* 8px - 横屏紧凑间距 */
  }

  .bank-card-item {
    padding: var(--spacing-2); /* 16px - 横屏紧凑内边距 */
  }

  .bank-stats {
    padding: var(--spacing-1); /* 8px */
    margin-bottom: var(--spacing-2); /* 16px */
  }

  .empty-state {
    padding: var(--spacing-3); /* 24px - 紧凑空状态 */
  }

  :deep(.n-data-table-tr) {
    min-height: var(--spacing-11);
  }

  :deep(.n-data-table-th),
  :deep(.n-data-table-td) {
    padding: var(--spacing-2) var(--spacing-2) !important;
  }
}
</style>
