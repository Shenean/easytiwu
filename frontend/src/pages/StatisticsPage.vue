<template>
  <PageContainer title="题库统计" card-class="stats-card" container-class="statistics-container">
    <!-- 加载状态 -->
    <LoadingContainer :loading="loading" description="正在加载统计数据..." height="400px" />

    <!-- 内容区域 -->
    <template v-if="!loading">
      <!-- 错误提示 -->
      <n-alert v-if="error" type="error" :title="error" style="margin-bottom: 16px;" />

      <!-- 统计数据内容 -->
      <div v-else-if="stats">
        <!-- 总览统计 -->
        <n-card class="overview-summary-card" size="small">
          <n-grid :cols="2" :x-gap="24" :y-gap="16" class="overview-grid">
            <n-grid-item>
              <div class="overview-item">
                <n-statistic label="题库总数" :value="formatNumber(stats.bankTotal)" />
              </div>
            </n-grid-item>
            <n-grid-item>
              <div class="overview-item">
                <n-statistic label="题目总数" :value="formatNumber(stats.questionTotal)" />
              </div>
            </n-grid-item>
          </n-grid>
        </n-card>

        <!-- 题型统计 -->
        <div class="type-stats-section">
          <n-grid :cols="2" :x-gap="8" :y-gap="8" class="type-stats-grid">
            <n-grid-item v-for="stat in typeStats" :key="stat.originalType">
              <n-card class="type-stat-card" size="small">
                <div class="type-stat-header">
                  <h4 class="type-title">{{ stat.type }}</h4>
                  <n-tag type="info" size="small" class="percentage-tag">
                    {{ stat.percentage }}%
                  </n-tag>
                </div>
                
                <div class="type-stat-content">
                  <n-grid :cols="3" :x-gap="8" :y-gap="4" class="stats-metrics">
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.total }}</div>
                        <div class="metric-label">总题数</div>
                      </div>
                    </n-grid-item>
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.completed }}</div>
                        <div class="metric-label">已完成</div>
                      </div>
                    </n-grid-item>
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.accuracy }}%</div>
                        <div class="metric-label">正确率</div>
                      </div>
                    </n-grid-item>
                  </n-grid>
                </div>
              </n-card>
            </n-grid-item>
          </n-grid>
        </div>
      </div>

      <!-- 空状态 -->
      <EmptyState v-else description="暂无统计数据" icon-type="chart" size="small" />
    </template>

    <!-- 刷新按钮 -->
    <template #action>
      <BaseButton @click="refreshData" :loading="loading" type="primary" size="small">
        刷新数据
      </BaseButton>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

import { statisticsAPI } from '../api/config'
import BaseButton from '../components/common/BaseButton.vue'
import LoadingContainer from '../components/common/LoadingContainer.vue'
import EmptyState from '../components/common/EmptyState.vue'
import PageContainer from '../components/common/PageContainer.vue'
import type {
  StatisticsOverview,
  ApiResponse,
  TypeStats
} from '../types/statistics'

// 数字格式化函数，防御性处理null/undefined
const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString()
}

// 题型字段映射
const getTypeDisplayName = (type: string): string => {
  const typeMap: Record<string, string> = {
    'single': '单选题',
    'fill_blank': '填空题',
    'true_false': '判断题',
    'multiple': '多选题',
    'short_answer': '简答题'
  }
  return typeMap[type] || type
}

// 响应式数据
const stats = ref<StatisticsOverview | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

// 题型统计卡片数据
const typeStats = computed(() => {
  if (!stats.value?.byType) return []

  return Object.entries(stats.value.byType).map(([type, v]: [string, TypeStats]) => {
    const total = v.count ?? 0
    const completed = v.completedCount ?? 0
    const correct = v.correctCount ?? 0
    const percentage = total > 0 ? Math.round((completed / total) * 100) : 0
    const accuracy = completed > 0 ? Math.round((correct / completed) * 100) : 0

    return {
      type: getTypeDisplayName(type),
      originalType: type,
      total,
      completed,
      correct,
      percentage,
      accuracy
    }
  })
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
  } catch (err: unknown) {
    console.error('获取统计数据失败:', err)

    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response: { status: number; data?: { message?: string } }; message?: string }
      const status = axiosErr.response.status
      const msg = axiosErr.response.data?.message || axiosErr.message
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
    } else if (err && typeof err === 'object' && ('code' in err || 'message' in err)) {
      const networkErr = err as { code?: string; message?: string }
      if (networkErr.code === 'ECONNREFUSED' || (networkErr.message && networkErr.message.includes('Network Error'))) {
        error.value = '无法连接到服务器，请检查网络连接和服务状态'
      } else {
        error.value = networkErr.message || '获取统计数据时发生未知错误'
      }
    } else {
      error.value = '获取统计数据时发生未知错误'
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
.statistics-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 16px;
}

.stats-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

/* 总览统计样式 */
.overview-summary-card {
  margin-bottom: 16px;
  background: linear-gradient(135deg, var(--n-color) 0%, var(--n-color-embedded) 100%);
  border: 1px solid var(--n-border-color);
}

.overview-grid {
  margin: 0;
}

.overview-item {
  text-align: center;
  padding: 8px;
}

/* 题型统计区域 */
.type-stats-section {
  margin-top: 0;
}

.type-stats-grid {
  margin: 0;
}

.type-stat-card {
  border: 1px solid var(--n-border-color);
  transition: all 0.3s ease;
  background: var(--n-color);
}

.type-stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.type-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--n-divider-color);
}

.type-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--n-text-color-1);
}

.percentage-tag {
  font-weight: 600;
}

.type-stat-content {
  padding: 0;
}

.stats-metrics {
  margin-bottom: 0;
}

.metric-item {
  text-align: center;
  padding: 6px 2px;
}

.metric-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--n-primary-color);
  margin-bottom: 2px;
  line-height: 1.1;
}

.metric-label {
  font-size: 12px;
  color: var(--n-text-color-2);
  font-weight: 500;
  line-height: 1.2;
}



/* 统计数字样式优化 */
:deep(.n-statistic-value) {
  font-weight: 700;
  color: var(--n-primary-color);
  font-size: 24px;
}

:deep(.n-statistic-label) {
  color: var(--n-text-color-2);
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 4px;
}

/* 紧凑卡片内边距 */
:deep(.overview-summary-card .n-card__content) {
  padding: 16px;
}

:deep(.type-stat-card .n-card__content) {
  padding: 12px;
}

/* 进度条样式优化 */
:deep(.n-progress .n-progress-graph .n-progress-graph-line-fill) {
  transition: all 0.3s ease;
}

/* 加载状态样式 */
:deep(.n-spin-content) {
  min-height: 200px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .statistics-container {
    padding: 12px;
  }

  .overview-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .overview-item {
    padding: 8px;
  }

  .type-stats-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .stats-metrics {
    grid-template-columns: 1fr;
    gap: 8px;
    margin-bottom: 0;
  }

  .metric-item {
    padding: 8px;
    background: var(--n-color-embedded);
    border-radius: 4px;
    border: 1px solid var(--n-border-color);
  }

  .metric-value {
    font-size: 16px;
  }

  .type-stat-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
    margin-bottom: 8px;
    padding-bottom: 6px;
  }

  .type-title {
    font-size: 14px;
  }

  :deep(.overview-summary-card .n-card__content) {
    padding: 12px;
  }

  :deep(.type-stat-card .n-card__content) {
    padding: 10px;
  }

  :deep(.n-statistic-value) {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .statistics-container {
    padding: 8px;
  }

  .overview-grid {
    gap: 8px;
  }

  .overview-item {
    padding: 6px;
  }

  .type-stats-grid {
    gap: 6px;
  }

  .stats-metrics {
    gap: 6px;
  }

  .metric-item {
    padding: 6px;
  }

  .metric-value {
    font-size: 14px;
  }

  .metric-label {
    font-size: 10px;
  }

  .type-title {
    font-size: 13px;
  }

  .type-stat-header {
    margin-bottom: 6px;
    padding-bottom: 4px;
  }

  :deep(.overview-summary-card .n-card__content) {
    padding: 10px;
  }

  :deep(.type-stat-card .n-card__content) {
    padding: 8px;
  }

  :deep(.n-statistic-value) {
    font-size: 16px;
  }

  :deep(.n-statistic-label) {
    font-size: 11px;
  }
}
</style>
