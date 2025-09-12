<template>
  <n-card title="题库列表" class="bank-card" :segmented="{ content: true }" size="large">
    <!-- 空状态占位 -->
    <div v-if="!loading && banks.length === 0" class="empty-state">
      <n-empty description="暂无题库数据">
        <template #extra>
          <BaseButton type="primary" size="small" @click="fetchBanks">
            刷新数据
          </BaseButton>
        </template>
      </n-empty>
    </div>

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
          <BaseButton 
            type="primary" 
            size="medium"
            class="action-button"
            @click="handlePractice(bank.id)"
            :aria-label="`练习题库 ${bank.name}`"
          >
            开始练习
          </BaseButton>
          <BaseButton 
            type="warning" 
            size="medium"
            class="action-button"
            @click="handleWrongSet(bank.id)"
            :aria-label="`查看错题集 ${bank.name}`"
          >
            错题集
          </BaseButton>
          <BaseButton 
            type="error" 
            ghost
            size="medium"
            class="action-button"
            @click="confirmDelete(bank.id)"
            :aria-label="`删除题库 ${bank.name}`"
          >
            删除
          </BaseButton>
        </div>
      </div>
    </div>
  </n-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { useRouter } from 'vue-router'
import axios, { AxiosError } from 'axios'
import BaseButton from '../components/common/BaseButton.vue'

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
    router.push({ name: 'content', params: { bankId: id.toString(), type: 'all' } })
    message.success(`进入题库 ID: ${id} 的全部练习`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    message.error('页面跳转失败，请稍后重试')
  }
}

function handleWrongSet(id: number) {
  try {
    router.push({ name: 'content', params: { bankId: id.toString(), type: 'wrong' } })
    message.success(`进入题库 ID: ${id} 的错题集`)
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
.bank-card {
  max-width: 1080px;
  margin: 48px auto;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  transition: box-shadow 0.3s ease;
}

.bank-card:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
}

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
  gap: 16px;
  padding: 0;
}

.bank-card-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.bank-card-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.bank-id {
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.bank-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
  min-width: 0;
  word-break: break-word;
}

.bank-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
  word-break: break-word;
}

.bank-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
  font-weight: 500;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.bank-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-button {
  flex: 1;
  min-height: 48px;
  min-width: 120px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  touch-action: manipulation;
  cursor: pointer;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-button:active {
  transform: translateY(0);
  transition: transform 0.1s;
}

/* 空状态提示按钮动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
  border-radius: 8px;
}

/* 移动端适配 */
@media (max-width: 768px) {
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