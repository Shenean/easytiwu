<template>
  <n-data-table :columns="columns" :data="rows" size="small" :bordered="false" />
</template>

<script setup lang="ts">
import type { StatisticsTableRow } from '../../types/statistics'
import { formatNumber, formatPercentage } from '../../utils/number'
import type { DataTableColumns } from 'naive-ui'

const props = defineProps<{ rows: StatisticsTableRow[] }>()
const rows = props.rows || []

const columns = [
  { title: '题型', key: 'type' },
  { title: '总题数', key: 'count', render: (r: StatisticsTableRow) => formatNumber(r.count) },
  { title: '已完成', key: 'completedCount', render: (r: StatisticsTableRow) => formatNumber(r.completedCount) },
  { title: '答对数', key: 'correctCount', render: (r: StatisticsTableRow) => formatNumber(r.correctCount) },
  { title: '正确率', key: 'accuracy', render: (r: StatisticsTableRow) => formatPercentage(r.correctCount, r.completedCount) }
] as DataTableColumns<StatisticsTableRow>
</script>
