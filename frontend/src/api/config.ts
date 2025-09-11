import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    console.log('发送请求:', config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    console.log('收到响应:', response.status, response.config.url)
    return response
  },
  (error) => {
    console.error('响应错误:', error.response?.status, error.response?.data || error.message)
    
    // 统一错误处理
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      switch (status) {
        case 400:
          console.error('请求参数错误:', data)
          break
        case 401:
          console.error('未授权访问')
          break
        case 403:
          console.error('禁止访问')
          break
        case 404:
          console.error('请求的资源不存在')
          // 添加更详细的404错误信息
          if (error.config) {
            console.error('请求URL:', error.config.baseURL + error.config.url)
            console.error('请求方法:', error.config.method?.toUpperCase())
          }
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(`未知错误状态码: ${status}`)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络错误或服务器无响应')
    } else {
      // 其他错误
      console.error('请求配置错误:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default api

// 导出常用的API方法
export const uploadAPI = {
  // 上传文件
  uploadFile: (formData: FormData) => {
    return api.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000 // 文件上传延长超时时间到60秒
    })
  }
}

export const bankAPI = {
  // 银行相关API
  getBankList: () => api.get('/bank/list'),
  getBankDetail: (id: string) => api.get(`/bank/${id}`)
}

export const contentAPI = {
  // 内容相关API
  getContentList: () => api.get('/content/list'),
  getContentDetail: (id: string) => api.get(`/content/${id}`),
  
  // 题目查询
  getQuestions: (bankId: number, type: string) => {
    return api.post('/content/questions', { bankId, type })
  },
  
  // 答案验证
  verifyAnswer: (questionId: number, userAnswer: string) => {
    return api.post('/content/verify-answer', { questionId, userAnswer })
  }
}