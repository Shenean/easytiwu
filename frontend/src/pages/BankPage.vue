<template>
  <PageContainer title="题库列表" card-class="bank-card">
    <!-- 空状态占位 -->
    <EmptyState v-if="!loading && banks.length === 0" description="暂无题库数据" icon-type="folder"
      :show-default-action="true" action-text="刷新数据" action-type="primary" action-size="small" @action="fetchBanks" />

    <!-- 移动端卡片列表 -->
    <div v-else class="banks-container">
      <div v-for="bank in banks" :key="bank.id" class="bank-card-item">
        <div class="bank-header">
          <div class="bank-id">ID: {{ bank.id }}</div>
          <div class="bank-name">{{ bank.name }}</div>
        </div>

        <div class="bank-description" v-if="bank.description">
          {{ bank.description }}
        </div>

        <n-grid :cols="3" :x-gap="8" :y-gap="8" class="bank-stats">
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">总题数</div>
              <div class="stat-value">{{ bank.totalCount }}</div>
            </div>
          </n-grid-item>
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">已完成</div>
              <div class="stat-value">{{ bank.completedCount }}</div>
            </div>
          </n-grid-item>
          <n-grid-item>
            <div class="stat-item">
              <div class="stat-label">错题数</div>
              <div class="stat-value">{{ bank.wrongCount }}</div>
            </div>
          </n-grid-item>
        </n-grid>

        <div class="bank-actions">
          <BaseButton type="primary" size="medium" class="action-button" @click="handlePractice(bank.id)"
            :aria-label="`练习题库 ${bank.name}`">
            开始练习
          </BaseButton>
          <BaseButton type="warning" size="medium" class="action-button" @click="handleWrongSet(bank.id)"
            :aria-label="`查看错题集 ${bank.name}`">
            错题集
          </BaseButton>
          <BaseButton type="error" ghost size="medium" class="action-button" @click="confirmDelete(bank.id)"
            :aria-label="`删除题库 ${bank.name}`">
            删除
          </BaseButton>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {NGrid, NGridItem, useDialog, useMessage} from 'naive-ui'
import {useRouter} from 'vue-router'
import axios, {AxiosError} from 'axios'
import BaseButton from '../components/common/BaseButton.vue'
import EmptyState from '../components/common/EmptyState.vue'
import PageContainer from '../components/common/PageContainer.vue'


// 定义统一响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

interface QuestionBank {
  id: number
  name: string
  description: string
  totalCount: number
  completedCount: number
  wrongCount: number
}

// ================== 状态管理 ==================
const message = useMessage()
const dialog = useDialog()
const router = useRouter()
const banks = ref<QuestionBank[]>([])
const loading = ref(false)








// ================== 数据获取 ==================
/**
 * 获取题库数据
 */
async function fetchBanks() {
  loading.value = true
  try {
    const res = await axios.get<ApiResponse<QuestionBank[]>>('/api/bank')

    // 检查响应格式并正确处理数据
    if (res.data.code === 200) {
      banks.value = res.data.data || []
    } else {
      message.error(res.data.message || '获取题库失败')
    }
  } catch (err) {
    console.error('[API Error] 获取题库失败:', err)
    if (err instanceof AxiosError) {
      message.error(`请求失败：${err.response?.data?.message || '未知错误'}`)
    } else {
      message.error('获取题库失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// ================== 操作处理 ==================
function handlePractice(id: number) {
  try {
    const targetBank = banks.value.find(b => b.id === id)
    const bankName = targetBank?.name || `ID: ${id}`
    router.push({
      name: 'content',
      params: { bankId: id.toString(), type: 'all' },
      query: { bankName: bankName }
    })
    // 移除这里的成功提示，将在ContentPage中统一显示
  } catch (error) {
    console.error('路由跳转失败:', error)
    message.error('页面跳转失败，请稍后重试')
  }
}

function handleWrongSet(id: number) {
  try {
    const targetBank = banks.value.find(b => b.id === id)
    const bankName = targetBank?.name || `ID: ${id}`
    router.push({
      name: 'content',
      params: { bankId: id.toString(), type: 'wrong' },
      query: { bankName: bankName }
    })
    // 移除这里的成功提示，将在ContentPage中统一显示
  } catch (error) {
    console.error('路由跳转失败:', error)
    message.error('页面跳转失败，请稍后重试')
  }
}

function confirmDelete(id: number) {
  const targetBank = banks.value.find(b => b.id === id)
  const bankName = targetBank?.name || `ID: ${id}`

  dialog.warning({
    title: '⚠️ 确认删除',
    content: `确定要删除题库 “${bankName}” 吗？此操作不可恢复！`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await handleDelete(id)
    },
    onNegativeClick: () => {
      message.info('已取消删除')
    }
  })
}

async function handleDelete(id: number) {
  try {
    const res = await axios.delete<ApiResponse<void>>(`/api/bank/${id}`)

    // 检查响应格式
    if (res.data.code === 200) {
      message.success('删除成功 ✅')
      // 优化：前端移除，避免重新拉取全部数据
      banks.value = banks.value.filter(b => b.id !== id)
    } else {
      message.error(res.data.message || '删除失败')
    }
  } catch (err) {
    console.error('[API Error] 删除失败:', err)
    if (err instanceof AxiosError) {
      message.error(`删除失败：${err.response?.data?.message || '未知错误'}`)
    } else {
      message.error('删除失败，请稍后重试')
    }
  }
}

// ================== 生命周期 ==================
onMounted(() => {
  fetchBanks()
})

// ================== 暴露方法（供父组件调用） ==================
defineExpose({
  refresh: fetchBanks,
  getBanks: () => [...banks.value]
})
</script>

<style scoped>
/* 页面容器样式已移至 PageContainer 组件 */

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

/* 卡片容器样式 */
.banks-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
  padding: 0;
}

.bank-card-item {
  border-radius: var(--card-border-radius);
  padding: var(--card-padding-desktop);
  box-shadow: var(--card-unified-shadow);
  border: none;
  position: relative;
  overflow: hidden;
  width: var(--card-standard-width);
  max-width: var(--card-content-max-width);
  margin: 0 auto;
  transition: box-shadow 0.3s ease;
}

.bank-card-item:hover {
  box-shadow: var(--card-unified-shadow-hover);
}

.bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-2);
  flex-wrap: wrap;
  gap: var(--spacing-2);
  /* 8px */
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
  margin-bottom: var(--spacing-3);
  word-break: break-word;
}

.bank-stats {
  margin-bottom: var(--spacing-4);
  /* 16px */
  padding: var(--spacing-3);
  background: var(--color-bg-soft);
  border-radius: var(--border-radius-sm);
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-1);
  /* 4px */
  font-weight: 500;
}

.stat-value {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--color-text-primary);
}

.bank-actions {
  display: flex;
  gap: var(--spacing-2);
  flex-wrap: wrap;
}

.action-button {
  flex: 1;
  min-width: var(--spacing-25);
  height: var(--spacing-8);
  font-size: var(--font-size-sm);
}

/* 空状态提示按钮动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
  border-radius: var(--border-radius-md);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .banks-container {
    gap: var(--spacing-3);
    /* 12px */
  }

  .bank-card-item {
    padding: var(--card-padding-tablet);
    border-radius: var(--card-border-radius);
    width: var(--card-tablet-width);
    box-shadow: var(--card-tablet-shadow);
  }

  .bank-header {
    margin-bottom: var(--spacing-2);
    /* 8px */
    gap: var(--spacing-1);
  }

  .bank-name {
    font-size: var(--font-size-base);
  }

  .bank-description {
    font-size: var(--font-size-xs);
    margin-bottom: var(--spacing-3);
    /* 12px */
  }

  .bank-stats {
    padding: var(--spacing-3);
    /* 12px */
    gap: var(--spacing-2);
    /* 8px */
    margin-bottom: var(--spacing-3);
  }

  .stat-value {
    font-size: var(--font-size-base);
  }

  .bank-actions {
    gap: var(--spacing-2);
    /* 8px */
  }

  .action-button {
    min-width: var(--spacing-20);
    /* 80px */
    height: var(--spacing-8);
    /* 32px */
    font-size: var(--font-size-xs);
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
    gap: var(--spacing-2);
  }

  .bank-card-item {
    padding: var(--card-padding-mobile);
    width: var(--card-mobile-width);
    box-shadow: var(--card-mobile-shadow);
  }

  .bank-name {
    font-size: var(--font-size-sm);
  }

  .bank-description {
    font-size: var(--font-size-xs);
    margin-bottom: var(--spacing-2);
  }

  .bank-stats {
    padding: var(--spacing-2);
    gap: var(--spacing-1);
    margin-bottom: var(--spacing-3);
  }

  .stat-label {
    font-size: var(--font-size-xs);
  }

  .stat-value {
    font-size: var(--font-size-sm);
  }

  .action-button {
    min-width: var(--spacing-17);
    height: var(--spacing-7);
    font-size: var(--font-size-xs);
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

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .bank-card {
    margin: var(--spacing-2);
    width: var(--card-mobile-width);
    box-shadow: var(--card-mobile-shadow);
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

/* 横屏模式优化 */
@media (max-height: 500px) and (orientation: landscape) {
  .bank-card {
    margin: var(--spacing-2) var(--spacing-4);
    width: var(--card-tablet-width);
    box-shadow: var(--card-tablet-shadow);
  }

  .empty-state {
    padding: var(--spacing-5);
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