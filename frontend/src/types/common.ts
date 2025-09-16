/**
 * @fileoverview 通用类型定义模块
 * 
 * 定义了整个应用程序中使用的核心数据类型和接口。
 * 包含 API 响应格式、题目数据结构、题库信息等关键类型定义。
 * 
 * @author EasyTiwu Team
 * @version 1.0.0
 * @since 2024
 * 
 * @example
 * ```typescript
 * import type { ApiResponse, Question, QuestionBank } from '@/types/common'
 * 
 * // 使用 API 响应类型
 * const response: ApiResponse<Question[]> = await api.get('/questions')
 * 
 * // 使用题目类型
 * const question: Question = {
 *   id: 1,
 *   content: '1+1等于几？',
 *   type: 'single',
 *   // ...
 * }
 * ```
 */

/**
 * 统一的 API 响应格式接口
 * 
 * 所有后端 API 都应该遵循这个响应格式，确保前端能够统一处理响应数据。
 * 支持泛型，可以指定具体的数据类型。
 * 
 * @template T - 响应数据的具体类型
 * 
 * @interface ApiResponse
 * @property {number} code - 响应状态码（200表示成功，其他表示错误）
 * @property {string} message - 响应消息，成功或错误的描述信息
 * @property {T} data - 响应数据，具体类型由泛型 T 决定
 * @property {number} timestamp - 响应时间戳，服务器生成响应的时间
 * @property {boolean} [success] - 可选的成功标识，用于快速判断请求是否成功
 * 
 * @example
 * ```typescript
 * // 题库列表响应
 * const banksResponse: ApiResponse<QuestionBank[]> = {
 *   code: 200,
 *   message: '获取成功',
 *   data: [{ id: 1, name: '数学题库', ... }],
 *   timestamp: 1640995200000,
 *   success: true
 * }
 * 
 * // 错误响应
 * const errorResponse: ApiResponse<null> = {
 *   code: 404,
 *   message: '题库不存在',
 *   data: null,
 *   timestamp: 1640995200000,
 *   success: false
 * }
 * ```
 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
  success?: boolean;
}

/**
 * 题目数据接口定义
 * 
 * 定义了题目的完整数据结构，包含题目内容、类型、选项、答案等信息。
 * 支持多种题目类型，适用于各种考试和练习场景。
 * 
 * @interface Question
 * @property {number} id - 题目唯一标识符
 * @property {string} content - 题目内容/题干
 * @property {'single' | 'multiple' | 'true_false' | 'fill_blank' | 'short_answer'} type - 题目类型
 * @property {{ label: string; text: string }[]} options - 题目选项数组（选择题使用）
 * @property {string | null} userAnswer - 用户答案，null表示未作答
 * @property {string} correctAnswer - 正确答案
 * @property {string} analysis - 题目解析/解答说明
 * @property {number} isCompleted - 是否已完成（0: 未完成, 1: 已完成）
 * @property {number | null} isCorrect - 是否正确（0: 错误, 1: 正确, null: 未判断）
 * 
 * @example
 * ```typescript
 * // 单选题示例
 * const singleQuestion: Question = {
 *   id: 1,
 *   content: '以下哪个是 JavaScript 的数据类型？',
 *   type: 'single',
 *   options: [
 *     { label: 'A', text: 'string' },
 *     { label: 'B', text: 'integer' },
 *     { label: 'C', text: 'float' },
 *     { label: 'D', text: 'char' }
 *   ],
 *   userAnswer: 'A',
 *   correctAnswer: 'A',
 *   analysis: 'JavaScript 中 string 是基本数据类型之一',
 *   isCompleted: 1,
 *   isCorrect: 1
 * }
 * 
 * // 填空题示例
 * const fillBlankQuestion: Question = {
 *   id: 2,
 *   content: 'JavaScript 中声明变量使用 _____ 关键字',
 *   type: 'fill_blank',
 *   options: [],
 *   userAnswer: 'var',
 *   correctAnswer: 'var|let|const',
 *   analysis: 'JavaScript 中可以使用 var、let 或 const 声明变量',
 *   isCompleted: 1,
 *   isCorrect: 1
 * }
 * ```
 */
export interface Question {
  id: number;
  content: string;
  type: 'single' | 'multiple' | 'true_false' | 'fill_blank' | 'short_answer';
  options: { label: string; text: string }[];
  userAnswer: string | null;
  correctAnswer: string;
  analysis: string;
  isCompleted: number;
  isCorrect: number | null;
}

/**
 * 答案值联合类型
 * 
 * 定义了所有可能的答案值类型，适用于不同类型的题目。
 * 支持字符串、数字、布尔值、数组等多种数据类型。
 * 
 * @typedef {string | number | boolean | (string | number)[] | null} AnswerValue
 * 
 * @example
 * ```typescript
 * // 单选题答案（字符串）
 * const singleAnswer: AnswerValue = 'A'
 * 
 * // 多选题答案（字符串数组）
 * const multipleAnswer: AnswerValue = ['A', 'C', 'D']
 * 
 * // 判断题答案（布尔值）
 * const trueFalseAnswer: AnswerValue = true
 * 
 * // 填空题答案（字符串）
 * const fillBlankAnswer: AnswerValue = '答案内容'
 * 
 * // 未作答
 * const noAnswer: AnswerValue = null
 * ```
 */
export type AnswerValue = string | number | boolean | (string | number)[] | null;

/**
 * 答案验证响应接口
 * 
 * 定义了答案验证 API 的响应数据结构。
 * 包含验证结果、正确答案、解析等信息。
 * 
 * @interface AnswerVerificationResponse
 * @property {boolean} isCorrect - 答案是否正确
 * @property {string} correctAnswer - 正确答案
 * @property {string} analysis - 题目解析
 * @property {string} message - 验证结果消息
 * @property {number} questionId - 题目ID
 * @property {string} userAnswer - 用户提交的答案
 * 
 * @example
 * ```typescript
 * // 正确答案的验证响应
 * const correctResponse: AnswerVerificationResponse = {
 *   isCorrect: true,
 *   correctAnswer: 'A',
 *   analysis: '这是正确答案的解析',
 *   message: '回答正确！',
 *   questionId: 123,
 *   userAnswer: 'A'
 * }
 * 
 * // 错误答案的验证响应
 * const incorrectResponse: AnswerVerificationResponse = {
 *   isCorrect: false,
 *   correctAnswer: 'B',
 *   analysis: '正确答案是B，因为...',
 *   message: '回答错误，请查看解析',
 *   questionId: 123,
 *   userAnswer: 'A'
 * }
 * ```
 */
export interface AnswerVerificationResponse {
  isCorrect: boolean;
  correctAnswer: string;
  analysis: string;
  message: string;
  questionId: number;
  userAnswer: string;
}

/**
 * 题库数据接口定义
 * 
 * 定义了题库的基本信息和统计数据。
 * 包含题库名称、描述、题目统计等关键信息。
 * 
 * @interface QuestionBank
 * @property {number} id - 题库唯一标识符
 * @property {string} name - 题库名称
 * @property {string} description - 题库描述
 * @property {number} totalCount - 题库中题目总数
 * @property {number} completedCount - 已完成的题目数量
 * @property {number} wrongCount - 答错的题目数量
 * 
 * @example
 * ```typescript
 * const mathBank: QuestionBank = {
 *   id: 1,
 *   name: '高等数学题库',
 *   description: '包含微积分、线性代数等内容',
 *   totalCount: 500,
 *   completedCount: 120,
 *   wrongCount: 25
 * }
 * 
 * // 计算正确率
 * const accuracy = (mathBank.completedCount - mathBank.wrongCount) / mathBank.completedCount
 * console.log(`正确率: ${(accuracy * 100).toFixed(1)}%`)
 * ```
 */
export interface QuestionBank {
  id: number;
  name: string;
  description: string;
  totalCount: number;
  completedCount: number;
  wrongCount: number;
}