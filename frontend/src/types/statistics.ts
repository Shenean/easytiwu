/**
 * 统计相关的数据类型定义
 * 与后端 StatisticsDTO 保持一致
 */

/**
 * 题目类型统计数据
 */
export interface QuestionTypeStats {
  /** 该类型题目总数 */
  count: number
  /** 已完成题目数 */
  completedCount: number
  /** 正确题目数 */
  correctCount: number
}

/**
 * 统计数据概览
 */
export interface StatisticsOverview {
  /** 题库总数 */
  bankTotal: number
  /** 题目总数 */
  questionTotal: number
  /** 按题目类型统计 */
  byType: Record<string, QuestionTypeStats>
}

/**
 * API响应格式
 */
export interface ApiResponse<T> {
  /** 响应码 */
  code: number
  /** 响应消息 */
  message: string
  /** 响应数据 */
  data: T
  /** 是否成功 */
  success: boolean
}

/**
 * 统计数据表格行
 */
export interface StatisticsTableRow {
  /** 题型名称 */
  type: string
  /** 总题数 */
  count: number
  /** 已完成 */
  completedCount: number
  /** 答对数 */
  correctCount: number
}