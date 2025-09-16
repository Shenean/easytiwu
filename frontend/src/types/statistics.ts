// src/types/statistics.ts
export interface TypeStats {
  count?: number
  completedCount?: number
  correctCount?: number
}

export interface StatisticsOverview {
  bankTotal: number
  questionTotal: number
  byType: Record<string, TypeStats>
}

export interface StatisticsTableRow {
  type: string
  count: number
  completedCount: number
  correctCount: number
}

// ApiResponse 接口已移至 common.ts 统一管理
export type { ApiResponse } from './common'
