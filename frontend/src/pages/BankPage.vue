<template>
  <PageContainer title="题库列表" max-width="1080px" card-class="bank-card">
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

        <div class="bank-stats">
          <div class="stat-item">
            <div class="stat-label">总题数</div>
            <div class="stat-value">{{ bank.totalCount }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">已完成</div>
            <div class="stat-value">{{ bank.completedCount }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">错题数</div>
            <div class="stat-value">{{ bank.wrongCount }}</div>
          </div>
        </div>

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
import { ref, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { useRouter } from 'vue-router'
import axios, { AxiosError } from 'axios'
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
  padding: 48px 0;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
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
  gap: 12px;
  padding: 0;
}

.bank-card-item {
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
}

.bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 6px;
}

.bank-id {
  font-size: 11px;
  color: #666;
  padding: 2px 6px;
  border-radius: 3px;
  font-weight: 500;
}

.bank-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
  min-width: 0;
  word-break: break-word;
  line-height: 1.3;
}

.bank-description {
  color: #666;
  font-size: 13px;
  line-height: 1.4;
  margin-bottom: 12px;
  word-break: break-word;
}

.bank-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 14px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 11px;
  color: #999;
  margin-bottom: 3px;
  font-weight: 500;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.bank-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-button {
  flex: 1;
  min-width: 100px;
  height: 32px;
  font-size: 13px;
}

/* 空状态提示按钮动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
  border-radius: 8px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .banks-container {
    gap: 10px;
  }

  .bank-card-item {
    padding: 14px;
    border-radius: 6px;
  }

  .bank-header {
    margin-bottom: 6px;
    gap: 4px;
  }

  .bank-name {
    font-size: 15px;
  }

  .bank-description {
    font-size: 12px;
    margin-bottom: 10px;
  }

  .bank-stats {
    padding: 10px;
    gap: 6px;
    margin-bottom: 12px;
  }

  .stat-value {
    font-size: 16px;
  }

  .bank-actions {
    gap: 6px;
  }

  .action-button {
    min-width: 80px;
    height: 30px;
    font-size: 12px;
  }

  .bank-card {
    margin: 16px;
    border-radius: 12px;
  }

  .table-container {
    margin: 0 -16px;
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  .mobile-table {
    font-size: 14px;
    min-width: 650px;
  }

  :deep(.n-data-table-th) {
    padding: 10px 8px !important;
    font-size: 13px;
    white-space: nowrap;
  }

  :deep(.n-data-table-td) {
    padding: 12px 8px !important;
    font-size: 13px;
    vertical-align: middle;
  }

  :deep(.action-btn) {
    min-width: 50px;
    padding: 6px 8px;
    font-size: 11px;
    margin: 0 2px;
  }

  .empty-state {
    padding: 40px 20px;
  }

  /* 优化表格行高 */
  :deep(.n-data-table-tr) {
    min-height: 60px;
  }
}

@media (max-width: 480px) {
  .banks-container {
    gap: 8px;
  }

  .bank-card-item {
    padding: 12px;
  }

  .bank-name {
    font-size: 14px;
  }

  .bank-description {
    font-size: 11px;
    margin-bottom: 8px;
  }

  .bank-stats {
    padding: 8px;
    gap: 4px;
    margin-bottom: 10px;
  }

  .stat-label {
    font-size: 10px;
  }

  .stat-value {
    font-size: 14px;
  }

  .action-button {
    min-width: 70px;
    height: 28px;
    font-size: 11px;
  }

  .bank-card {
    margin: 12px;
  }

  .mobile-table {
    font-size: 13px;
    min-width: 600px;
  }

  :deep(.n-data-table-th) {
    padding: 8px 6px !important;
    font-size: 12px;
  }

  :deep(.n-data-table-td) {
    padding: 10px 6px !important;
    font-size: 12px;
  }

  :deep(.action-btn) {
    min-width: 45px;
    padding: 4px 6px;
    font-size: 10px;
    margin: 0 1px;
  }

  .empty-state {
    padding: 30px 16px;
  }

  /* 进一步优化小屏幕表格 */
  :deep(.n-data-table-tr) {
    min-height: 55px;
  }
}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .bank-card {
    margin: 8px;
  }

  .mobile-table {
    min-width: 550px;
  }

  :deep(.action-btn) {
    min-width: 40px;
    padding: 3px 5px;
    font-size: 9px;
  }

  :deep(.n-data-table-th),
  :deep(.n-data-table-td) {
    padding: 6px 4px !important;
    font-size: 11px;
  }
}

/* 横屏模式优化 */
@media (max-height: 500px) and (orientation: landscape) {
  .bank-card {
    margin: 8px 16px;
  }

  .empty-state {
    padding: 20px;
  }

  :deep(.n-data-table-tr) {
    min-height: 45px;
  }

  :deep(.n-data-table-th),
  :deep(.n-data-table-td) {
    padding: 6px 8px !important;
  }
}
</style>