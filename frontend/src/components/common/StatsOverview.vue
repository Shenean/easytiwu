<!-- src/components/common/StatsOverview.vue -->
<template>
  <n-space justify="center" align="center" size="large" v-if="stats">
    <n-statistic label="题库总数" :value="formatNumber(stats.bankTotal)" />
    <n-statistic label="题目总数" :value="formatNumber(stats.questionTotal)" />
  </n-space>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { formatNumber } from '../../utils/number'
import type { StatisticsOverview } from '../../types/statistics'

// 允许传入 null，父组件渲染时先判断非空再使用也更安全
const props = defineProps<{ stats: StatisticsOverview | null }>()
const stats = props.stats
</script>

<style scoped>
:deep(.n-statistic-value) {
  font-weight: 600;
  color: #1890ff;
}
</style>
