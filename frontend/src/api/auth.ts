import api from "./config";

export interface UserLoginDTO {
  email: string;
  password: string;
}

export interface UserRegisterDTO {
  username: string;
  email: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  isActive: number;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  code: number;
  message: string;
  data: User | null;
  success: boolean;
}

export const authAPI = {
  login: (data: UserLoginDTO) => {
    return api.post<AuthResponse>("/api/v1/auth/login", data);
  },

  register: (data: UserRegisterDTO) => {
    return api.post<AuthResponse>("/api/v1/auth/register", data);
  },
};
