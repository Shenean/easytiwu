// src/composables/useStatistics.ts
import { ref } from 'vue'
import { statisticsAPI } from '../api/config'
import type { StatisticsOverview, ApiResponse } from '../types/statistics'

export function useStatistics() {
  const stats = ref<StatisticsOverview | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetch = async () => {
    loading.value = true
    error.value = null
    try {
      const res = await statisticsAPI.getOverview()
      // 兼容不同后端格式：优先用 data.data 或 data
      const body = (res.data && res.data.data) ? res.data : res.data
      const api = body as ApiResponse<StatisticsOverview>

      if (api?.success && api.data) {
        stats.value = api.data
      } else {
        // 如果后端直接返回对象（非标准包裹），尝试直接赋值
        if (res.data && typeof res.data.bankTotal === 'number') {
          stats.value = res.data as StatisticsOverview
        } else {
          throw new Error(api?.message || '获取统计数据失败')
        }
      }
    } catch (e: any) {
      // 简单错误归一
      if (e.response?.status === 404) error.value = '统计服务不可用（404）'
      else if (e.response?.status === 500) error.value = '服务器内部错误'
      else if (e.message?.includes('Network Error') || e.code === 'ECONNREFUSED') error.value = '无法连接到服务器'
      else error.value = e.message || '获取统计数据失败'
    } finally {
      loading.value = false
    }
  }

  // 自动首次加载可选
  // void fetch()

  return {
    stats,
    loading,
    error,
    refresh: fetch
  }
}
