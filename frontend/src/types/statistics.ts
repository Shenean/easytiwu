/**
 * 统计数据相关类型定义模块
 * 
 * 本模块定义了统计页面和统计功能相关的TypeScript类型接口，
 * 包括题型统计、统计概览、统计表格行等数据结构。
 * 
 * @module StatisticsTypes
 * @author EasyTiWu Team
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * import { StatisticsOverview, TypeStats } from '@/types/statistics'
 * 
 * const overview: StatisticsOverview = {
 *   bankTotal: 10,
 *   questionTotal: 500,
 *   byType: {
 *     'single': { count: 200, completedCount: 150, correctCount: 120 },
 *     'multiple': { count: 300, completedCount: 200, correctCount: 160 }
 *   }
 * }
 * ```
 */

/**
 * 题型统计数据接口
 * 
 * 定义单个题型的统计信息，包括题目总数、已完成数量和正确数量。
 * 所有字段都是可选的，以支持部分数据加载的场景。
 * 
 * @interface TypeStats
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * const singleChoiceStats: TypeStats = {
 *   count: 100,
 *   completedCount: 80,
 *   correctCount: 65
 * }
 * 
 * // 部分数据场景
 * const partialStats: TypeStats = {
 *   count: 50
 *   // completedCount 和 correctCount 暂未加载
 * }
 * ```
 */
export interface TypeStats {
  /** 该题型的题目总数 */
  count?: number
  /** 该题型已完成的题目数量 */
  completedCount?: number
  /** 该题型答对的题目数量 */
  correctCount?: number
}

/**
 * 统计概览数据接口
 * 
 * 定义统计页面的整体概览数据结构，包括题库总数、题目总数
 * 以及按题型分组的详细统计信息。
 * 
 * @interface StatisticsOverview
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * const overview: StatisticsOverview = {
 *   bankTotal: 5,
 *   questionTotal: 1000,
 *   byType: {
 *     'single': { count: 400, completedCount: 300, correctCount: 250 },
 *     'multiple': { count: 350, completedCount: 200, correctCount: 150 },
 *     'judge': { count: 250, completedCount: 180, correctCount: 160 }
 *   }
 * }
 * ```
 */
export interface StatisticsOverview {
  /** 题库总数 */
  bankTotal: number
  /** 题目总数 */
  questionTotal: number
  /** 按题型分组的统计数据，键为题型标识，值为对应的统计信息 */
  byType: Record<string, TypeStats>
}

/**
 * 统计表格行数据接口
 * 
 * 定义统计表格中单行数据的结构，用于在表格组件中展示
 * 各题型的统计信息。与TypeStats不同，这里的字段都是必需的。
 * 
 * @interface StatisticsTableRow
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * const tableRows: StatisticsTableRow[] = [
 *   {
 *     type: '单选题',
 *     count: 200,
 *     completedCount: 150,
 *     correctCount: 120
 *   },
 *   {
 *     type: '多选题',
 *     count: 180,
 *     completedCount: 100,
 *     correctCount: 75
 *   }
 * ]
 * ```
 */
export interface StatisticsTableRow {
  /** 题型名称（用于显示） */
  type: string
  /** 题目总数 */
  count: number
  /** 已完成题目数量 */
  completedCount: number
  /** 答对题目数量 */
  correctCount: number
}

// ApiResponse 接口已移至 common.ts 统一管理
export type { ApiResponse } from './common'
