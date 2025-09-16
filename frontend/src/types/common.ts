// 通用类型定义文件

/**
 * 统一的 API 响应格式
 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
  success?: boolean;
}

/**
 * 题目接口定义
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
 * 答案值类型
 */
export type AnswerValue = string | number | boolean | (string | number)[] | null;

/**
 * 答案验证响应
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
 * 题库接口定义
 */
export interface QuestionBank {
  id: number;
  name: string;
  description: string;
  totalCount: number;
  completedCount: number;
  wrongCount: number;
}