/**
 * @fileoverview HTTP客户端配置模块
 * 
 * 基于 Axios 构建的企业级 HTTP 客户端，提供统一的 API 请求管理。
 * 包含请求/响应拦截器、错误处理、超时配置等核心功能。
 * 
 * @author EasyTiwu Team
 * @version 1.0.0
 * @since 2024
 * 
 * @example
 * ```typescript
 * import api, { bankAPI, contentAPI } from '@/api/config'
 * 
 * // 使用基础 API 实例
 * const response = await api.get('/custom/endpoint')
 * 
 * // 使用预定义的 API 模块
 * const banks = await bankAPI.getBankList()
 * const questions = await contentAPI.getQuestions(1, 'single')
 * ```
 */

import axios from 'axios'

/**
 * 主要的 Axios 实例配置
 * 
 * 配置了基础 URL、超时时间和默认请求头，
 * 所有 API 请求都基于此实例进行。
 * 
 * @constant {AxiosInstance} api - 配置好的 Axios 实例
 */
const api = axios.create({
  /** API 基础路径 */
  baseURL: '/api',
  /** 请求超时时间（30秒） */
  timeout: 30000,
  /** 默认请求头 */
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * 
 * 在每个请求发送前执行，用于：
 * - 记录请求日志
 * - 添加认证信息（如需要）
 * - 请求参数预处理
 * 
 * @param {AxiosRequestConfig} config - 请求配置对象
 * @returns {AxiosRequestConfig} 处理后的请求配置
 */
api.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 
 * 在每个响应返回后执行，用于：
 * - 统一错误处理
 * - 响应数据格式化
 * - 状态码处理
 * - 错误信息提取
 * 
 * @param {AxiosResponse} response - 成功响应对象
 * @param {AxiosError} error - 错误响应对象
 * @returns {Promise} 处理后的响应或拒绝的 Promise
 */
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 统一错误处理逻辑
    if (error.response) {
      // 服务器返回了错误状态码
      const { status } = error.response
      // 静默处理各种HTTP状态码错误
      switch (status) {
        case 400:
        case 401:
        case 403:
        case 404:
        case 500:
        default:
          // 静默处理错误
          break
      }
    }

    // 将错误继续向上抛出，供调用方处理
    return Promise.reject(error)
  }
)

/** 导出默认的 Axios 实例，供自定义 API 调用使用 */
export default api

/**
 * 文件上传相关 API 模块
 * 
 * 提供文件上传功能，支持多种文件格式。
 * 针对文件上传场景优化了超时时间和请求头配置。
 * 
 * @namespace uploadAPI
 * @example
 * ```typescript
 * import { uploadAPI } from '@/api/config'
 * 
 * const formData = new FormData()
 * formData.append('file', file)
 * formData.append('bankName', '题库名称')
 * 
 * const response = await uploadAPI.uploadFile(formData)
 * ```
 */
export const uploadAPI = {
  /**
   * 上传文件到服务器
   * 
   * @param {FormData} formData - 包含文件和其他表单数据的 FormData 对象
   * @returns {Promise<AxiosResponse>} 上传结果响应
   * 
   * @example
   * ```typescript
   * const formData = new FormData()
   * formData.append('file', selectedFile)
   * formData.append('bankName', '数学题库')
   * 
   * try {
   *   const result = await uploadAPI.uploadFile(formData)
   *   console.log('上传成功:', result.data)
   * } catch (error) {
   *   console.error('上传失败:', error)
   * }
   * ```
   */
  uploadFile: (formData: FormData) => {
    return api.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000 // 文件上传延长超时时间到60秒
    })
  }
}

/**
 * 题库管理相关 API 模块
 * 
 * 提供题库的基础 CRUD 操作，包括获取题库列表和详情。
 * 
 * @namespace bankAPI
 * @example
 * ```typescript
 * import { bankAPI } from '@/api/config'
 * 
 * // 获取所有题库
 * const banks = await bankAPI.getBankList()
 * 
 * // 获取特定题库详情
 * const bankDetail = await bankAPI.getBankDetail('123')
 * ```
 */
export const bankAPI = {
  /**
   * 获取题库列表
   * 
   * @returns {Promise<AxiosResponse>} 题库列表响应
   * 
   * @example
   * ```typescript
   * const response = await bankAPI.getBankList()
   * const banks = response.data.data // 题库数组
   * ```
   */
  getBankList: () => api.get('/bank/list'),
  
  /**
   * 获取指定题库的详细信息
   * 
   * @param {string} id - 题库ID
   * @returns {Promise<AxiosResponse>} 题库详情响应
   * 
   * @example
   * ```typescript
   * const response = await bankAPI.getBankDetail('123')
   * const bankInfo = response.data.data
   * ```
   */
  getBankDetail: (id: string) => api.get(`/bank/${id}`)
}

/**
 * 内容管理相关 API 模块
 * 
 * 提供题目内容的查询、验证等功能，是核心的业务 API 模块。
 * 包含题目获取、答案验证等关键功能。
 * 
 * @namespace contentAPI
 * @example
 * ```typescript
 * import { contentAPI } from '@/api/config'
 * 
 * // 获取题目列表
 * const questions = await contentAPI.getQuestions(1, 'single')
 * 
 * // 验证答案
 * const result = await contentAPI.verifyAnswer(123, 'A')
 * ```
 */
export const contentAPI = {
  /**
   * 获取内容列表
   * 
   * @returns {Promise<AxiosResponse>} 内容列表响应
   */
  getContentList: () => api.get('/content/list'),
  
  /**
   * 获取指定内容的详细信息
   * 
   * @param {string} id - 内容ID
   * @returns {Promise<AxiosResponse>} 内容详情响应
   */
  getContentDetail: (id: string) => api.get(`/content/${id}`),

  /**
   * 根据题库ID和题目类型获取题目列表
   * 
   * @param {number} bankId - 题库ID
   * @param {string} type - 题目类型（single/multiple/true_false/fill_blank/short_answer）
   * @returns {Promise<AxiosResponse>} 题目列表响应
   * 
   * @example
   * ```typescript
   * // 获取单选题
   * const singleQuestions = await contentAPI.getQuestions(1, 'single')
   * 
   * // 获取多选题
   * const multipleQuestions = await contentAPI.getQuestions(1, 'multiple')
   * ```
   */
  getQuestions: (bankId: number, type: string) => {
    return api.post('/content/questions', { bankId, type })
  },

  /**
   * 验证用户答案是否正确
   * 
   * @param {number} questionId - 题目ID
   * @param {string} userAnswer - 用户答案
   * @returns {Promise<AxiosResponse>} 验证结果响应
   * 
   * @example
   * ```typescript
   * try {
   *   const result = await contentAPI.verifyAnswer(123, 'A')
   *   if (result.data.data.isCorrect) {
   *     console.log('答案正确！')
   *   } else {
   *     console.log('答案错误，正确答案是:', result.data.data.correctAnswer)
   *   }
   * } catch (error) {
   *   console.error('验证失败:', error)
   * }
   * ```
   */
  verifyAnswer: (questionId: number, userAnswer: string) => {
    return api.post('/content/verify-answer', { questionId, userAnswer })
  }
}

/**
 * 统计数据相关 API 模块
 * 
 * 提供系统统计信息和健康检查功能。
 * 用于监控系统状态和获取数据分析结果。
 * 
 * @namespace statisticsAPI
 * @example
 * ```typescript
 * import { statisticsAPI } from '@/api/config'
 * 
 * // 获取统计概览
 * const overview = await statisticsAPI.getOverview()
 * 
 * // 健康检查
 * const health = await statisticsAPI.healthCheck()
 * ```
 */
export const statisticsAPI = {
  /**
   * 获取统计数据概览
   * 
   * 包含题库数量、题目统计、用户活跃度等关键指标。
   * 
   * @returns {Promise<AxiosResponse>} 统计概览响应
   * 
   * @example
   * ```typescript
   * const response = await statisticsAPI.getOverview()
   * const stats = response.data.data
   * console.log('总题库数:', stats.totalBanks)
   * console.log('总题目数:', stats.totalQuestions)
   * ```
   */
  getOverview: () => api.get('/statistics/overview'),
  
  /**
   * 系统健康检查
   * 
   * 检查服务器状态和各个服务模块的运行情况。
   * 
   * @returns {Promise<AxiosResponse>} 健康检查响应
   * 
   * @example
   * ```typescript
   * try {
   *   const health = await statisticsAPI.healthCheck()
   *   console.log('系统状态:', health.data.status)
   * } catch (error) {
   *   console.error('健康检查失败，系统可能存在问题')
   * }
   * ```
   */
  healthCheck: () => api.get('/statistics/health')
}