import axios from 'axios'
import type { AxiosError } from 'axios'
import { getToken, clearAuth } from '@/utils/auth'

// 后端统一响应结构
export interface ApiResult<T = null> {
  code: number
  msg: string
  data: T
}

// 统一请求实例：自动附带 token，401 时清理凭证并跳转登录页
const request = axios.create({
  timeout: 10000,
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截：注入 JWT 令牌
request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) config.headers.token = token
  return config
})

// 响应拦截：未登录(401)时跳转登录页
request.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      clearAuth()
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = `/login?redirect=${redirect}`
      }
    }
    return Promise.reject(error)
  },
)

export default request