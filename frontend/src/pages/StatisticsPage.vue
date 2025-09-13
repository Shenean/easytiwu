<template>
  <PageContainer title="题库统计" card-class="stats-card" background="transparent" container-class="statistics-container">
    <!-- 加载状态 -->
    <LoadingContainer :loading="loading" description="正在加载统计数据..." height="400px" />

    <!-- 内容区域 -->
    <template v-if="!loading">
      <!-- 错误提示 -->
      <n-alert v-if="error" type="error" :title="error" style="margin-bottom: 16px;" />

      <!-- 统计数据内容 -->
      <div v-else-if="stats">
        <!-- 总览统计 -->
        <n-grid :cols="2" :x-gap="16" :y-gap="16" class="overview-grid">
          <n-grid-item>
            <n-card embedded class="overview-card">
              <n-statistic label="题库总数" :value="formatNumber(stats.bankTotal)" />
            </n-card>
          </n-grid-item>
          <n-grid-item>
            <n-card embedded class="overview-card">
              <n-statistic label="题目总数" :value="formatNumber(stats.questionTotal)" />
            </n-card>
          </n-grid-item>
        </n-grid>

        <!-- 题型统计 -->
        <n-space vertical size="large" style="margin-top: 24px;">
          <n-card v-for="stat in typeStats" :key="stat.type" class="type-stat-card" :title="stat.type" size="small"
            embedded>
            <template #header-extra>
              <n-tag type="info" size="small">
                {{ stat.percentage }}%
              </n-tag>
            </template>

            <n-grid :cols="3" :x-gap="12" :y-gap="12" class="stats-grid">
              <n-grid-item>
                <n-statistic label="总题数" :value="stat.total" />
              </n-grid-item>
              <n-grid-item>
                <n-statistic label="已完成" :value="stat.completed" />
              </n-grid-item>
              <n-grid-item>
                <n-statistic label="正确率" :value="stat.accuracy + '%'" />
              </n-grid-item>
            </n-grid>

            <div style="margin-top: 16px;">
              <div style="margin-bottom: 8px; font-size: 14px; color: var(--n-text-color-2);">完成进度</div>
              <n-progress type="line" :percentage="stat.percentage" :show-indicator="false" :height="8"
                border-radius="4px" />
            </div>
          </n-card>
        </n-space>
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
      type,
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
  padding: 20px;
}

.stats-card {
  border-radius: 12px;
  background: var(--n-card-color);
  box-shadow: var(--n-box-shadow-1);
  border: 1px solid var(--n-border-color);
}

.overview-grid {
  margin-bottom: 8px;
}

.overview-card {
  text-align: center;
  transition: all 0.2s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--n-box-shadow-2);
}

.type-stat-card {
  transition: all 0.2s ease;
}

.type-stat-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--n-box-shadow-2);
}

.stats-grid {
  text-align: center;
}

/* 统计数字样式优化 */
:deep(.n-statistic-value) {
  font-weight: 600;
  color: var(--n-primary-color);
}

:deep(.n-statistic-label) {
  color: var(--n-text-color-2);
  font-weight: 500;
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
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .statistics-container {
    padding: 8px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 6px;
  }
}
</style>
