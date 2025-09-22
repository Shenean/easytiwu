import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  timeout: 30000,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status } = error.response;
      switch (status) {
        case 400:
        case 401:
        case 403:
        case 404:
        case 500:
        default:
          break;
      }
    }
    return Promise.reject(error);
  }
);

export default api;

export const uploadAPI = {
  uploadFile: (formData: FormData) => {
    return api.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      timeout: 60000,
    });
  },
};

export const bankAPI = {
  getBankList: () => api.get("/bank/list"),
  getBankDetail: (id: string) => api.get(`/bank/${id}`),
};

export const contentAPI = {
  getContentList: () => api.get("/content/list"),
  getContentDetail: (id: string) => api.get(`/content/${id}`),
  getQuestions: (bankId: number, type: string) => {
    // 如果type以"type:"开头，则使用新的题型练习接口
    if (type.startsWith("type:")) {
      const questionType = type.substring(5); // 移除"type:"前缀
      return api.post("/content/questions-by-type", { 
        bankId, 
        questionType 
      });
    }
    return api.post("/content/questions", { bankId, type });
  },
  verifyAnswer: (questionId: number, userAnswer: string) => {
    return api.post("/content/verify-answer", { questionId, userAnswer });
  },
};

export const statisticsAPI = {
  getOverview: () => api.get("/statistics/overview"),
  healthCheck: () => api.get("/statistics/health"),
};