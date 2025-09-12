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
            <div class="overview-container">
              <n-space justify="space-around" class="overview-space" :wrap="false">
                <div class="statistic-item">
                  <n-statistic label="题库总数" :value="formatNumber(stats.bankTotal)" />
                </div>
                <div class="statistic-item">
                  <n-statistic label="题目总数" :value="formatNumber(stats.questionTotal)" />
                </div>
              </n-space>
            </div>

            <!-- 题型统计卡片 -->
            <div class="type-stats-container">
              <div v-for="stat in typeStats" :key="stat.type" class="type-stat-card">
                <div class="type-header">
                  <div class="type-name">{{ stat.type }}</div>
                  <div class="type-badge">{{ stat.percentage }}%</div>
                </div>
                
                <div class="type-stats-grid">
                  <div class="type-stat-item">
                    <div class="stat-label">总题数</div>
                    <div class="stat-value primary">{{ stat.total }}</div>
                  </div>
                  <div class="type-stat-item">
                    <div class="stat-label">已完成</div>
                    <div class="stat-value success">{{ stat.completed }}</div>
                  </div>
                  <div class="type-stat-item">
                    <div class="stat-label">正确率</div>
                    <div class="stat-value" :class="getAccuracyClass(stat.accuracy)">{{ stat.accuracy }}%</div>
                  </div>
                </div>
                
                <div class="progress-section">
                  <div class="progress-label">完成进度</div>
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: stat.percentage + '%' }"></div>
                  </div>
                </div>
              </div>
            </div>
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

import { statisticsAPI } from '../api/config'
import BaseButton from '../components/common/BaseButton.vue'
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

// 获取正确率样式类
const getAccuracyClass = (accuracy: number) => {
  if (accuracy >= 80) return 'success'
  if (accuracy >= 60) return 'warning'
  return 'error'
}

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

/* 总览容器样式 */
.overview-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.overview-space {
  width: 100%;
}

.statistic-item {
  flex: 1;
  text-align: center;
  padding: 0 16px;
}

/* 统计数字样式优化 */
:deep(.n-statistic-value) {
  font-weight: 700;
  color: #1890ff;
  font-size: 28px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

:deep(.n-statistic-label) {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  margin-bottom: 8px;
}

/* 题型统计卡片容器 */
.type-stats-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.type-stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.type-stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.type-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.type-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.type-stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.type-stat-item {
  text-align: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.stat-value.primary {
  color: #1890ff;
}

.stat-value.success {
  color: #52c41a;
}

.stat-value.warning {
  color: #faad14;
}

.stat-value.error {
  color: #ff4d4f;
}

.progress-section {
  margin-top: 16px;
}

.progress-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1890ff 0%, #52c41a 100%);
  border-radius: 4px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 加载状态样式 */
:deep(.n-spin-content) {
  min-height: 200px;
}

/* 刷新按钮样式 */
:deep(.n-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  min-height: 48px;
  padding: 0 24px;
}

:deep(.n-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .stats-card {
    margin: 0 16px;
  }
  
  .overview-container {
    margin: 0 -8px 20px -8px;
    padding: 16px;
    border-radius: 8px;
  }
  
  .statistic-item {
    padding: 0 8px;
  }
  
  :deep(.n-statistic-value) {
    font-size: 24px;
  }
  
  :deep(.n-statistic-label) {
    font-size: 13px;
  }
  
  .type-stats-container {
    gap: 12px;
  }
  
  .type-stat-card {
    padding: 16px;
    border-radius: 10px;
  }
  
  .type-name {
    font-size: 16px;
  }
  
  .type-badge {
    padding: 4px 10px;
    font-size: 11px;
  }
  
  .type-stats-grid {
    gap: 12px;
  }
  
  .type-stat-item {
    padding: 10px;
  }
  
  .stat-value {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .stats-card {
    margin: 0 12px;
  }
  
  .overview-container {
    margin: 0 -4px 16px -4px;
    padding: 12px;
  }
  
  .overview-space {
    flex-direction: column;
    gap: 16px;
  }
  
  .statistic-item {
    padding: 12px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.n-statistic-value) {
    font-size: 22px;
  }
  
  :deep(.n-statistic-label) {
    font-size: 12px;
  }
  
  .type-stat-card {
    padding: 14px;
    border-radius: 8px;
  }
  
  .type-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 12px;
  }
  
  .type-name {
    font-size: 15px;
  }
  
  .type-badge {
    align-self: flex-end;
  }
  
  .type-stats-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    margin-bottom: 16px;
  }
  
  .type-stat-item {
    padding: 8px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .stat-value {
    font-size: 16px;
  }
  
  .progress-label {
    font-size: 11px;
  }
}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .stats-card {
    margin: 0 8px;
  }
  
  .overview-container {
    padding: 10px;
  }
  
  .statistic-item {
    padding: 8px;
  }
  
  :deep(.n-statistic-value) {
    font-size: 20px;
  }
  
  :deep(.n-statistic-label) {
    font-size: 11px;
  }
  
  .type-stat-card {
    padding: 12px;
  }
  
  .type-stats-grid {
    grid-template-columns: 1fr;
    gap: 6px;
  }
  
  .type-stat-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: left;
    padding: 10px;
  }
  
  .stat-label {
    margin-bottom: 0;
  }
  
  .stat-value {
    font-size: 14px;
  }
}

/* 横屏模式优化 */
@media (max-height: 500px) and (orientation: landscape) {
  .stats-card {
    margin: 8px 16px;
  }
  
  .overview-container {
    padding: 12px;
    margin-bottom: 16px;
  }
  
  .overview-space {
    flex-direction: row;
  }
  
  .statistic-item {
    padding: 8px;
  }
  
  :deep(.n-statistic-value) {
    font-size: 20px;
  }
  
  .type-stat-card {
    padding: 12px 16px;
  }
  
  .type-stats-grid {
    margin-bottom: 12px;
  }
  
  .progress-section {
    margin-top: 12px;
  }
}
</style>
