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

export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  code?: number
}
