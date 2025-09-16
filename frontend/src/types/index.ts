/**
 * 类型定义统一导出模块
 * 
 * 本模块作为类型定义的统一入口，将所有子模块的类型定义重新导出，
 * 方便其他模块进行统一导入，避免分散的导入语句。
 * 
 * ## 导出模块说明
 * - **common**: 通用类型定义，包括 API 响应、题目、题库等核心数据结构
 * - **statistics**: 统计相关类型定义，包括统计概览、题型统计等
 * - **global**: 全局类型声明，扩展 Vue 组件实例的类型定义
 * 
 * @module TypesIndex
 * @author EasyTiWu Team
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 推荐的导入方式：从统一入口导入
 * import type { 
 *   ApiResponse, 
 *   Question, 
 *   QuestionBank, 
 *   StatisticsOverview,
 *   TypeStats 
 * } from '@/types'
 * 
 * // 避免分散导入
 * // import type { ApiResponse } from '@/types/common'
 * // import type { StatisticsOverview } from '@/types/statistics'
 * 
 * // 使用导入的类型
 * const response: ApiResponse<Question[]> = await fetchQuestions()
 * const overview: StatisticsOverview = await fetchStatistics()
 * ```
 */

// 导出通用类型定义（API 响应、题目、题库等核心数据结构）
export * from './common'

// 导出统计相关类型定义（统计概览、题型统计、表格行数据等）
export * from './statistics'

// 导出全局类型声明（Vue 组件实例类型扩展）
export * from './global'