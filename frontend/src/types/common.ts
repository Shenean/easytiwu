export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
  success?: boolean;
}

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

export type AnswerValue = string | number | boolean | (string | number)[] | null;

export interface AnswerVerificationResponse {
  isCorrect: boolean;
  correctAnswer: string;
  analysis: string;
  message: string;
  questionId: number;
  userAnswer: string;
}

export interface QuestionBank {
  id: number;
  name: string;
  description: string;
  totalCount: number;
  completedCount: number;
  wrongCount: number;
}