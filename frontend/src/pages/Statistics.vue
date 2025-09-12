<template>
  <n-card title="题库统计" class="stats-card">
    <n-space vertical size="large">
      <!-- 使用 n-spin 的 default slot，确保 v-if/v-else 是相邻的 -->
      <n-spin :show="loading" description="正在加载统计数据...">
        <template #default>
          <!-- 错误提示 -->
          <n-alert v-if="error" type="error" :title="error" class="error-alert" />

          <!-- 统计数据内容（仅当 stats 非空 时展示） -->
          <div v-else-if="!loading && stats">
            <!-- 总览 -->
            <n-space justify="space-around" class="overview-space">
              <n-statistic label="题库总数" :value="formatNumber(stats.bankTotal)" />
              <n-statistic label="题目总数" :value="formatNumber(stats.questionTotal)" />
            </n-space>

            <!-- 题型统计表格 -->
            <n-data-table
              :columns="columns"
              :data="tableData"
              :bordered="false"
              size="small"
              :loading="loading"
            />
          </div>

          <!-- 空状态 -->
          <n-empty v-else description="暂无统计数据" />
        </template>
      </n-spin>

      <!-- 刷新按钮 -->
      <n-space justify="center">
        <BaseButton @click="refreshData" :loading="loading" type="primary">
          刷新数据
        </BaseButton>
      </n-space>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { DataTableColumns } from 'naive-ui'
import { statisticsAPI } from '../api/config'
import BaseButton from '../components/common/BaseButton.vue'
import type {
  StatisticsOverview,
  StatisticsTableRow,
  ApiResponse,
  TypeStats
} from '../types/statistics'

// 数字格式化函数，防御性处理null/undefined
const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString()
}

// 百分比格式化函数
const formatPercentage = (correct: number | null | undefined, total: number | null | undefined): string => {
  const correctCount = correct ?? 0
  const totalCount = total ?? 0
  if (totalCount === 0) return '0%'
  const accuracy = (correctCount / totalCount * 100).toFixed(1)
  return `${accuracy}%`
}

// 响应式数据
const stats = ref<StatisticsOverview | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

// 表格列定义
const columns: DataTableColumns<StatisticsTableRow> = [
  {
    title: '题型',
    key: 'type',
    width: 120
  },
  {
    title: '总题数',
    key: 'count',
    width: 100,
    render: (row) => formatNumber(row.count)
  },
  {
    title: '已完成',
    key: 'completedCount',
    width: 100,
    render: (row) => formatNumber(row.completedCount)
  },
  {
    title: '答对数',
    key: 'correctCount',
    width: 100,
    render: (row) => formatNumber(row.correctCount)
  },
  {
    title: '正确率',
    key: 'accuracy',
    width: 100,
    render: (row) => formatPercentage(row.correctCount, row.completedCount)
  }
]

// 将 byType 转为表格数据
const tableData = computed<StatisticsTableRow[]>(() => {
  if (!stats.value?.byType) return []

  return Object.entries(stats.value.byType).map(([type, v]: [string, TypeStats]) => ({
    type,
    count: v.count ?? 0,
    completedCount: v.completedCount ?? 0,
    correctCount: v.correctCount ?? 0
  }))
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true
    error.value = null

    const response = await statisticsAPI.getOverview()

    // 可能的后端包装：{ success, data } 或 直接对象
    const body = response.data
    if (body && typeof body === 'object') {
      const apiResp = body as ApiResponse<StatisticsOverview>
      if (apiResp.success && apiResp.data) {
        stats.value = apiResp.data
      } else if (typeof body.bankTotal === 'number') {
        // 若后端直接返回 overview 对象
        stats.value = body as StatisticsOverview
      } else {
        throw new Error(apiResp.message || '获取统计数据失败')
      }
    } else {
      throw new Error('响应数据格式错误')
    }
  } catch (err: any) {
    console.error('获取统计数据失败:', err)

    if (err.response) {
      const status = err.response.status
      const msg = err.response.data?.message || err.message
      switch (status) {
        case 404:
          error.value = '统计服务不可用，请检查服务是否启动'
          break
        case 500:
          error.value = `服务器内部错误: ${msg}`
          break
        case 503:
          error.value = '统计服务暂时不可用，请稍后重试'
          break
        default:
          error.value = `请求失败 (${status}): ${msg}`
      }
    } else if (err.code === 'ECONNREFUSED' || (err.message && err.message.includes('Network Error'))) {
      error.value = '无法连接到服务器，请检查网络连接和服务状态'
    } else {
      error.value = err.message || '获取统计数据时发生未知错误'
    }
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  fetchStatistics()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.stats-card {
  max-width: 1000px;
  margin: 0 auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.error-alert {
  margin-bottom: 16px;
}

/* 统计数字样式优化 */
:deep(.n-statistic-value) {
  font-weight: 600;
  color: #1890ff;
}

:deep(.n-statistic-label) {
  font-size: 14px;
  color: #666;
}

/* 表格样式优化 */
:deep(.n-data-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.n-data-table-th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.n-data-table-td) {
  border-bottom: 1px solid #f0f0f0;
}

/* 加载状态样式 */
:deep(.n-spin-content) {
  min-height: 200px;
}

/* 刷新按钮样式 */
:deep(.n-button) {
  border-radius: 6px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-card {
    margin: 0 16px;
  }

  :deep(.n-space) {
    flex-direction: column;
  }

  :deep(.n-statistic) {
    text-align: center;
  }
}
</style>
